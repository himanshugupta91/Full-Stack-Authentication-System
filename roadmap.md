# 1-Week Backend Implementation Plan (From Scratch, Feature-Wise)

This is a practical build order for implementing your backend from zero.
Target stack: Spring Boot + Spring Security + JWT + PostgreSQL + Redis + Mail + OAuth2.

## Ground Rules (Before Day 1)
1. Keep code in layers: `controller -> service -> repository -> entity`.
2. Keep all secrets in local env/properties, never in git.
3. Build one feature completely (API + service + persistence + test) before moving to next.
4. At end of each day, run smoke tests for all features completed so far.

---

## Day 1 - Project Bootstrapping + Core Infrastructure

### Feature Goal
Run backend locally with DB/Redis and base security wiring.

### Tasks (in order)
1. Create Spring Boot project with dependencies:
- Web, Security, Validation, Data JPA, PostgreSQL, Redis, Mail, OAuth2 Client, Lombok, MapStruct, Test.

2. Configure local infrastructure:
- Add `docker-compose.yaml` with `postgres` and `redis`.
- Add optional tools (`adminer`, `redis-commander`) if needed.

3. Add backend runtime config:
- Create local properties from `application.properties.example`.
- Configure datasource, redis, mail, jwt, cors, cookie, oauth placeholders.

4. Add startup config classes:
- `SecurityConfig`, `CorsConfig`, `PasswordConfig`, `SpringDataWebConfig`.

5. Add startup seed logic:
- `DataInitializer` to seed `ROLE_USER`, `ROLE_ADMIN`, and optional admin user.

6. Run backend:
- Start infra containers.
- Start Spring app.
- Confirm app boot without bean/config errors.

### Files to implement/check
- `backend/pom.xml`
- `backend/docker-compose.yaml`
- `backend/src/main/resources/application.properties`
- `backend/src/main/java/com/auth/config/SecurityConfig.java`
- `backend/src/main/java/com/auth/config/CorsConfig.java`
- `backend/src/main/java/com/auth/config/PasswordConfig.java`
- `backend/src/main/java/com/auth/config/SpringDataWebConfig.java`
- `backend/src/main/java/com/auth/config/DataInitializer.java`

### End-of-day success check
1. App starts.
2. DB + Redis connected.
3. Public route `/api/auth/**` accessible.

---

## Day 2 - User Domain + Registration + OTP Send

### Feature Goal
User can register and receive OTP email.

### Tasks (in order)
1. Build domain model:
- Create `User` and `Role` entities.
- Add user-role relation and audit timestamps.

2. Build repositories:
- `UserRepository`, `RoleRepository`.

3. Build DTO contracts:
- Request DTOs: `RegisterRequest`, `OtpVerifyRequest`, `LoginRequest`.
- Response DTO: `MessageResponse`, `AuthResponse`, `AuthTokens`.

4. Build mapper:
- `UserMapper` for entity/DTO conversion.

5. Build registration service path:
- Add `AuthService` interface.
- In `AuthServiceImpl.register(...)`:
  - normalize email
  - check duplicate
  - validate password policy
  - hash password
  - generate OTP
  - hash OTP
  - set expiry
  - assign `ROLE_USER`
  - save user
  - send OTP email

6. Build OTP and email support:
- `OtpService` for OTP/reset token generation.
- `EmailService.sendOtpEmail(...)`.

7. Expose endpoint:
- `POST /api/auth/register`.

### Files to implement/check
- `backend/src/main/java/com/auth/entity/User.java`
- `backend/src/main/java/com/auth/entity/Role.java`
- `backend/src/main/java/com/auth/repository/UserRepository.java`
- `backend/src/main/java/com/auth/repository/RoleRepository.java`
- `backend/src/main/java/com/auth/mapper/UserMapper.java`
- `backend/src/main/java/com/auth/service/AuthService.java`
- `backend/src/main/java/com/auth/service/impl/AuthServiceImpl.java`
- `backend/src/main/java/com/auth/service/support/OtpService.java`
- `backend/src/main/java/com/auth/service/support/EmailService.java`
- `backend/src/main/java/com/auth/controller/AuthController.java`

### End-of-day success check
1. Register API returns success.
2. User saved as `enabled=false`.
3. OTP hash + expiry stored.

---

## Day 3 - OTP Verify + Login + JWT Access Token

### Feature Goal
User can verify account and login to receive access token + refresh cookie.

### Tasks (in order)
1. Implement OTP verification:
- In `AuthServiceImpl.verifyOtp(...)`:
  - load user
  - ensure not already verified
  - compare raw OTP with stored hash
  - check expiry
  - enable account
  - clear OTP fields
  - send welcome email

2. Implement JWT core:
- `JwtUtil` for generate/validate/extract email.

3. Implement Spring auth user details:
- `CustomUserDetailsService` map user roles -> authorities.

4. Implement login flow:
- In `AuthServiceImpl.login(...)`:
  - authenticate via `AuthenticationManager`
  - issue tokens via `AuthTokenService`

5. Implement refresh-token cookie service:
- `RefreshTokenCookieService` for secure cookie headers.

6. Expose endpoints:
- `POST /api/auth/verify-otp`
- `POST /api/auth/login`

### Files to implement/check
- `backend/src/main/java/com/auth/security/jwt/JwtUtil.java`
- `backend/src/main/java/com/auth/security/CustomUserDetailsService.java`
- `backend/src/main/java/com/auth/service/auth/AuthTokenService.java`
- `backend/src/main/java/com/auth/security/RefreshTokenCookieService.java`
- `backend/src/main/java/com/auth/controller/AuthController.java`
- `backend/src/main/java/com/auth/service/impl/AuthServiceImpl.java`

### End-of-day success check
1. Verify OTP enables account.
2. Login returns access token.
3. Login sets refresh token HttpOnly cookie.

---

## Day 4 - Refresh + Logout + Password Reset Flow

### Feature Goal
Complete token lifecycle and password recovery.

### Tasks (in order)
1. Implement refresh token rotation:
- In `AuthTokenService.refreshTokens(...)`:
  - hash incoming refresh token
  - lookup user by hash
  - check expiry
  - rotate token

2. Implement logout:
- Revoke refresh token in DB.
- Clear refresh cookie in response.

3. Implement forgot-password request:
- `resetPassword(...)`:
  - always return generic success message
  - for existing user: generate reset token, store hash + expiry, send mail link

4. Implement set-new-password endpoint:
- `updatePassword(...)`:
  - hash incoming reset token
  - lookup user
  - check expiry
  - validate policy
  - update encoded password
  - clear reset token fields

5. Implement authenticated password change:
- `changePassword(...)` with current-password check.

6. Expose endpoints:
- `POST /api/auth/refresh`
- `POST /api/auth/logout`
- `POST /api/auth/reset-password`
- `POST /api/auth/update-password`
- `POST /api/user/change-password`

### Files to implement/check
- `backend/src/main/java/com/auth/service/auth/AuthTokenService.java`
- `backend/src/main/java/com/auth/controller/AuthController.java`
- `backend/src/main/java/com/auth/service/impl/AuthServiceImpl.java`
- `backend/src/main/java/com/auth/controller/UserController.java`
- `backend/src/main/java/com/auth/service/support/TokenHashService.java`
- `backend/src/main/java/com/auth/service/support/PasswordPolicyService.java`

### End-of-day success check
1. Refresh gives new access + new refresh cookie.
2. Logout invalidates refresh token.
3. Reset flow works end-to-end.

---

## Day 5 - Abuse Protection + JWT Filter Security

### Feature Goal
Protect auth endpoints and secure protected APIs.

### Tasks (in order)
1. Implement Redis fixed-window limiter:
- `RateLimitService.consume(...)`.

2. Implement abuse protection coordinator:
- `AuthAbuseProtectionService` methods:
  - `guardLoginAttempt`
  - `recordFailedLogin`
  - `guardOtpVerification`
  - `recordFailedOtp`
  - `guardResendOtp`
  - `guardResetPassword`

3. Add account lock strategy:
- failed login attempts -> lock account until time.
- failed OTP attempts -> lock OTP verification until time.

4. Build JWT auth filter:
- `JwtAuthFilter` parse bearer token, validate, load user, set security context.

5. Ensure route protection works:
- `/api/auth/**` public.
- `/api/user/**` requires USER/ADMIN.
- `/api/admin/**` requires ADMIN.

6. Add resend OTP endpoint:
- `POST /api/auth/resend-otp` with abuse guard.

### Files to implement/check
- `backend/src/main/java/com/auth/service/support/RateLimitService.java`
- `backend/src/main/java/com/auth/service/auth/AuthAbuseProtectionService.java`
- `backend/src/main/java/com/auth/security/jwt/JwtAuthFilter.java`
- `backend/src/main/java/com/auth/config/SecurityConfig.java`
- `backend/src/main/java/com/auth/controller/AuthController.java`

### End-of-day success check
1. Repeated failed logins trigger lock and `Retry-After` behavior.
2. Protected routes reject missing/invalid JWT.
3. Valid JWT allows user/admin routes by role.

---

## Day 6 - OAuth2 Login + User/Admin Dashboards

### Feature Goal
Add OAuth2 social login and dashboard APIs.

### Tasks (in order)
1. Add OAuth2 provider config placeholders:
- Google, GitHub, Apple, LinkedIn in properties.

2. Implement OAuth2 provisioning service:
- `OAuth2UserProvisioningService.loadOrCreateUser(...)`:
  - normalize provider
  - extract provider user id
  - resolve email/name with fallback strategy
  - create or update local user

3. Implement OAuth2 success/failure handlers:
- Success: issue tokens + set refresh cookie + redirect frontend callback.
- Failure: redirect frontend login with error.

4. Add LinkedIn request resolver customization:
- remove nonce for compatibility path.

5. Implement user portal APIs:
- `GET /api/user/dashboard`
- `GET /api/user/profile`

6. Implement admin APIs:
- `GET /api/admin/dashboard`
- `GET /api/admin/users` with paging/filtering/search/sort.

### Files to implement/check
- `backend/src/main/java/com/auth/service/auth/OAuth2UserProvisioningService.java`
- `backend/src/main/java/com/auth/security/oauth2/OAuth2AuthenticationSuccessHandler.java`
- `backend/src/main/java/com/auth/security/oauth2/OAuth2AuthenticationFailureHandler.java`
- `backend/src/main/java/com/auth/security/oauth2/LinkedInAuthorizationRequestResolver.java`
- `backend/src/main/java/com/auth/controller/UserController.java`
- `backend/src/main/java/com/auth/controller/AdminController.java`
- `backend/src/main/java/com/auth/service/impl/UserPortalServiceImpl.java`
- `backend/src/main/java/com/auth/service/impl/AdminServiceImpl.java`

### End-of-day success check
1. OAuth2 login creates/links user and redirects correctly.
2. Admin user listing works with filters and safe sorting.

---

## Day 7 - Hardening, Exceptions, Testing, and Release Readiness

### Feature Goal
Stabilize and prepare for deployment.

### Tasks (in order)
1. Implement global exception standardization:
- Add `GlobalExceptionHandler` mappings for validation/auth/lock/rate-limit/server errors.

2. Complete email templates:
- OTP, welcome, reset, password changed, account locked.

3. Add/finish unit tests:
- Auth controller
- Auth service
- Auth token service
- OAuth2 provisioning service
- Admin service
- User service
- User details service

4. Run full test suite and fix failing cases.

5. Build container image and boot with compose:
- verify app + db + redis integrated startup.

6. Final verification checklist:
- register -> verify -> login -> refresh -> logout
- forgot password -> update password
- resend OTP + lockout paths
- user dashboard
- admin dashboard/users
- oauth2 login flow

### Files to implement/check
- `backend/src/main/java/com/auth/exception/GlobalExceptionHandler.java`
- `backend/src/test/java/com/auth/controller/AuthControllerTest.java`
- `backend/src/test/java/com/auth/service/impl/AuthServiceImplTest.java`
- `backend/src/test/java/com/auth/service/auth/AuthTokenServiceTest.java`
- `backend/src/test/java/com/auth/service/auth/OAuth2UserProvisioningServiceTest.java`
- `backend/src/test/java/com/auth/service/impl/AdminServiceImplTest.java`
- `backend/src/test/java/com/auth/service/impl/UserServiceImplTest.java`
- `backend/src/test/java/com/auth/security/CustomUserDetailsServiceTest.java`

### End-of-day success check
1. Tests are green or known failures are documented with root cause.
2. Full auth feature set works end-to-end.
3. You can deploy confidently to staging.

---

## Quick Daily Execution Template (Use Every Day)
1. Implement feature tasks in listed order.
2. Run only related tests first.
3. Smoke test APIs for that feature.
4. Commit with clear message.
5. Write short notes: completed, blocked, next day focus.

---

## Suggested Git Commit Plan (Optional)
1. `day1: bootstrap backend config + infra`
2. `day2: registration + otp send`
3. `day3: otp verify + login + jwt core`
4. `day4: refresh logout password reset/change`
5. `day5: abuse protection + jwt auth filter`
6. `day6: oauth2 + user/admin dashboards`
7. `day7: tests + exception handling + release hardening`
