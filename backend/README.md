# Electro Backend

Node + Express + SQLite + JWT backend for the Electro Android app.

## Stack
- Node 18+
- Express 4
- SQLite via `better-sqlite3` (single-file DB, no external service)
- `bcryptjs` for password hashing, `jsonwebtoken` for JWT issuance

## Setup
```bash
cd backend
npm install
cp .env.example .env       # then edit JWT_SECRET to a real random string
npm run seed               # creates ./electro.db and inserts sample data
npm start                  # http://localhost:8080
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

### Products (FakeStore-compatible shape)
| Method | Path                              | Returns                |
| ------ | --------------------------------- | ---------------------- |
| GET    | `/products`                       | `Product[]`            |
| GET    | `/products/:id`                   | `Product`              |
| GET    | `/products/category/:category`    | `Product[]`            |

`Product` shape:
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

### Services (Electro service catalog)
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

## Deploying

### Fly.io (recommended)
```bash
flyctl launch --no-deploy
# accept defaults; pick a region; choose internal port 8080
flyctl secrets set JWT_SECRET="$(openssl rand -hex 32)"
flyctl deploy
```

The included `Dockerfile` (root of `backend/`) already exposes `8080` and runs `npm start`.

### Render / Railway
- Connect this repo, set root directory to `backend/`.
- Build command: `npm install && npm run seed`
- Start command: `npm start`
- Env vars: `JWT_SECRET`, `PORT` (Render injects `PORT` automatically).

## Updating the Android client

Set `BASE_URL` in `app/src/main/java/com/example/electro/di/NetworkModule.kt` to your deployed URL (with trailing slash, e.g. `https://electro-backend.fly.dev/`).
