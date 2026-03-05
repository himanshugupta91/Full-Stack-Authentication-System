# Deep-Dive Implementation Roadmap (Task-by-Task)

This roadmap is based on a deep scan of the current project structure and is designed as a **from-scratch reproduction plan** for the same feature set.

Project root: `/Users/himanshu/Downloads/Full-Stack-Authentication-System`

---

## 0) What Exists Right Now (Deep Scan Snapshot)

### Backend modules already represented in this project
1. Core config: security, CORS, password encoder, page serialization, startup seeding.
2. Auth flows: register, OTP verify/resend, login, refresh, logout, reset/update password, change password.
3. Security internals: JWT utility/filter, custom user details, refresh cookie service.
4. Abuse protection: rate limiting + lockout counters + retry-after handling.
5. OAuth2: Google/GitHub/Apple/LinkedIn with custom success/failure handlers and LinkedIn request resolver.
6. Admin and user APIs: dashboard/profile + paginated/searchable admin users.
7. Support services: OTP token generation, token hashing, email templates, password policy.
8. Tests: controller/service/security tests for core auth behavior.

### Frontend modules already represented
1. Auth context with refresh-on-bootstrap session logic.
2. Axios API client with access-token storage + refresh retry interceptor.
3. Pages for all auth flows and dashboard/admin views.
4. Route guarding and role-based redirect logic.
5. Password policy parity utility aligned with backend policy.

### Infra and delivery
1. Dockerfile + docker-compose (Postgres/Redis + optional admin tools + app service).
2. Env examples for backend and frontend.
3. GitHub workflow for secret scanning.

---

## 1) 7-Day Detailed Execution Plan (Feature-Wise)

## Day 1 - Foundation and Runtime Setup

### Day goal
Bring up local infrastructure and application skeleton with security baseline.

### D1-T1: Initialize backend + frontend project scaffolds
- Create backend Spring Boot app with dependencies:
  - Web, Security, Validation, Data JPA, PostgreSQL, Redis, Mail, OAuth2 Client, Thymeleaf, Lombok, MapStruct, Test.
- Create frontend Vite + React app.

### D1-T2: Add runtime configuration and environment strategy
- Implement backend property model with env fallbacks.
- Implement frontend public env variables (`VITE_API_URL`, `VITE_OAUTH_BASE_URL`).
- Ensure `.env` is ignored and `.env.example` is committed.

### D1-T3: Provision local infrastructure
- Add `backend/docker-compose.yaml`:
  - `postgres`
  - `redis`
  - optional `adminer`
  - optional `redis-commander`
  - optional `app` service.

### D1-T4: Build baseline backend config
- Implement:
  - `backend/src/main/java/com/auth/config/SecurityConfig.java`
  - `backend/src/main/java/com/auth/config/CorsConfig.java`
  - `backend/src/main/java/com/auth/config/PasswordConfig.java`
  - `backend/src/main/java/com/auth/config/SpringDataWebConfig.java`
- Security baseline:
  - `/api/auth/**` public.
  - `/api/user/**` requires user/admin authority.
  - `/api/admin/**` requires admin authority.

### D1-T5: Seed bootstrap data
- Implement `backend/src/main/java/com/auth/config/DataInitializer.java`:
  - ensure roles exist
  - optional seed admin account.

### D1-T6: Verify startup
- Run:
  - `cd backend && docker compose up -d postgres redis`
  - `cd backend && mvn spring-boot:run`
  - `cd frontend && npm install && npm run dev`

### Day 1 done criteria
1. Backend starts without config exceptions.
2. Postgres and Redis are healthy.
3. Frontend boots and can call backend public routes.

---

## Day 2 - Domain Model + Registration + OTP Send

### Day goal
Implement user creation with OTP generation and email dispatch.

### D2-T1: Implement entities and repositories
- Files:
  - `backend/src/main/java/com/auth/entity/User.java`
  - `backend/src/main/java/com/auth/entity/Role.java`
  - `backend/src/main/java/com/auth/repository/UserRepository.java`
  - `backend/src/main/java/com/auth/repository/RoleRepository.java`
- Include:
  - user-role relationship
  - verification OTP fields
  - reset token fields
  - refresh token fields
  - failed-attempt counters
  - lock timestamps
  - created/updated timestamps.

### D2-T2: Implement DTOs + mapper
- Request DTOs:
  - `RegisterRequest`, `OtpVerifyRequest`, `LoginRequest`.
- Response DTOs:
  - `MessageResponse`, `AuthResponse`, `AuthTokens`.
- Mapper:
  - `backend/src/main/java/com/auth/mapper/UserMapper.java`.

### D2-T3: Implement support services needed for registration
- `backend/src/main/java/com/auth/service/support/OtpService.java`
  - `generateOtp()`.
- `backend/src/main/java/com/auth/service/support/TokenHashService.java`
  - deterministic hash + constant-time compare.
- `backend/src/main/java/com/auth/service/support/PasswordPolicyService.java`
  - minimum length, letter, digit, no spaces, blocklist checks.
- `backend/src/main/java/com/auth/service/support/EmailService.java`
  - OTP email method.

### D2-T4: Implement service contracts and core user services
- `UserService` + `UserServiceImpl`.
- `RoleService` + `RoleServiceImpl`.
- `AuthService` + `AuthServiceImpl.register(...)`.

### D2-T5: Implement register endpoint
- `backend/src/main/java/com/auth/controller/AuthController.java`
  - `POST /api/auth/register`.

### D2-T6: Frontend register flow
- `frontend/src/pages/Register.jsx`
- `frontend/src/context/AuthContext.jsx`
- `frontend/src/services/api.js`
- Validate password in frontend with `frontend/src/utils/passwordPolicy.js`.

### D2-T7: Verify register behavior
- API checks:
  - valid register -> success response.
  - duplicate email -> conflict.
  - weak password -> validation error.
- DB checks:
  - `enabled=false`
  - hashed password stored
  - hashed OTP + expiry present.

### Day 2 done criteria
1. Registration API fully functional.
2. OTP email send path invoked.
3. Frontend registration screen integrated.

---

## Day 3 - OTP Verify + Login + JWT Session Establishment

### Day goal
Activate accounts and issue authenticated sessions.

### D3-T1: Implement OTP verify and resend flow
- In `AuthServiceImpl` implement:
  - `verifyOtp(...)`
  - `resendOtp(...)`
- Ensure:
  - OTP hash comparison
  - expiry validation
  - enable user
  - clear OTP state
  - resend path regenerates OTP.

### D3-T2: Implement JWT utility and user details
- `backend/src/main/java/com/auth/security/jwt/JwtUtil.java`:
  - generate token
  - parse token
  - validate token.
- `backend/src/main/java/com/auth/security/CustomUserDetailsService.java`:
  - load user by email
  - map roles to authorities.

### D3-T3: Implement token issuance service
- `backend/src/main/java/com/auth/service/auth/AuthTokenService.java`
  - `issueTokens(User)`
  - persist hashed refresh token and expiry.

### D3-T4: Implement login flow
- `AuthServiceImpl.login(...)`.
- `AuthController.login(...)`.
- Refresh cookie creation via:
  - `backend/src/main/java/com/auth/security/RefreshTokenCookieService.java`.

### D3-T5: Frontend OTP/login integration
- `frontend/src/pages/VerifyOtp.jsx`
- `frontend/src/pages/Login.jsx`
- `frontend/src/services/api.js`
- `frontend/src/context/AuthContext.jsx`

### D3-T6: Verify auth establishment
- API checks:
  - `POST /api/auth/verify-otp`
  - `POST /api/auth/resend-otp`
  - `POST /api/auth/login`
- Confirm:
  - access token in response
  - refresh token in HttpOnly cookie.

### Day 3 done criteria
1. Verified users can login.
2. Unverified users are blocked.
3. Frontend login redirects by role.

---

## Day 4 - Refresh/Logout + Password Recovery + Authenticated Password Change

### Day goal
Complete token lifecycle and password management features.

### D4-T1: Implement refresh and logout
- `AuthTokenService.refreshTokens(...)`
- `AuthTokenService.revokeRefreshToken(...)`
- `AuthController.refreshToken(...)`
- `AuthController.logout(...)`
- Accept refresh token from body and/or cookie.

### D4-T2: Implement password reset request flow
- `AuthServiceImpl.resetPassword(...)`
- Generate raw reset token
- Store hash + expiry
- Send reset link email
- Return generic response to prevent user enumeration.

### D4-T3: Implement password update by reset token
- `AuthServiceImpl.updatePassword(...)`
- Validate hashed token + expiry
- Apply password policy
- encode and store new password
- clear reset token state.

### D4-T4: Implement authenticated change-password
- `AuthServiceImpl.changePassword(...)`
- `UserController.changePassword(...)`
- verify current password before update.

### D4-T5: Frontend password flows
- `frontend/src/pages/ForgotPassword.jsx`
- `frontend/src/pages/ResetPassword.jsx`
- `frontend/src/pages/ChangePassword.jsx`

### D4-T6: Verify all password/token lifecycle endpoints
- `POST /api/auth/refresh`
- `POST /api/auth/logout`
- `POST /api/auth/reset-password`
- `POST /api/auth/update-password`
- `POST /api/user/change-password`

### Day 4 done criteria
1. Refresh rotation works reliably.
2. Logout invalidates refresh state.
3. Reset and change password flows pass manual tests.

---

## Day 5 - Abuse Protection + JWT Request Guard + Exception Standardization

### Day goal
Harden API with brute-force and request-rate defenses.

### D5-T1: Implement Redis rate limiting primitive
- `backend/src/main/java/com/auth/service/support/RateLimitService.java`
  - fixed-window consume logic
  - include retry metadata.

### D5-T2: Implement abuse orchestration
- `backend/src/main/java/com/auth/service/auth/AuthAbuseProtectionService.java`
  - guard login
  - guard OTP verify
  - guard resend OTP
  - guard reset-password
  - record failed login/otp
  - clear counters on success.

### D5-T3: Integrate abuse checks into auth service methods
- `AuthServiceImpl.login(...)`
- `AuthServiceImpl.verifyOtp(...)`
- `AuthServiceImpl.resendOtp(...)`
- `AuthServiceImpl.resetPassword(...)`.

### D5-T4: Implement JWT auth filter
- `backend/src/main/java/com/auth/security/jwt/JwtAuthFilter.java`
  - parse bearer header
  - validate token
  - set security context.
- Register filter in `SecurityConfig` before username/password filter.

### D5-T5: Implement exception model and global handler
- Files:
  - `GlobalExceptionHandler`
  - `AccountLockedException`
  - `RateLimitExceededException`
  - `TokenValidationException`
  - `UserAlreadyExistsException`
  - `ResourceNotFoundException`
- Ensure consistent `MessageResponse` body and status codes.

### D5-T6: Verify hardening behavior
- Repeated bad logins should lock account.
- Burst traffic should return rate-limit with retry-after semantics.
- Invalid/expired JWT should fail protected routes.

### Day 5 done criteria
1. Abuse controls active on all intended auth endpoints.
2. JWT-protected routes behave correctly.
3. Errors are standardized for frontend consumption.

---

## Day 6 - OAuth2 + User Portal + Admin Module

### Day goal
Add social login and full role-based dashboard features.

### D6-T1: Implement OAuth2 provisioning service
- `backend/src/main/java/com/auth/service/auth/OAuth2UserProvisioningService.java`
- Responsibilities:
  - normalize provider
  - extract provider user id
  - map email/name with provider-specific fallback
  - create or update local user
  - assign default role on first creation.

### D6-T2: Implement OAuth2 handlers + resolver
- `OAuth2AuthenticationSuccessHandler`
  - issue tokens
  - set refresh cookie
  - redirect to frontend callback.
- `OAuth2AuthenticationFailureHandler`
  - redirect to login with error param.
- `LinkedInAuthorizationRequestResolver`
  - LinkedIn nonce compatibility handling.

### D6-T3: Implement user portal APIs
- `UserPortalService` + `UserPortalServiceImpl`
- `UserController` endpoints:
  - `GET /api/user/dashboard`
  - `GET /api/user/profile`

### D6-T4: Implement admin APIs
- `AdminService` + `AdminServiceImpl`
- `AdminController` endpoints:
  - `GET /api/admin/dashboard`
  - `GET /api/admin/users`
- Add search/filter/sort/pagination normalization and role filter handling.

### D6-T5: Frontend dashboard + OAuth callback wiring
- `frontend/src/pages/OAuthCallback.jsx`
- `frontend/src/pages/UserDashboard.jsx`
- `frontend/src/pages/AdminDashboard.jsx`
- `frontend/src/components/ProtectedRoute.jsx`
- `frontend/src/services/api.js` (admin/user endpoints).

### D6-T6: Verify OAuth and role UX
- OAuth login returns to callback route then redirects user/admin accordingly.
- Admin listing filters work for search, enabled, role, and pagination.

### Day 6 done criteria
1. OAuth2 works end-to-end.
2. User and admin pages are fully backed by secured APIs.

---

## Day 7 - QA, Test Coverage, Packaging, and Release Checklist

### Day goal
Stabilize and make project release-ready.

### D7-T1: Complete and run backend test suite
- Validate/extend tests in:
  - `backend/src/test/java/com/auth/controller/AuthControllerTest.java`
  - `backend/src/test/java/com/auth/service/impl/AuthServiceImplTest.java`
  - `backend/src/test/java/com/auth/service/auth/AuthTokenServiceTest.java`
  - `backend/src/test/java/com/auth/service/auth/OAuth2UserProvisioningServiceTest.java`
  - `backend/src/test/java/com/auth/service/impl/AdminServiceImplTest.java`
  - `backend/src/test/java/com/auth/service/impl/UserServiceImplTest.java`
  - `backend/src/test/java/com/auth/security/CustomUserDetailsServiceTest.java`

### D7-T2: Frontend quality checks
- `npm run lint`
- `npm run build`
- Verify no broken routes and token refresh behavior in browser.

### D7-T3: Full manual regression pass (critical flows)
1. Register -> OTP verify -> login -> dashboard.
2. Login failure paths and lock behavior.
3. Refresh + logout.
4. Forgot/reset/update/change password.
5. OAuth2 login for at least one configured provider.
6. Admin list filters + pagination + role visibility.

### D7-T4: Containerization verification
- Build backend image.
- Start compose stack including app service.
- Validate app connectivity to postgres/redis in container network.

### D7-T5: Security and delivery hygiene
- Verify secret handling in `.env` only.
- Keep `JWT_SECRET` strong and non-default.
- Verify cookie secure/samesite for deployment target.
- Ensure secret scan workflow remains passing.

### Day 7 done criteria
1. Tests/lint/build pass.
2. All critical flows pass regression checks.
3. Deployment path is documented and reproducible.

---

## 2) Endpoint-by-Endpoint Task Map (Implementation Sequence)

## Auth endpoints (build in this exact order)
1. `POST /api/auth/register`
2. `POST /api/auth/verify-otp`
3. `POST /api/auth/login`
4. `POST /api/auth/refresh`
5. `POST /api/auth/logout`
6. `POST /api/auth/reset-password`
7. `POST /api/auth/update-password`
8. `POST /api/auth/resend-otp`

For each endpoint complete this checklist:
1. Request DTO validation.
2. Service logic.
3. Repository interaction.
4. Security and abuse checks.
5. Controller wiring.
6. Happy-path test.
7. Failure-path tests.
8. Frontend screen integration.

## User endpoints
1. `GET /api/user/dashboard`
2. `GET /api/user/profile`
3. `POST /api/user/change-password`

## Admin endpoints
1. `GET /api/admin/dashboard`
2. `GET /api/admin/users`

## OAuth2 managed routes
1. `/oauth2/authorization/{provider}`
2. `/login/oauth2/code/{provider}`

---

## 3) File-by-File Build Order (If starting with empty project)

1. Config + app entry files.
2. Entities and repositories.
3. DTOs and mapper.
4. Service interfaces.
5. Support services (OTP/hash/password/email/rate-limit).
6. Core auth service implementation.
7. Security internals (jwt util/filter/user details/cookies).
8. Controllers.
9. OAuth2 services and handlers.
10. User/admin services and controllers.
11. Exception handlers.
12. Frontend context + API client.
13. Frontend pages and route guards.
14. Tests.

---

## 4) Daily Command Checklist

## Backend
1. `cd backend && mvn -q -DskipTests compile`
2. `cd backend && mvn -q test`
3. `cd backend && mvn spring-boot:run`

## Frontend
1. `cd frontend && npm run lint`
2. `cd frontend && npm run build`
3. `cd frontend && npm run dev`

## Infrastructure
1. `cd backend && docker compose up -d postgres redis`
2. `cd backend && docker compose ps`

---

## 5) Suggested Commit Plan (Granular)

1. `chore: bootstrap backend/frontend and environment setup`
2. `feat(auth): add domain entities repositories and dto contracts`
3. `feat(auth): implement register otp send and verify flows`
4. `feat(security): implement jwt util login and refresh cookie`
5. `feat(auth): add refresh logout reset and password change flows`
6. `feat(security): add abuse protection rate limits and jwt filter`
7. `feat(oauth): implement oauth2 provisioning and handlers`
8. `feat(user-admin): add user/admin dashboard apis and filters`
9. `feat(frontend): integrate auth pages context and protected routes`
10. `test: add service controller and security test coverage`
11. `docs: finalize setup and deployment notes`

---

## 6) Final Definition of Done

1. All auth/user/admin APIs implemented and tested.
2. Frontend flows fully integrated with backend.
3. Refresh-token rotation and logout revocation verified.
4. Abuse protection and lockout behavior verified.
5. OAuth2 provider flow verified for at least one provider.
6. Backend tests + frontend lint/build pass.
7. Docker-based local runbook works from clean setup.
