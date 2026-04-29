# Electro Backend

Node + Express + **MongoDB (Mongoose)** + JWT backend for the Electro Android app.

## Stack
- Node 18+
- Express 4
- **MongoDB** via [Mongoose 8](https://mongoosejs.com/) — works with MongoDB Atlas or self-hosted Mongo.
- `bcryptjs` for password hashing, `jsonwebtoken` for JWT issuance.

## Setup

```bash
cd backend
npm install
cp .env.example .env       # then edit
```

`.env` must contain:
```
PORT=8080
JWT_SECRET=<a long random string, e.g. `openssl rand -hex 32`>
MONGODB_URI=mongodb+srv://USER:PASS@cluster.mongodb.net/electro?retryWrites=true&w=majority
```

Then:
```bash
npm run seed   # idempotent upsert of 6 products + 8 services into the DB
npm start      # http://localhost:8080
```

For local dev with auto-restart:
```bash
npm run dev
```

## Endpoints

### Auth
| Method | Path           | Body                                | Returns                |
| ------ | -------------- | ----------------------------------- | ---------------------- |
| POST   | `/auth/signup` | `{ email, password, name }`         | `{ token, user }`      |
| POST   | `/auth/login`  | `{ email, password }`               | `{ token, user }`      |
| GET    | `/auth/me`     | header `Authorization: Bearer ...`  | `{ user }`             |

### Products
| Method | Path                              | Returns                |
| ------ | --------------------------------- | ---------------------- |
| GET    | `/products`                       | `Product[]`            |
| GET    | `/products/:id`                   | `Product`              |
| GET    | `/products/category/:category`    | `Product[]`            |

`Product` shape (FakeStore-compatible — Android client uses it unchanged):
```json
{
  "id": 1,
  "title": "...",
  "price": 64.0,
  "description": "...",
  "category": "electronics",
  "image": "https://...",
  "rating": { "rate": 3.3, "count": 203 }
}
```

### Services
| Method | Path                              | Returns                |
| ------ | --------------------------------- | ---------------------- |
| GET    | `/services`                       | `Service[]`            |
| GET    | `/services/:id`                   | `Service`              |
| GET    | `/services/category/:category`    | `Service[]`            |

`Service` shape:
```json
{
  "id": 1,
  "name": "Electrical Wiring",
  "description": "...",
  "price": 1500.0,
  "icon": "https://...",
  "category": "wiring"
}
```

## Data model notes

The `id` field returned to clients is the integer `legacyId` carried over from the previous SQLite seed (so the Android client keeps working). `_id` (Mongo's ObjectId) is hidden from JSON responses by `toJSON` transforms in `src/models/*.js`.

Indexes: `users.email` is unique; `products.category` and `services.category` are indexed for the `/category/:category` filters.

## Deploying

### Fly.io
```bash
flyctl launch --no-deploy
flyctl secrets set \
  JWT_SECRET="$(openssl rand -hex 32)" \
  MONGODB_URI="mongodb+srv://..."
flyctl deploy
```

### Render / Railway
- Connect this repo, set root directory to `backend/`.
- Build: `npm install`
- Start: `npm start`
- Env vars: `JWT_SECRET`, `MONGODB_URI`. Render injects `PORT` automatically.

### Atlas IP allowlist
Atlas blocks all IPs by default. While iterating, add `0.0.0.0/0` under **Network Access**, then lock it down to your hosting provider's egress range once deployed.

## Updating the Android client

Set `BASE_URL` in `app/build.gradle` (`buildConfigField`) to your deployed URL (with trailing slash, e.g. `https://electro-backend.fly.dev/`). Debug builds default to `http://10.0.2.2:8080/` for emulator → host machine.
