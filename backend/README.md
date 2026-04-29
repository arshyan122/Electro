# Electro Backend

Node + Express + **MongoDB (Mongoose)** + JWT backend for the Electro user app and the Electro Technician app.

## Stack
- Node 18+
- Express 4
- **MongoDB** via [Mongoose 8](https://mongoosejs.com/) — works with MongoDB Atlas or self-hosted Mongo.
- `bcryptjs` for password hashing, `jsonwebtoken` for JWT issuance.
- **firebase-admin** for FCM push notifications (optional).
- Cloudinary signed-upload helper (optional, no SDK — uses crypto + the public REST API).

## Setup

```bash
cd backend
npm install
cp .env.example .env       # then edit
```

Required `.env` values:
```
PORT=8080
JWT_SECRET=<openssl rand -hex 32>
MONGODB_URI=mongodb+srv://USER:PASS@cluster.mongodb.net/electro?retryWrites=true&w=majority
```

Optional `.env` values:
```
# Firebase Cloud Messaging — paste the entire service-account JSON on a single line.
# If unset, push notifications are silently skipped.
FIREBASE_ADMIN_SDK_JSON={"type":"service_account",...}

# Cloudinary — required for /technician/uploads/sign to work.
CLOUDINARY_CLOUD_NAME=
CLOUDINARY_API_KEY=
CLOUDINARY_API_SECRET=
CLOUDINARY_UPLOAD_PRESET=
```

Then:
```bash
npm run seed   # idempotent upsert of 6 products + 8 services
npm start      # http://localhost:8080
```

For local dev with auto-restart:
```bash
npm run dev
```

## Roles

Every `User` document carries a `role` of either `user` (default) or `technician`. JWTs include the role claim, and the backend uses `requireRole(...)` middleware to gate role-specific endpoints.

- Customers sign up via `POST /auth/signup` and log in via `POST /auth/login`.
- Technicians sign up via `POST /technician/register` and log in via `POST /technician/login`. Logging into `/auth/login` with a technician email is rejected (the user app should never see technician accounts).

## Endpoints

### Auth (shared)
| Method | Path                | Auth        | Body / Notes                          |
| ------ | ------------------- | ----------- | ------------------------------------- |
| POST   | `/auth/signup`      | none        | `{ email, password, name }` — creates a `user` |
| POST   | `/auth/login`       | none        | `{ email, password }` — `user` only   |
| GET    | `/auth/me`          | Bearer      | returns `{ user }`                    |
| POST   | `/auth/fcm-token`   | Bearer      | `{ token }` — registers the device's FCM token |

### Products / Services catalogue (read-only)
| Method | Path                              | Auth |
| ------ | --------------------------------- | ---- |
| GET    | `/products`                       | none |
| GET    | `/products/:id`                   | none |
| GET    | `/products/category/:category`    | none |
| GET    | `/services`                       | none |
| GET    | `/services/:id`                   | none |
| GET    | `/services/category/:category`    | none |

### Technician
| Method | Path                                | Auth                | Notes |
| ------ | ----------------------------------- | ------------------- | ----- |
| POST   | `/technician/register`              | none                | `{ email, password, name, phone?, specialization?, experienceYears?, bio? }` |
| POST   | `/technician/login`                 | none                | `{ email, password }` — `technician` only |
| GET    | `/technician/me`                    | Bearer technician   | `{ user, profile }` |
| PATCH  | `/technician/me`                    | Bearer technician   | partial update of `name`, `phone`, `specialization`, `experienceYears`, `bio`, `profileImageUrl`, `available` |
| GET    | `/technician/services`              | Bearer technician   | technician's own service catalogue |
| POST   | `/technician/services`              | Bearer technician   | `{ name, category, price, description? }` |
| PATCH  | `/technician/services/:id`          | Bearer technician   | partial update |
| DELETE | `/technician/services/:id`          | Bearer technician   | |
| GET    | `/technician/dashboard`             | Bearer technician   | `{ openPending, activeAccepted, activeInProgress, completed, totalAssigned }` |
| POST   | `/technician/uploads/sign`          | Bearer              | returns `{ timestamp, signature, apiKey, cloudName, uploadPreset, folder }` |

### Service requests
| Method | Path                          | Auth                | Notes |
| ------ | ----------------------------- | ------------------- | ----- |
| POST   | `/requests`                   | Bearer user         | customer creates a service request |
| GET    | `/requests/mine`              | Bearer user         | customer's own requests, newest first |
| GET    | `/requests`                   | Bearer technician   | `?status=pending` (default), `mine`, `accepted`, `in_progress`, `completed`, `rejected`, `cancelled` |
| GET    | `/requests/:id`               | Bearer              | customer of the request, assigned technician, or any technician if `pending` |
| POST   | `/requests/:id/accept`        | Bearer technician   | atomic accept on first-come-first-served — second technician gets 409 |
| POST   | `/requests/:id/reject`        | Bearer technician   | adds the technician to `rejectedBy`; request stays pending for others |
| PATCH  | `/requests/:id/status`        | Bearer technician   | `{ status: 'in_progress' \| 'completed' }`, must own the request |
| POST   | `/requests/:id/cancel`        | Bearer user         | customer cancels their own pending/accepted request |

### `Request` shape
```json
{
  "id": "...",
  "customerId": "...",
  "technicianId": "..." or null,
  "category": "AC",
  "title": "AC not cooling",
  "description": "...",
  "address": "...",
  "price": 1500,
  "status": "pending|accepted|in_progress|completed|rejected|cancelled",
  "rejectedBy": ["..."],
  "createdAt": "ISO8601",
  "updatedAt": "ISO8601"
}
```

## Push notifications (FCM)

When `FIREBASE_ADMIN_SDK_JSON` is set, the backend pushes to:
- All technicians (best-effort fan-out) when a new `request` is created.
- The customer when their request is accepted or transitions in status.

Tokens are registered via `POST /auth/fcm-token` from each device. If `FIREBASE_ADMIN_SDK_JSON` is unset, the helper logs a warning at startup and silently skips pushes — the rest of the API continues to work.

## Cloudinary signed uploads

`POST /technician/uploads/sign` returns the params the client needs to upload directly to Cloudinary. The technician Android app posts the image bytes to `https://api.cloudinary.com/v1_1/<cloudName>/image/upload` with the returned signature, then `PATCH /technician/me` with the resulting secure URL.

If Cloudinary env vars are unset, the sign endpoint returns 503 and the rest of the API continues to work.

## Data model notes

- `users.email` is unique. `users.role` is indexed.
- `technicians.userId` is unique (1:1 with `users`).
- `requests.status`, `requests.customerId`, `requests.technicianId` are indexed for typical queries.
- `Product` and `Service` keep an integer `legacyId` so the existing user app's deserialization continues to work — `id` in the JSON is the legacy integer, not the Mongo ObjectId.

## Deploying

### Render
1. New → Web Service → connect this repo. Root directory: `backend/`.
2. Build: `npm install`. Start: `npm start`.
3. Env vars: `JWT_SECRET`, `MONGODB_URI` (URL-encode special chars in the password — `@` → `%40`). Optional: `FIREBASE_ADMIN_SDK_JSON`, `CLOUDINARY_*`. Render injects `PORT` automatically.

### Fly.io
```bash
flyctl launch --no-deploy
flyctl secrets set \
  JWT_SECRET="$(openssl rand -hex 32)" \
  MONGODB_URI="mongodb+srv://..."
flyctl deploy
```

### Atlas IP allowlist
Atlas blocks all IPs by default. While iterating, add `0.0.0.0/0` under **Network Access**, then lock it down to your hosting provider's egress range once deployed.

## Android clients
- User app: `app/build.gradle` `BASE_URL` build-config.
- Technician app: separate repo, same shape, same `BASE_URL` pattern.
