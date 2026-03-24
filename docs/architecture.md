# Architecture Guide

This document is the high-signal technical handoff for the project. It explains how the system is structured, how the main authentication flows work, and where to make changes safely.

## System Context

The application is made of four main pieces:

- Spring Boot backend: authentication, authorization, token lifecycle, abuse protection, email delivery, user APIs, and admin APIs
- React frontend: route rendering, auth/session coordination, protected navigation, and API integration
- PostgreSQL: source of truth for users, roles, and persisted token state
- Redis: shared cache and rate-limiting/abuse-protection backend

## Backend Architecture

The backend follows a conventional layered architecture with a few important security-specific support layers.

### Core layers

- `controller`: transport boundary, request validation, response shaping
- `service`: business workflows and orchestration
- `repository`: persistence boundary
- `entity`: database-backed domain model
- `dto`: request and response contracts
- `exception`: centralized error model

### Security-specific layers

- `security`: JWT utilities, filter, Spring Security integration, refresh-cookie behavior, OAuth handlers
- `service/auth`: token issuance, abuse protection, OAuth user provisioning
- `service/support`: OTP generation, email delivery, password policy, hashing, time abstraction, rate limiting
- `util`: shared helpers such as email normalization and authenticated principal extraction

## Backend Request Flow

A typical protected request follows this path:

1. `JwtAuthFilter` extracts the bearer token from the request.
2. `JwtUtil` validates and parses the token.
3. Spring Security builds an authenticated `SecurityContext`.
4. The controller accepts the request and delegates immediately to a service.
5. The service applies business rules and calls repositories or support services.
6. `GlobalExceptionHandler` translates failures into a stable API response shape.

The important design rule is that controllers stay thin. They do not own password checks, token hashing, or rate-limiting decisions.

## Auth Flows

### Registration and OTP verification

`AuthServiceImpl` handles registration and email verification:

- normalize email
- reject duplicates
- validate password policy
- hash and store password
- generate OTP
- hash and store OTP
- persist user in a disabled state
- send verification email

Verification then:

- rate-limits the request
- verifies OTP hash match
- checks expiry
- enables the account
- clears OTP state
- sends a welcome email

### Login and refresh

Login uses Spring Security authentication for password validation and `AuthTokenService` for token issuance.

Token model:

- access token: JWT, short-lived, stateless, used on API requests
- refresh token: random opaque secret, hashed in the database, rotated on use

Refresh flow:

1. frontend sends refresh request
2. backend loads user by refresh-token hash
3. backend checks expiry
4. backend issues a new token pair
5. backend rotates the stored refresh-token hash

This gives the operational simplicity of JWT access tokens without the revocation limitations of long-lived JWT refresh tokens.

### Password reset

Password reset is deliberately split into two stages:

- request reset: generate random reset token, hash it, persist it, and email the raw token link
- complete reset: hash presented token, load user, validate expiry, update password, clear token state

The public reset request returns a generic success message even if the email is unknown. That prevents account enumeration.

### OAuth2 login

OAuth2 is handled by Spring Security plus project-specific handlers:

1. frontend redirects to `/oauth2/authorization/{provider}`
2. provider authenticates the user
3. Spring Security receives the callback
4. `OAuth2UserProvisioningService` loads or creates the local user
5. success handler issues the refresh cookie
6. frontend callback page completes login via `/api/v1/auth/refresh`

This keeps token issuance consistent across local and social login.

## Abuse Protection

`AuthAbuseProtectionService` coordinates:

- per-IP and per-email login rate limiting
- OTP verification rate limiting
- resend-OTP cooldown and windowed rate limiting
- password reset request throttling
- temporary account and OTP lockouts after repeated failures

Redis-backed counters are used for distributed enforcement, while user lock state is persisted in PostgreSQL.

## Important Backend Components

### `AuthServiceImpl`

Owns the core auth workflows:

- register
- verify OTP
- login
- reset password
- update password
- resend OTP
- change password

This is the primary workflow orchestration service in the backend.

### `AuthTokenService`

Owns token lifecycle:

- issue token pair
- refresh token pair
- revoke refresh token

It is intentionally separate from `AuthServiceImpl` so token logic does not leak into unrelated business flows.

### `RefreshTokenCookieService`

Owns all cookie header behavior for refresh tokens:

- cookie name
- path
- secure flag
- same-site behavior
- domain
- clear-cookie behavior

This prevents cookie policy drift across controllers and handlers.

### `CustomUserDetailsService`

Adapts the domain `User` model to Spring Security's `UserDetails` contract.

### `GlobalExceptionHandler`

Centralizes error-to-response translation so the frontend can rely on a stable contract.

## Data Model Notes

The `User` entity stores more than profile data. It also carries authentication state such as:

- password hash
- verification OTP hash and expiry
- reset-token hash and expiry
- refresh-token hash and expiry
- failed login counters
- account lock timestamps
- OAuth provider metadata

That is intentional. Authentication state is part of the domain model, not just transient session state.

## Frontend Architecture

The frontend is intentionally simple in structure but opinionated in auth behavior.

### Key pieces

- `App.jsx`: route definitions
- `AuthContext.jsx`: auth bootstrap and session state
- `services/api.js`: Axios client, response unwrapping, refresh retry logic
- `ProtectedRoute.jsx`: route guard
- `pages/*`: route-level screens

### Session behavior

The frontend stores:

- access token in memory
- user profile snapshot in `localStorage`
- refresh token in backend-managed HttpOnly cookie

The API layer retries protected requests after refresh and clears local auth state if refresh can no longer succeed.

## Development Modes

### Docker mode

Use Docker Compose when you want the full local stack:

- PostgreSQL
- Redis
- backend
- frontend
- Adminer
- Redis Commander

### Local split-process mode

Use this when iterating quickly on code:

- bring up PostgreSQL and Redis with Docker Compose
- run backend through Maven
- run frontend through Vite

## Extension Points

If you need to extend the system, these are good seams:

- new auth workflow: `AuthServiceImpl`
- new token behavior: `AuthTokenService`
- new abuse-protection rules: `AuthAbuseProtectionService`
- new user/admin read models: `UserPortalServiceImpl`, `AdminServiceImpl`
- new frontend route: `src/pages`, `src/App.jsx`
- new provider-specific auth logic: `OAuth2UserProvisioningService`

## Operational Notes

- Cookie settings matter in production. Review `AUTH_REFRESH_TOKEN_COOKIE_SECURE`, `AUTH_REFRESH_TOKEN_COOKIE_SAME_SITE`, and allowed origins carefully.
- SMTP is part of the product, not an optional decoration. Registration and reset flows depend on it.
- Redis outages affect rate limiting and caching behavior, so do not treat Redis as frontend-only infrastructure.
- Seeded admin credentials should remain disabled except in controlled environments.

## Recommended Reading Order

If you are onboarding to the codebase, this order gives the fastest understanding:

1. [../README.md](../README.md)
2. [../backend/src/main/resources/application.properties.example](../backend/src/main/resources/application.properties.example)
3. [../backend/src/main/java/com/auth/entity/User.java](../backend/src/main/java/com/auth/entity/User.java)
4. [../backend/src/main/java/com/auth/config/SecurityConfig.java](../backend/src/main/java/com/auth/config/SecurityConfig.java)
5. [../backend/src/main/java/com/auth/service/impl/AuthServiceImpl.java](../backend/src/main/java/com/auth/service/impl/AuthServiceImpl.java)
6. [../backend/src/main/java/com/auth/service/auth/AuthTokenService.java](../backend/src/main/java/com/auth/service/auth/AuthTokenService.java)
7. [../frontend/src/context/AuthContext.jsx](../frontend/src/context/AuthContext.jsx)
8. [../frontend/src/services/api.js](../frontend/src/services/api.js)

## Related Documents

- Project overview: [../README.md](../README.md)
- Frontend guide: [../frontend/README.md](../frontend/README.md)
- Backend build-order guide: [../backend/BACKEND_CODEBASE_CREATION_GUIDE.md](../backend/BACKEND_CODEBASE_CREATION_GUIDE.md)
