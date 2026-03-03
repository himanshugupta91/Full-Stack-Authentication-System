# 🔐 Matrix Auth System

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

---

## <img src="https://img.shields.io/badge/📖-About%20the%20Project-1a1a2e?style=for-the-badge&labelColor=00C853" />

Matrix Auth System is a full-stack authentication and authorization platform built for real-world user management use cases.
The backend is built with Spring Boot layered architecture (Controller, Service, Repository, Entity, DTO, Security).
The frontend is built with React 19 and Vite, with role-aware routing and token lifecycle handling.
The system supports email/password authentication, OTP verification, password reset, refresh-token rotation, and OAuth2 login.
Security and abuse protection are first-class concerns, implemented with Redis-backed rate limiting and account lock strategies.
This project is structured to be interview-ready, production-oriented, and easy to extend for enterprise features.

---

## <img src="https://img.shields.io/badge/✨-Features-1a1a2e?style=for-the-badge&labelColor=6C63FF" />

- User registration with email verification (OTP-based activation)
- Login with access token + HttpOnly refresh token cookie
- Refresh token rotation and logout token revocation
- Password reset flow with secure token handling
- Password change for authenticated users
- OAuth2 login (Google, GitHub, Apple, LinkedIn)
- RBAC authorization (`ROLE_USER`, `ROLE_ADMIN`)
- Admin dashboard and paginated/filterable/searchable user listing
- Brute-force protection and endpoint-specific rate limiting
- Token hashing for refresh/reset/OTP values (no plaintext storage)
- Global exception handling with consistent JSON error responses
- Layered Spring Boot backend design and reusable service contracts
- Responsive React frontend with Bootstrap and protected routes
- Accessibility/performance aware Matrix background animation

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
| Frontend | React 19, React Router 7, Vite |
| UI | Bootstrap 5, Bootstrap Icons |
| HTTP Client (Frontend) | Axios |
| Containers | Docker, Docker Compose |

---

## <img src="https://img.shields.io/badge/📸-Preview-1a1a2e?style=for-the-badge&labelColor=E91E63" />

<p align="center">
  <img src="screenshots/readme-preview.png" alt="Home Page" width="700" />
</p>

---

## <img src="https://img.shields.io/badge/🔗-API%20Endpoints-1a1a2e?style=for-the-badge&labelColor=00BCD4" />

Base URL: `http://localhost:8080`

### Auth APIs — `POST /api/auth/*`

| # | Method | Endpoint | Description | Access |
|---|--------|----------|-------------|--------|
| 1 | POST | `/api/auth/register` | Register a new user account | Public |
| 2 | POST | `/api/auth/verify-otp` | Verify email with OTP code | Public |
| 3 | POST | `/api/auth/login` | Login and receive JWT + refresh cookie | Public |
| 4 | POST | `/api/auth/refresh` | Refresh access token (cookie or body) | Public |
| 5 | POST | `/api/auth/logout` | Revoke refresh token and clear cookie | Public |
| 6 | POST | `/api/auth/reset-password` | Request password reset email | Public |
| 7 | POST | `/api/auth/update-password` | Set new password using reset token | Public |
| 8 | POST | `/api/auth/resend-otp?email={email}` | Resend OTP with rate limiting | Public |

### User APIs — `/api/user/*`

| # | Method | Endpoint | Description | Access |
|---|--------|----------|-------------|--------|
| 1 | GET | `/api/user/dashboard` | Get user dashboard data | `ROLE_USER` or `ROLE_ADMIN` |
| 2 | GET | `/api/user/profile` | Get current user profile | `ROLE_USER` or `ROLE_ADMIN` |
| 3 | POST | `/api/user/change-password` | Change password (authenticated) | `ROLE_USER` or `ROLE_ADMIN` |

### Admin APIs — `/api/admin/*`

| # | Method | Endpoint | Description | Access |
|---|--------|----------|-------------|--------|
| 1 | GET | `/api/admin/dashboard` | Admin metrics (total users, active count) | `ROLE_ADMIN` |
| 2 | GET | `/api/admin/users` | Paginated, searchable, filterable user list | `ROLE_ADMIN` |

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

OAuth2 flow: Frontend redirects to `/oauth2/authorization/google` → user authenticates with provider → Spring Security handles callback → `OAuth2AuthenticationSuccessHandler` issues tokens + HttpOnly cookie → redirects to frontend with no token in URL → frontend calls `/api/auth/refresh` to get access token.

---

## <img src="https://img.shields.io/badge/🚀-Getting%20Started-1a1a2e?style=for-the-badge&labelColor=4CAF50" />

### Prerequisites

- Java 21+
- Maven 3.9+
- Node.js 18+
- npm 9+
- Docker + Docker Compose

### 1) Start database + Redis

```bash
cd backend
docker compose up -d postgres redis
```

### 2) Run backend

```bash
cd backend
mvn spring-boot:run
```

Default backend URL: `http://localhost:8080`

### 3) Run frontend

```bash
cd frontend
npm install
npm run dev
```

Default frontend URL: `http://localhost:5173`

### 4) Optional build checks

```bash
cd backend
mvn -q -DskipTests compile
mvn -q test

cd ../frontend
npm run lint
npm run build
```
