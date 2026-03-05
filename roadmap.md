# My Beginner-Friendly 1-Week Plan (Company Style)

This is my personal project plan.
I am a beginner, so I wrote this in simple English.
But I still want to work like a real company team.

Project: `Full-Stack-Authentication-System`
Main focus: backend first, then frontend integration.

---

## How I Will Work

1. I will finish one feature fully before starting the next.
2. Every feature must include:
- API endpoint
- service logic
- database work
- error handling
- test
- frontend connection
3. I will keep secrets in `.env` only.
4. End of every day: I will run checks and write short notes.

---

## Company-Style Ticket Template (Simple)

For each ticket, I will write:
1. Scope (what I am building)
2. Files (where I will code)
3. Steps (what I will do)
4. Acceptance Criteria (when done)
5. QA (how I will test)

---

## When I Will Add Exception Handling (Exact Timing)

1. Day 2 (after register logic works):  
I will throw domain exceptions from service methods.
- `UserAlreadyExistsException` for duplicate email.
- `ResourceNotFoundException` where user is required but missing.
- `IllegalArgumentException` for invalid business input (like weak password).

2. Day 3 (after OTP/login methods work):  
I will add auth-specific exceptions in service flow.
- `TokenValidationException` for invalid/expired OTP or token.
- `BadCredentialsException` for wrong email/password.

3. Day 4 (after password flows work):  
I will finalize reset/change-password error scenarios.
- invalid/expired reset token -> `TokenValidationException`
- wrong current password -> `BadCredentialsException`

4. Day 5 (centralization step):  
I will implement all mappings in `GlobalExceptionHandler` so every API returns consistent status + message.
- 400 validation/illegal arguments
- 401 bad credentials/token issues
- 404 not found
- 409 duplicate user
- 423 account locked
- 429 rate limit
- 500 fallback

5. Day 7 (final QA):  
I will test both success and failure response bodies for every major endpoint.

---

## Week Overview

1. Day 1: Setup environment and architecture
2. Day 2: Register user + send OTP
3. Day 3: Verify OTP + login + JWT
4. Day 4: Refresh/logout + password flows
5. Day 5: Security hardening (rate limit + lock + filter)
6. Day 6: OAuth2 + user/admin dashboard APIs
7. Day 7: Testing + QA + release readiness

---

## Day 1 - Setup Environment and Architecture

### Ticket D1-1: Start local services
Scope: run PostgreSQL and Redis locally.

Files:
- `backend/docker-compose.yaml`

Steps:
1. Configure postgres service.
2. Configure redis service.
3. Add health checks.
4. Start services.

Acceptance Criteria:
1. `docker compose up -d postgres redis` works.
2. Both containers are healthy.

QA:
1. `docker compose ps`
2. Backend can connect to DB and Redis.

---

### Ticket D1-2: Backend config and env
Scope: set all required backend config values.

Files:
- `backend/src/main/resources/application.properties`
- `backend/.env.example`

Steps:
1. Set DB, Redis, Mail, JWT, OAuth placeholders.
2. Keep local defaults for development.
3. Keep secrets out of source code.

Acceptance Criteria:
1. Backend starts with no missing property error.
2. Secrets are only in `.env`.

QA:
1. Run backend and check startup logs.

---

### Ticket D1-3: Security baseline
Scope: define base access rules.

Files:
- `backend/src/main/java/com/auth/config/SecurityConfig.java`
- `backend/src/main/java/com/auth/config/CorsConfig.java`
- `backend/src/main/java/com/auth/config/PasswordConfig.java`

Steps:
1. Keep `/api/auth/**` public.
2. Protect `/api/user/**` for USER/ADMIN.
3. Protect `/api/admin/**` for ADMIN.
4. Enable CORS for frontend URL.

Acceptance Criteria:
1. Public endpoints are open.
2. Protected endpoints need auth.

QA:
1. Test one public endpoint.
2. Test one protected endpoint without token.

---

### Ticket D1-4: Seed roles and optional admin
Scope: create default roles on startup.

Files:
- `backend/src/main/java/com/auth/config/DataInitializer.java`

Steps:
1. Ensure `ROLE_USER` exists.
2. Ensure `ROLE_ADMIN` exists.
3. Create admin only when env flag is true.

Acceptance Criteria:
1. Roles are present after app startup.
2. Admin is created only when enabled in config.

---

## Day 2 - Registration and OTP Send

### Ticket D2-1: User and role data model
Scope: create entity and repository base.

Files:
- `backend/src/main/java/com/auth/entity/User.java`
- `backend/src/main/java/com/auth/entity/Role.java`
- `backend/src/main/java/com/auth/repository/UserRepository.java`
- `backend/src/main/java/com/auth/repository/RoleRepository.java`

Steps:
1. Add user fields (email/password/enabled etc).
2. Add token and lock-related fields.
3. Map user-role relation.
4. Add repository methods for email/token lookups.

Acceptance Criteria:
1. Tables are created correctly.
2. Repository methods return expected data.

---

### Ticket D2-2: DTOs and mapper
Scope: clean request and response contracts.

Files:
- `backend/src/main/java/com/auth/dto/request/*`
- `backend/src/main/java/com/auth/dto/response/*`
- `backend/src/main/java/com/auth/mapper/UserMapper.java`

Steps:
1. Add validation on request fields.
2. Keep response safe (no secret fields).
3. Map register request to user entity.

Acceptance Criteria:
1. Invalid request gives validation error.
2. Mapping works for auth and user data.

---

### Ticket D2-3: Register service flow
Scope: register user and send OTP.

Files:
- `backend/src/main/java/com/auth/service/AuthService.java`
- `backend/src/main/java/com/auth/service/impl/AuthServiceImpl.java`
- `backend/src/main/java/com/auth/service/support/OtpService.java`
- `backend/src/main/java/com/auth/service/support/TokenHashService.java`
- `backend/src/main/java/com/auth/service/support/PasswordPolicyService.java`
- `backend/src/main/java/com/auth/service/support/EmailService.java`

Steps:
1. Normalize email.
2. Check duplicate email.
3. Validate password strength.
4. Hash password.
5. Generate OTP.
6. Hash OTP + set expiry.
7. Assign `ROLE_USER`.
8. Save user.
9. Send OTP email.
10. Throw clear exceptions for duplicate/invalid inputs.

Acceptance Criteria:
1. User saved with `enabled=false`.
2. OTP hash + expiry saved.
3. Success message returned.

---

### Ticket D2-4: Register API + frontend page
Scope: connect backend and frontend for registration.

Files:
- `backend/src/main/java/com/auth/controller/AuthController.java`
- `frontend/src/pages/Register.jsx`
- `frontend/src/context/AuthContext.jsx`
- `frontend/src/services/api.js`
- `frontend/src/utils/passwordPolicy.js`

Acceptance Criteria:
1. Register form submits successfully.
2. On success, user goes to OTP page.

QA:
1. Try duplicate email.
2. Try weak password.

---

## Day 3 - OTP Verify, Login, JWT

### Ticket D3-1: OTP verify and resend
Scope: activate account and support resend.

Files:
- `backend/src/main/java/com/auth/service/impl/AuthServiceImpl.java`
- `backend/src/main/java/com/auth/controller/AuthController.java`

Steps:
1. Verify OTP hash.
2. Check OTP expiry.
3. Enable user.
4. Clear OTP fields.
5. Add resend OTP logic.
6. Return token-validation errors for invalid/expired OTP.

Acceptance Criteria:
1. Valid OTP verifies account.
2. Invalid/expired OTP returns proper error.

---

### Ticket D3-2: JWT core and user details
Scope: implement auth token internals.

Files:
- `backend/src/main/java/com/auth/security/jwt/JwtUtil.java`
- `backend/src/main/java/com/auth/security/CustomUserDetailsService.java`

Steps:
1. Create access token.
2. Parse token subject.
3. Validate token signature.
4. Map user roles to authorities.

Acceptance Criteria:
1. Token generated and validated correctly.
2. UserDetails loads roles correctly.

---

### Ticket D3-3: Login and refresh cookie
Scope: successful login should create session tokens.

Files:
- `backend/src/main/java/com/auth/service/auth/AuthTokenService.java`
- `backend/src/main/java/com/auth/security/RefreshTokenCookieService.java`
- `backend/src/main/java/com/auth/service/impl/AuthServiceImpl.java`
- `backend/src/main/java/com/auth/controller/AuthController.java`

Steps:
1. Authenticate email/password.
2. Issue access + refresh token.
3. Store hashed refresh token in DB.
4. Set refresh token as HttpOnly cookie.
5. Return auth errors for wrong credentials or disabled account.

Acceptance Criteria:
1. Login returns access token response.
2. Refresh cookie is set in response.

---

### Ticket D3-4: Frontend OTP + login integration
Files:
- `frontend/src/pages/VerifyOtp.jsx`
- `frontend/src/pages/Login.jsx`
- `frontend/src/services/api.js`
- `frontend/src/context/AuthContext.jsx`

Acceptance Criteria:
1. OTP verify page works.
2. Login redirects to user/admin dashboard by role.

---

## Day 4 - Refresh, Logout, Password Features

### Ticket D4-1: Refresh and logout endpoints
Scope: complete session lifecycle.

Files:
- `backend/src/main/java/com/auth/service/auth/AuthTokenService.java`
- `backend/src/main/java/com/auth/controller/AuthController.java`

Steps:
1. Refresh using token from cookie or body.
2. Rotate refresh token on refresh.
3. Revoke refresh token on logout.
4. Clear cookie on logout.

Acceptance Criteria:
1. Refresh gives new access token.
2. Logout removes active refresh session.

---

### Ticket D4-2: Forgot and reset password APIs
Scope: recover account securely.

Files:
- `backend/src/main/java/com/auth/service/impl/AuthServiceImpl.java`
- `backend/src/main/java/com/auth/service/support/EmailService.java`
- `backend/src/main/resources/templates/emails/*`

Steps:
1. Create reset token.
2. Save reset token hash + expiry.
3. Send reset email link.
4. Update password only with valid token.
5. Clear reset token after success.
6. Return token-validation errors for invalid/expired reset token.

Acceptance Criteria:
1. Reset request returns generic message.
2. Update password fails for invalid/expired token.

---

### Ticket D4-3: Change password (logged-in user)
Scope: secure password change for authenticated user.

Files:
- `backend/src/main/java/com/auth/controller/UserController.java`
- `backend/src/main/java/com/auth/service/impl/AuthServiceImpl.java`

Acceptance Criteria:
1. Current password is required.
2. New password must pass policy.

---

### Ticket D4-4: Frontend password pages
Files:
- `frontend/src/pages/ForgotPassword.jsx`
- `frontend/src/pages/ResetPassword.jsx`
- `frontend/src/pages/ChangePassword.jsx`

Acceptance Criteria:
1. All pages connected to backend APIs.
2. Clear success/error messages shown.

---

## Day 5 - Security Hardening

### Ticket D5-1: Rate limiting core service
Scope: create Redis-based request limiter.

Files:
- `backend/src/main/java/com/auth/service/support/RateLimitService.java`

Acceptance Criteria:
1. Service returns allow/deny + retry time.

---

### Ticket D5-2: Abuse protection logic
Scope: stop brute-force and abuse.

Files:
- `backend/src/main/java/com/auth/service/auth/AuthAbuseProtectionService.java`

Steps:
1. Guard login by IP and email.
2. Guard OTP verify and resend.
3. Guard reset-password requests.
4. Lock account after max failed attempts.

Acceptance Criteria:
1. Abuse scenarios return lock/rate-limit errors.
2. Retry-after value is available.

---

### Ticket D5-3: JWT request filter
Scope: protect all private routes.

Files:
- `backend/src/main/java/com/auth/security/jwt/JwtAuthFilter.java`
- `backend/src/main/java/com/auth/config/SecurityConfig.java`

Acceptance Criteria:
1. Valid bearer token sets auth context.
2. Invalid token cannot access protected APIs.

---

### Ticket D5-4: Global exception handling
Scope: standardize API errors.

Files:
- `backend/src/main/java/com/auth/exception/GlobalExceptionHandler.java`
- `backend/src/main/java/com/auth/exception/*`

Acceptance Criteria:
1. Consistent JSON error response format.
2. Correct status codes for known error cases.

Steps:
1. Map validation errors to 400.
2. Map bad credentials/token errors to 401.
3. Map not-found to 404 and duplicates to 409.
4. Map account lock to 423 and rate-limit to 429 with retry-after.
5. Keep one 500 fallback handler for unexpected errors.

---

## Day 6 - OAuth2 + User/Admin Features

### Ticket D6-1: OAuth2 user provisioning
Scope: create or update local user from social login.

Files:
- `backend/src/main/java/com/auth/service/auth/OAuth2UserProvisioningService.java`

Acceptance Criteria:
1. First social login creates user.
2. Repeat login updates/links user safely.

---

### Ticket D6-2: OAuth2 handlers and LinkedIn resolver
Scope: finish OAuth flow behavior.

Files:
- `backend/src/main/java/com/auth/security/oauth2/OAuth2AuthenticationSuccessHandler.java`
- `backend/src/main/java/com/auth/security/oauth2/OAuth2AuthenticationFailureHandler.java`
- `backend/src/main/java/com/auth/security/oauth2/LinkedInAuthorizationRequestResolver.java`

Acceptance Criteria:
1. Success flow sets cookie and redirects to frontend callback.
2. Failure flow redirects with readable error.

---

### Ticket D6-3: User dashboard/profile APIs
Scope: authenticated user info APIs.

Files:
- `backend/src/main/java/com/auth/controller/UserController.java`
- `backend/src/main/java/com/auth/service/impl/UserPortalServiceImpl.java`

Acceptance Criteria:
1. `/api/user/dashboard` works.
2. `/api/user/profile` works.

---

### Ticket D6-4: Admin dashboard/users APIs
Scope: admin data and user list management.

Files:
- `backend/src/main/java/com/auth/controller/AdminController.java`
- `backend/src/main/java/com/auth/service/impl/AdminServiceImpl.java`

Acceptance Criteria:
1. Admin dashboard metrics are correct.
2. User list supports page/size/search/role/enabled/sort.

---

### Ticket D6-5: Frontend dashboards and route guard
Files:
- `frontend/src/pages/OAuthCallback.jsx`
- `frontend/src/pages/UserDashboard.jsx`
- `frontend/src/pages/AdminDashboard.jsx`
- `frontend/src/components/ProtectedRoute.jsx`

Acceptance Criteria:
1. Role-based route protection works.
2. Non-admin cannot access admin page.

---

## Day 7 - Testing, QA, Release Readiness

### Ticket D7-1: Backend tests
Scope: verify core auth/service/security logic.

Files:
- `backend/src/test/java/com/auth/controller/AuthControllerTest.java`
- `backend/src/test/java/com/auth/service/impl/AuthServiceImplTest.java`
- `backend/src/test/java/com/auth/service/auth/AuthTokenServiceTest.java`
- `backend/src/test/java/com/auth/service/auth/OAuth2UserProvisioningServiceTest.java`
- `backend/src/test/java/com/auth/service/impl/AdminServiceImplTest.java`
- `backend/src/test/java/com/auth/service/impl/UserServiceImplTest.java`
- `backend/src/test/java/com/auth/security/CustomUserDetailsServiceTest.java`

Acceptance Criteria:
1. Critical tests pass.
2. Failures (if any) are documented.

---

### Ticket D7-2: Frontend quality checks
Steps:
1. Run lint.
2. Run production build.
3. Manually verify routes.

Acceptance Criteria:
1. Lint passes.
2. Build passes.

---

### Ticket D7-3: Full manual regression
I will test these full journeys:
1. Register -> OTP verify -> login.
2. Refresh token flow.
3. Logout and login again.
4. Forgot/reset/change password.
5. OAuth callback flow.
6. Admin filters and pagination.

Acceptance Criteria:
1. Major flows pass.
2. Known issues are listed clearly.

---

### Ticket D7-4: Deployment readiness
Checklist:
1. `.env` values set correctly.
2. Strong `JWT_SECRET` configured.
3. Production cookie flags configured.
4. Docker image builds.
5. Secret scan workflow remains clean.

---

## Daily Command Checklist

### Backend
1. `cd backend && mvn -q -DskipTests compile`
2. `cd backend && mvn -q test`
3. `cd backend && mvn spring-boot:run`

### Frontend
1. `cd frontend && npm run lint`
2. `cd frontend && npm run build`
3. `cd frontend && npm run dev`

### Infra
1. `cd backend && docker compose up -d postgres redis`
2. `cd backend && docker compose ps`

---

## My Final Definition of Done

I will call this project complete when:
1. Auth, user, and admin APIs are fully implemented and secured.
2. Frontend is fully connected to backend.
3. Refresh/logout/password/OAuth flows are stable.
4. Tests and quality checks pass.
5. Another developer can run project using docs + env examples.
