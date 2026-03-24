# Full-Stack Authentication System

A production-oriented authentication and authorization platform built with Spring Boot, React, PostgreSQL, and Redis.

The project covers the full account lifecycle: registration, OTP-based email verification, login, token refresh, logout, password reset, password change, social login, protected user APIs, and admin reporting. It is structured like a real application rather than a demo-only repo: backend responsibilities are layered cleanly, security concerns are centralized, and the frontend integrates with the backend using a consistent auth/session model.

Live demo: `https://authsystem-plum.vercel.app/`

## What This Project Includes

- Email/password registration with OTP verification and resend flow
- JWT access tokens with refresh-token rotation
- HttpOnly refresh cookie handling
- Password reset and authenticated password change
- OAuth2 login for Google, GitHub, Apple, and LinkedIn
- Role-based access control for `ROLE_USER` and `ROLE_ADMIN`
- Redis-backed rate limiting and temporary lockout protection
- Admin dashboard with filtering, pagination, and sorting
- Docker-based local environment for backend, frontend, PostgreSQL, and Redis

## Technology Stack

| Layer | Technology |
| --- | --- |
| Backend | Java 21, Spring Boot 3.5.10, Spring Security, Spring Data JPA |
| Frontend | React 19, React Router 7, Vite 7, Axios |
| Database | PostgreSQL 16 |
| Caching and protection | Redis 7 |
| Auth and tokens | JWT, OAuth2 Client, BCrypt, hashed refresh tokens |
| Email | Spring Mail, Thymeleaf templates |
| Mapping and boilerplate | MapStruct, Lombok |
| Containers | Docker, Docker Compose |

## Documentation Map

- Project overview: [README.md](./README.md)
- Frontend guide: [frontend/README.md](./frontend/README.md)
- Architecture handoff: [docs/architecture.md](./docs/architecture.md)
- Backend property reference: [backend/src/main/resources/application.properties.example](./backend/src/main/resources/application.properties.example)

## Repository Layout

```text
.
|-- backend/
|   |-- src/main/java/com/auth/
|   |   |-- config/
|   |   |-- controller/
|   |   |-- dto/
|   |   |-- entity/
|   |   |-- exception/
|   |   |-- mapper/
|   |   |-- repository/
|   |   |-- security/
|   |   |-- service/
|   |   `-- util/
|   |-- src/main/resources/
|   |-- src/test/
|   |-- docker-compose.yaml
|   `-- .env.example
|-- frontend/
|   |-- src/
|   |   |-- components/
|   |   |-- context/
|   |   |-- pages/
|   |   |-- services/
|   |   `-- utils/
|   |-- public/
|   |-- .env.example
|   `-- package.json
`-- docs/
    `-- architecture.md
```

## Architecture At A Glance

The backend follows a layered structure:

- `controller`: HTTP entry points and request validation
- `service`: application workflows and business rules
- `repository`: persistence boundaries
- `security`: JWT filter, cookie handling, OAuth2 handlers, and Spring Security integration
- `support` services: OTP generation, password policy, hashing, email delivery, rate limiting, and time abstraction

The frontend is route-driven and session-aware:

- access tokens are kept in memory
- refresh tokens are stored in an HttpOnly cookie
- Axios automatically retries protected requests after refresh
- auth state is coordinated centrally in `AuthContext`

If you want the deeper walkthrough, start with [docs/architecture.md](./docs/architecture.md).

## Security Model

This project intentionally uses a split-token design:

- access token: short-lived JWT used for API authorization
- refresh token: opaque random secret stored as a hash in the database

Important security choices:

- passwords are stored with BCrypt
- refresh tokens, OTPs, and reset tokens are stored hashed
- refresh tokens are rotated on use
- rate limiting and brute-force protection are enforced through Redis-backed counters and lockouts
- refresh tokens are sent through HttpOnly cookies, not JavaScript-readable storage
- controllers return a stable API wrapper and centralized error responses

## Core API Surface

Base URLs during local development:

- Backend API: `http://localhost:8080`
- Frontend UI: `http://localhost:5173`

### Auth

| Method | Endpoint | Purpose |
| --- | --- | --- |
| `POST` | `/api/v1/auth/register` | Register a new account |
| `POST` | `/api/v1/auth/verify-otp` | Verify email address |
| `POST` | `/api/v1/auth/login` | Authenticate and issue tokens |
| `POST` | `/api/v1/auth/refresh` | Rotate refresh token and issue new access token |
| `POST` | `/api/v1/auth/logout` | Revoke refresh token and clear cookie |
| `POST` | `/api/v1/auth/reset-password` | Request password reset email |
| `POST` | `/api/v1/auth/update-password` | Complete password reset |
| `POST` | `/api/v1/auth/resend-otp?email={email}` | Resend OTP |

### User

| Method | Endpoint | Access |
| --- | --- | --- |
| `GET` | `/api/v1/user/dashboard` | `ROLE_USER`, `ROLE_ADMIN` |
| `GET` | `/api/v1/user/profile` | `ROLE_USER`, `ROLE_ADMIN` |
| `POST` | `/api/v1/user/change-password` | `ROLE_USER`, `ROLE_ADMIN` |

### Admin

| Method | Endpoint | Access |
| --- | --- | --- |
| `GET` | `/api/v1/admin/dashboard` | `ROLE_ADMIN` |
| `GET` | `/api/v1/admin/users` | `ROLE_ADMIN` |

Admin user query parameters:

- `page`: zero-based page index
- `size`: page size, `1-100`
- `search`: name/email search term
- `enabled`: `true` or `false`
- `role`: `USER`, `ADMIN`, `ROLE_USER`, or `ROLE_ADMIN`
- `sortBy`: `id`, `name`, `email`, `enabled`, or `createdAt`
- `sortDir`: `asc` or `desc`

### OAuth2

| Endpoint | Purpose |
| --- | --- |
| `/oauth2/authorization/{provider}` | Start provider login |
| `/login/oauth2/code/{provider}` | Provider callback handled by Spring Security |

## Quick Start

### Prerequisites

- Java 21+
- Maven 3.9+
- Node.js 18+
- Docker and Docker Compose

### 1. Configure environment files

```bash
cd backend
cp .env.example .env

cd ../frontend
cp .env.example .env
```

Backend configuration lives primarily in `backend/.env`, with additional property defaults documented in [backend/src/main/resources/application.properties.example](./backend/src/main/resources/application.properties.example).

Frontend configuration uses only public `VITE_*` values. Do not place secrets in frontend environment files.

### 2. Run everything with Docker

```bash
cd backend
docker compose up --build -d postgres redis app frontend
```

Available services:

- Frontend: `http://localhost:5173`
- Backend API: `http://localhost:8080`
- Adminer: `http://localhost:5050`
- Redis Commander: `http://localhost:8081`

### 3. Run locally without containers

Start infrastructure:

```bash
cd backend
docker compose up -d postgres redis
```

Run the backend:

```bash
cd backend
mvn spring-boot:run
```

Run the frontend:

```bash
cd frontend
npm install
npm run dev
```

## Configuration Notes

### Backend

The most important `backend/.env` settings are:

- `SPRING_DATASOURCE_PASSWORD`
- `JWT_SECRET`
- `SPRING_MAIL_USERNAME`
- `SPRING_MAIL_PASSWORD`
- OAuth provider credentials for any enabled providers

Useful references:

- env-based setup: [backend/.env.example](./backend/.env.example)
- property-based setup: [backend/src/main/resources/application.properties.example](./backend/src/main/resources/application.properties.example)

### Frontend

The frontend needs only:

- `VITE_API_URL`
- `VITE_OAUTH_BASE_URL`

Reference: [frontend/.env.example](./frontend/.env.example)

## Development Workflow

### Backend

```bash
cd backend
mvn -q -DskipTests compile
mvn -q test
```

### Frontend

```bash
cd frontend
npm run lint
npm run build
```

## Local Auth Flow

The standard browser flow is:

1. User logs in or completes OAuth2 sign-in.
2. Backend returns an access token in the JSON response and a refresh token in an HttpOnly cookie.
3. Frontend keeps the access token in memory and attaches it to protected API calls.
4. When the access token expires, the frontend calls `/api/v1/auth/refresh`.
5. Backend validates the refresh token, rotates it, and returns a fresh access token.

This keeps the backend authorization path stateless while still supporting token revocation and safer long-lived sessions.

## Deployment Notes

- For HTTPS deployments, verify `AUTH_REFRESH_TOKEN_COOKIE_SECURE` and `AUTH_REFRESH_TOKEN_COOKIE_SAME_SITE`.
- Set explicit `APP_FRONTEND_URL`, `APP_BACKEND_URL`, and `APP_CORS_ALLOWED_ORIGINS` for your target environment.
- Use real SMTP credentials if you want OTP, welcome, lockout, and reset emails to work end to end.
- Enable seeded admin credentials only intentionally and only in controlled environments.

## Troubleshooting

### Backend starts but login/registration fails

Check:

- database connection settings
- Redis availability
- `JWT_SECRET` presence
- SMTP credentials if the flow depends on email delivery

### OAuth2 redirects but login does not complete

Check:

- provider client ID and secret
- backend redirect URI configuration
- frontend base URL and backend base URL alignment
- cookie security and same-site settings for your environment

### Protected frontend requests return `401`

Check:

- whether the refresh cookie is being set by the backend
- whether frontend and backend origins are included in allowed CORS origins
- whether the browser is rejecting cookies because of `Secure` or `SameSite` policy

## Who This Repository Is For

This repository is useful if you want to study or extend:

- secure auth architecture in Spring Boot
- refresh-token rotation with HttpOnly cookies
- a React SPA backed by a session-aware API layer
- production-style separation of controllers, services, repositories, security, and infrastructure

For a deeper handoff-quality system walkthrough, continue with [docs/architecture.md](./docs/architecture.md).
