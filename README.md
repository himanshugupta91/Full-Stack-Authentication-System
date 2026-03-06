# 🔐 Full-Stack Auth System

<p align="center">
  <img src="https://img.shields.io/badge/A%20Secure%20Full--Stack-Authentication%20%26%20Authorization%20Platform-00C853?style=for-the-badge&labelColor=1a1a2e" alt="tagline" />
</p>

<p align="center">
  <img src="https://img.shields.io/badge/Spring%20Boot-6DB33F?style=for-the-badge&logo=springboot&logoColor=white" alt="Spring Boot" />
  <img src="https://img.shields.io/badge/React-61DAFB?style=for-the-badge&logo=react&logoColor=black" alt="React" />
  <img src="https://img.shields.io/badge/PostgreSQL-4169E1?style=for-the-badge&logo=postgresql&logoColor=white" alt="PostgreSQL" />
  <img src="https://img.shields.io/badge/Redis-DC382D?style=for-the-badge&logo=redis&logoColor=white" alt="Redis" />
  <img src="https://img.shields.io/badge/JWT-000000?style=for-the-badge&logo=jsonwebtokens&logoColor=white" alt="JWT" />
  <img src="https://img.shields.io/badge/Docker-2496ED?style=for-the-badge&logo=docker&logoColor=white" alt="Docker" />
</p>

<p align="center">
  <img src="https://img.shields.io/badge/Java-21-ED8B00?style=flat-square&logo=openjdk&logoColor=white" alt="Java 21" />
  <img src="https://img.shields.io/badge/Spring%20Security-6.x-6DB33F?style=flat-square&logo=springsecurity&logoColor=white" alt="Spring Security 6" />
  <img src="https://img.shields.io/badge/React-19-61DAFB?style=flat-square&logo=react&logoColor=black" alt="React 19" />
  <img src="https://img.shields.io/badge/Vite-5-646CFF?style=flat-square&logo=vite&logoColor=white" alt="Vite" />
  <img src="https://img.shields.io/badge/License-MIT-blue?style=flat-square" alt="License" />
</p>

<p align="center">
  <strong>Live Demo:</strong>
  <a href="https://authsystem-plum.vercel.app/">https://authsystem-plum.vercel.app/</a>
</p>

---

## <img src="https://img.shields.io/badge/📖-About%20the%20Project-1a1a2e?style=for-the-badge&labelColor=00C853" />

- Backend-focused authentication and authorization service built with Spring Boot
- Clean layered architecture: Controller, Service, Repository, Entity, DTO, and Security
- Covers core auth flows: signup, OTP verification, login, refresh, logout, password reset, and OAuth2 login
- Role-based access control enforced for `ROLE_USER` and `ROLE_ADMIN`
- Security-first design with refresh-token rotation, token hashing, Redis-backed rate limiting, and account lock protection
- Structured for production-grade backend practices and easy extensibility

---

## <img src="https://img.shields.io/badge/✨-Features-1a1a2e?style=for-the-badge&labelColor=6C63FF" />

- Email/password signup with OTP verification and resend flow
- JWT access token + HttpOnly refresh cookie with refresh-token rotation
- Password reset via email and authenticated password change
- OAuth2 login support: Google, GitHub, Apple, and LinkedIn
- Role-based access control (`ROLE_USER`, `ROLE_ADMIN`)
- Admin dashboard with user pagination, search, filtering, and sorting
- Redis-backed rate limiting and temporary lockout against abuse
- Token/OTP hash storage and centralized exception handling
- Timestamps exposed to UI in Indian timezone (`Asia/Kolkata`) using 12-hour format where applicable
- Dockerized deployment with separate backend and frontend containers
- Responsive React frontend with protected routes and theme support

---

## <img src="https://img.shields.io/badge/🛠️-Tech%20Stack-1a1a2e?style=for-the-badge&labelColor=FF6F00" />

| Layer | Technology |
| --- | --- |
| Language | Java 21 |
| Backend Framework | Spring Boot 3.5.10 |
| Security | Spring Security 6, JWT (jjwt 0.12.x), OAuth2 Client |
| Persistence | Spring Data JPA, Hibernate |
| Database | PostgreSQL 16 |
| Caching / Protection | Redis 7 |
| Validation | Jakarta Bean Validation |
| Email | Spring Mail |
| Mapping | MapStruct |
| Boilerplate Reduction | Lombok |
| Frontend | React 19, React Router 7, Vite, Axios |
| UI | Bootstrap 5, Bootstrap Icons |
| Web Server (Frontend Container) | Nginx (alpine) |
| Containers | Docker, Docker Compose (separate `app` + `frontend` services) |

---

## <img src="https://img.shields.io/badge/🔗-API%20Endpoints-1a1a2e?style=for-the-badge&labelColor=00BCD4" />

Base URLs:

- Backend API: `http://localhost:8080`
- Frontend UI: `http://localhost:5173`

### Auth APIs — `POST /api/v1/auth/*`

| # | Method | Endpoint | Description | Access |
|---|--------|----------|-------------|--------|
| 1 | POST | `/api/v1/auth/register` | Register a new user account | Public |
| 2 | POST | `/api/v1/auth/verify-otp` | Verify email with OTP code | Public |
| 3 | POST | `/api/v1/auth/login` | Login and receive JWT + refresh cookie | Public |
| 4 | POST | `/api/v1/auth/refresh` | Refresh access token (cookie or body) | Public |
| 5 | POST | `/api/v1/auth/logout` | Revoke refresh token and clear cookie | Public |
| 6 | POST | `/api/v1/auth/reset-password` | Request password reset email | Public |
| 7 | POST | `/api/v1/auth/update-password` | Set new password using reset token | Public |
| 8 | POST | `/api/v1/auth/resend-otp?email={email}` | Resend OTP with rate limiting | Public |

### User APIs — `/api/v1/user/*`

| # | Method | Endpoint | Description | Access |
|---|--------|----------|-------------|--------|
| 1 | GET | `/api/v1/user/dashboard` | Get user dashboard data | `ROLE_USER` or `ROLE_ADMIN` |
| 2 | GET | `/api/v1/user/profile` | Get current user profile | `ROLE_USER` or `ROLE_ADMIN` |
| 3 | POST | `/api/v1/user/change-password` | Change password (authenticated) | `ROLE_USER` or `ROLE_ADMIN` |

### Admin APIs — `/api/v1/admin/*`

| # | Method | Endpoint | Description | Access |
|---|--------|----------|-------------|--------|
| 1 | GET | `/api/v1/admin/dashboard` | Admin metrics (total users, active count) | `ROLE_ADMIN` |
| 2 | GET | `/api/v1/admin/users` | Paginated, searchable, filterable user list | `ROLE_ADMIN` |

Admin users query params:

- `page` default `0`
- `size` default `20`, max `100`
- `search` for name/email match
- `enabled` boolean filter
- `role` accepts `USER`, `ADMIN`, `ROLE_USER`, `ROLE_ADMIN`
- `sortBy` allows `id`, `name`, `email`, `enabled`, `createdAt`
- `sortDir` accepts `asc` or `desc`

### OAuth2 APIs — Spring Security Managed

| # | Method | Endpoint | Description | Access |
|---|--------|----------|-------------|--------|
| 1 | GET | `/oauth2/authorization/{provider}` | Start OAuth2 login flow (Google, GitHub, Apple, LinkedIn) | Public |
| 2 | GET | `/login/oauth2/code/{provider}` | OAuth2 callback (handled by Spring Security) | Public |

OAuth2 flow: Frontend redirects to `/oauth2/authorization/google` → user authenticates with provider → Spring Security handles callback → `OAuth2AuthenticationSuccessHandler` issues tokens + HttpOnly cookie → redirects to frontend with no token in URL → frontend calls `/api/v1/auth/refresh` to get access token.

Timestamp notes:

- Admin user list `createdAt` is returned in IST 12-hour format (`dd MMM yyyy, hh:mm:ss a`)
- Dashboard timestamps are emitted in IST 12-hour format

---

## <img src="https://img.shields.io/badge/🚀-Getting%20Started-1a1a2e?style=for-the-badge&labelColor=4CAF50" />

### Prerequisites

- Docker + Docker Compose (recommended)
- Java 21+, Maven 3.9+, Node.js 18+, npm 9+ (for local non-container dev)

### 1) Configure environment files

```bash
cd backend
cp .env.example .env
# edit backend/.env and set at least: SPRING_DATASOURCE_PASSWORD, JWT_SECRET

cd ../frontend
cp .env.example .env
```

Security note:

- Never commit `.env` files.
- Never put secrets in `VITE_*` variables (they are exposed to the browser).

### 2) Run with Docker (recommended)

```bash
cd backend
docker compose up --build -d postgres redis app frontend
```

URLs:

- Frontend: `http://localhost:5173`
- Backend API: `http://localhost:8080`
- Adminer: `http://localhost:5050`
- Redis Commander: `http://localhost:8081`

To rebuild app/frontend after code changes:

```bash
cd backend
docker compose up -d --build --force-recreate app frontend
```

### 3) Local dev mode (separate processes)

Run supporting services:

```bash
cd backend
docker compose up -d postgres redis
```

Run backend:

```bash
cd backend
mvn spring-boot:run
```

Run frontend:

```bash
cd frontend
npm install
npm run dev
```

Local URLs:

- Frontend: `http://localhost:5173`
- Backend API: `http://localhost:8080`

### 4) Optional build checks

```bash
cd backend
mvn -q -DskipTests compile
mvn -q test

cd ../frontend
npm run lint
npm run build
```

---
