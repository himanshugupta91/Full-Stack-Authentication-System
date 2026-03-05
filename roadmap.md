# Backend Implementation-First Roadmap (Only Existing Code Files)

This plan is backend-only and uses only files that already exist in this project.
I will follow implementation-first order: write method code first, then controller wiring, then testing.

Project root: `/Users/himanshu/Downloads/Full-Stack-Authentication-System`

---

## Rules I Will Follow

1. I will only use existing files/classes from this repository.
2. For each feature: implement service/support methods first, then connect controller, then test.
3. I will not add placeholder files in this roadmap.
4. I will not move to next feature until current feature passes manual/API checks.

---

## Week Plan (Implementation-First)

## Day 1 - Base Runtime + Security Foundation

### Task 1: Runtime config and dependencies
Files:
- `backend/src/main/resources/application.properties`
- `backend/.env.example`
- `backend/docker-compose.yaml`

Work:
1. Configure DB/Redis/Mail/JWT/OAuth env keys.
2. Start postgres + redis and verify health.

### Task 2: Security and bootstrap config
Files:
- `backend/src/main/java/com/auth/config/SecurityConfig.java`
- `backend/src/main/java/com/auth/config/CorsConfig.java`
- `backend/src/main/java/com/auth/config/PasswordConfig.java`
- `backend/src/main/java/com/auth/config/SpringDataWebConfig.java`
- `backend/src/main/java/com/auth/config/DataInitializer.java`

Work:
1. Implement filter chain authorization rules.
2. Implement CORS allowed origin logic.
3. Implement password encoder bean.
4. Implement role/admin seed logic.

### Day 1 check
1. App boots.
2. `/api/auth/**` works without token.
3. `/api/user/**` and `/api/admin/**` require auth.

---

## Day 2 - User/Role Model + Register Flow

### Task 1: Entity and repository implementation
Files:
- `backend/src/main/java/com/auth/entity/User.java`
- `backend/src/main/java/com/auth/entity/Role.java`
- `backend/src/main/java/com/auth/repository/UserRepository.java`
- `backend/src/main/java/com/auth/repository/RoleRepository.java`

Work:
1. Ensure user has fields for OTP/reset/refresh/lock counters.
2. Ensure role relation and timestamps work.
3. Implement required repository lookups.

### Task 2: Support/service methods for register
Files:
- `backend/src/main/java/com/auth/service/support/OtpService.java`
- `backend/src/main/java/com/auth/service/support/TokenHashService.java`
- `backend/src/main/java/com/auth/service/support/PasswordPolicyService.java`
- `backend/src/main/java/com/auth/service/support/EmailService.java`
- `backend/src/main/java/com/auth/service/impl/RoleServiceImpl.java`
- `backend/src/main/java/com/auth/service/impl/UserServiceImpl.java`
- `backend/src/main/java/com/auth/service/impl/AuthServiceImpl.java`

Methods to implement first:
1. `OtpService.generateOtp()`
2. `TokenHashService.hash(...)`, `TokenHashService.matches(...)`
3. `PasswordPolicyService.validate(...)`
4. `EmailService.sendOtpEmail(...)`
5. `RoleServiceImpl.findOrCreateRole(...)`
6. `UserServiceImpl.existsByEmail(...)`, `UserServiceImpl.save(...)`
7. `AuthServiceImpl.register(...)`

### Task 3: Controller wiring for register
Files:
- `backend/src/main/java/com/auth/controller/AuthController.java`

Method to wire:
1. `register(...)`

### Day 2 check
1. `POST /api/auth/register` creates disabled user.
2. Password is encoded.
3. OTP hash + expiry are saved.

---

## Day 3 - OTP Verify + Login + JWT Issue

### Task 1: Token/security implementation
Files:
- `backend/src/main/java/com/auth/security/jwt/JwtUtil.java`
- `backend/src/main/java/com/auth/security/CustomUserDetailsService.java`
- `backend/src/main/java/com/auth/security/RefreshTokenCookieService.java`
- `backend/src/main/java/com/auth/service/auth/AuthTokenService.java`

Methods to implement first:
1. `JwtUtil.generateTokenFromEmail(...)`
2. `JwtUtil.getEmailFromToken(...)`
3. `JwtUtil.validateToken(...)`
4. `CustomUserDetailsService.loadUserByUsername(...)`
5. `RefreshTokenCookieService.buildRefreshTokenCookie(...)`
6. `AuthTokenService.issueTokens(...)`

### Task 2: Auth service logic implementation
Files:
- `backend/src/main/java/com/auth/service/impl/AuthServiceImpl.java`

Methods:
1. `verifyOtp(...)`
2. `login(...)`

### Task 3: Controller wiring
Files:
- `backend/src/main/java/com/auth/controller/AuthController.java`

Methods:
1. `verifyOtp(...)`
2. `login(...)`

### Day 3 check
1. `POST /api/auth/verify-otp` enables account.
2. `POST /api/auth/login` returns access token and sets refresh cookie.

---

## Day 4 - Refresh/Logout + Password Flows

### Task 1: Refresh/logout implementation
Files:
- `backend/src/main/java/com/auth/service/auth/AuthTokenService.java`
- `backend/src/main/java/com/auth/controller/AuthController.java`

Methods to implement first:
1. `AuthTokenService.refreshTokens(...)`
2. `AuthTokenService.revokeRefreshToken(...)`
3. `AuthController.refreshToken(...)`
4. `AuthController.logout(...)`

### Task 2: Reset/update password implementation
Files:
- `backend/src/main/java/com/auth/service/impl/AuthServiceImpl.java`
- `backend/src/main/java/com/auth/service/support/EmailService.java`

Methods:
1. `AuthServiceImpl.resetPassword(...)`
2. `AuthServiceImpl.updatePassword(...)`
3. `EmailService.sendPasswordResetEmail(...)`
4. `EmailService.sendPasswordChangedConfirmationEmail(...)`

### Task 3: Change password implementation
Files:
- `backend/src/main/java/com/auth/service/impl/AuthServiceImpl.java`
- `backend/src/main/java/com/auth/controller/UserController.java`

Methods:
1. `AuthServiceImpl.changePassword(...)`
2. `UserController.changePassword(...)`

### Task 4: Controller wiring for remaining auth endpoints
Files:
- `backend/src/main/java/com/auth/controller/AuthController.java`

Methods:
1. `resetPassword(...)`
2. `updatePassword(...)`
3. `resendOtp(...)`

### Day 4 check
1. Refresh rotates token.
2. Logout clears refresh state.
3. Reset/update/change password paths work.

---

## Day 5 - Abuse Protection + JWT Request Filter + Exceptions

### Task 1: Rate limit and abuse implementation
Files:
- `backend/src/main/java/com/auth/service/support/RateLimitService.java`
- `backend/src/main/java/com/auth/service/auth/AuthAbuseProtectionService.java`
- `backend/src/main/java/com/auth/service/impl/AuthServiceImpl.java`

Methods to implement first:
1. `RateLimitService.consume(...)`
2. `AuthAbuseProtectionService.guardLoginAttempt(...)`
3. `AuthAbuseProtectionService.recordFailedLogin(...)`
4. `AuthAbuseProtectionService.guardOtpVerification(...)`
5. `AuthAbuseProtectionService.recordFailedOtp(...)`
6. `AuthAbuseProtectionService.guardResendOtp(...)`
7. `AuthAbuseProtectionService.guardResetPassword(...)`
8. Integrate guards in `AuthServiceImpl` methods.

### Task 2: JWT request filter implementation
Files:
- `backend/src/main/java/com/auth/security/jwt/JwtAuthFilter.java`
- `backend/src/main/java/com/auth/config/SecurityConfig.java`

Methods:
1. `JwtAuthFilter.doFilterInternal(...)`
2. Register filter in `SecurityConfig.filterChain(...)`

### Task 3: Exception handling implementation
Files:
- `backend/src/main/java/com/auth/exception/GlobalExceptionHandler.java`
- `backend/src/main/java/com/auth/exception/AccountLockedException.java`
- `backend/src/main/java/com/auth/exception/RateLimitExceededException.java`
- `backend/src/main/java/com/auth/exception/TokenValidationException.java`
- `backend/src/main/java/com/auth/exception/UserAlreadyExistsException.java`
- `backend/src/main/java/com/auth/exception/ResourceNotFoundException.java`

Methods:
1. Implement handlers in `GlobalExceptionHandler`.
2. Ensure lock/rate-limit responses include retry info.

### Day 5 check
1. Failed login/OTP attempts trigger guards.
2. Protected routes require valid bearer token.
3. Error response format is consistent.

---

## Day 6 - OAuth2 + User/Admin APIs

### Task 1: OAuth2 provisioning and handlers
Files:
- `backend/src/main/java/com/auth/service/auth/OAuth2UserProvisioningService.java`
- `backend/src/main/java/com/auth/security/oauth2/OAuth2AuthenticationSuccessHandler.java`
- `backend/src/main/java/com/auth/security/oauth2/OAuth2AuthenticationFailureHandler.java`
- `backend/src/main/java/com/auth/security/oauth2/LinkedInAuthorizationRequestResolver.java`

Methods to implement first:
1. `OAuth2UserProvisioningService.loadOrCreateUser(...)`
2. `OAuth2AuthenticationSuccessHandler.onAuthenticationSuccess(...)`
3. `OAuth2AuthenticationFailureHandler.onAuthenticationFailure(...)`
4. `LinkedInAuthorizationRequestResolver.resolve(...)` (both overloads)

### Task 2: User portal service and controller
Files:
- `backend/src/main/java/com/auth/service/impl/UserPortalServiceImpl.java`
- `backend/src/main/java/com/auth/controller/UserController.java`

Methods:
1. `UserPortalServiceImpl.getDashboard(...)`
2. `UserPortalServiceImpl.getProfile(...)`
3. `UserController.getDashboard(...)`
4. `UserController.getProfile(...)`

### Task 3: Admin service and controller
Files:
- `backend/src/main/java/com/auth/service/impl/AdminServiceImpl.java`
- `backend/src/main/java/com/auth/controller/AdminController.java`

Methods:
1. `AdminServiceImpl.getDashboard(...)`
2. `AdminServiceImpl.getUsers(...)`
3. `AdminController.getDashboard(...)`
4. `AdminController.getAllUsers(...)`

### Day 6 check
1. OAuth2 success/failure flow works.
2. User dashboard/profile endpoints work.
3. Admin dashboard/users endpoints work with filter/sort/page.

---

## Day 7 - Backend Test Pass + Final QA

### Task 1: Complete tests
Files:
- `backend/src/test/java/com/auth/controller/AuthControllerTest.java`
- `backend/src/test/java/com/auth/service/impl/AuthServiceImplTest.java`
- `backend/src/test/java/com/auth/service/auth/AuthTokenServiceTest.java`
- `backend/src/test/java/com/auth/service/auth/OAuth2UserProvisioningServiceTest.java`
- `backend/src/test/java/com/auth/service/impl/AdminServiceImplTest.java`
- `backend/src/test/java/com/auth/service/impl/UserServiceImplTest.java`
- `backend/src/test/java/com/auth/security/CustomUserDetailsServiceTest.java`

Work:
1. Run tests.
2. Fix failing behavior.
3. Re-run to green.

### Task 2: Manual backend regression
Flows to validate:
1. Register -> OTP verify -> login.
2. Refresh -> logout.
3. Reset password -> update password.
4. Authenticated change password.
5. Abuse guard and lockout.
6. OAuth2 login path.
7. User and admin API access by role.

### Task 3: Release readiness checks
Files:
- `backend/Dockerfile`
- `backend/docker-compose.yaml`
- `.github/workflows/secret-scan.yml`

Work:
1. Build backend image.
2. Verify compose-based startup.
3. Verify secret scan workflow config.

### Day 7 check
1. Tests pass.
2. Manual flows pass.
3. Backend is deploy-ready.

---

## Method Checklist (Only Existing Backend Classes)

## Controllers

### `AuthController`
1. `register(...)` -> create new user via service.
2. `verifyOtp(...)` -> activate user via OTP.
3. `login(...)` -> authenticate and set refresh cookie.
4. `refreshToken(...)` -> rotate refresh token and return new access token.
5. `logout(...)` -> revoke refresh token and clear cookie.
6. `resetPassword(...)` -> trigger reset-link flow.
7. `updatePassword(...)` -> set new password using reset token.
8. `resendOtp(...)` -> regenerate and send OTP.

### `UserController`
1. `getDashboard(...)` -> return user dashboard DTO.
2. `getProfile(...)` -> return profile DTO.
3. `changePassword(...)` -> change logged-in user password.

### `AdminController`
1. `getDashboard(...)` -> return admin metrics DTO.
2. `getAllUsers(...)` -> return paginated/filterable users.

## Service Implementations

### `AuthServiceImpl`
1. `register(...)` -> create disabled user + OTP hash+expiry.
2. `verifyOtp(...)` -> validate OTP and enable account.
3. `login(...)` -> validate credentials and issue tokens.
4. `resetPassword(...)` -> create reset hash+expiry and send email.
5. `updatePassword(...)` -> validate reset token and update password.
6. `resendOtp(...)` -> enforce resend guard and send new OTP.
7. `changePassword(...)` -> verify current password and update.

### `UserServiceImpl`
1. `getUserByEmail(...)`
2. `findByEmail(...)`
3. `existsByEmail(...)`
4. `save(...)`
5. `findByResetToken(...)`
6. `findByRefreshToken(...)`
7. `findByAuthProviderAndAuthProviderUserId(...)`

### `RoleServiceImpl`
1. `findOrCreateRole(...)`
2. `findByName(...)`

### `UserPortalServiceImpl`
1. `getDashboard(...)`
2. `getProfile(...)`

### `AdminServiceImpl`
1. `getDashboard(...)`
2. `getUsers(...)`

## Token/Security/Support

### `AuthTokenService`
1. `issueTokens(...)`
2. `refreshTokens(...)`
3. `revokeRefreshToken(...)`

### `AuthAbuseProtectionService`
1. `guardLoginAttempt(...)`
2. `recordFailedLogin(...)`
3. `clearLoginFailures(...)`
4. `guardOtpVerification(...)`
5. `recordFailedOtp(...)`
6. `clearOtpFailures(...)`
7. `guardResendOtp(...)`
8. `guardResetPassword(...)`

### `RateLimitService`
1. `consume(...)`

### `TokenHashService`
1. `hash(...)`
2. `matches(...)`

### `OtpService`
1. `generateOtp()`
2. `generateResetToken()`

### `PasswordPolicyService`
1. `validate(...)`

### `EmailService`
1. `sendOtpEmail(...)`
2. `sendPasswordResetEmail(...)`
3. `sendWelcomeEmail(...)`
4. `sendPasswordChangedConfirmationEmail(...)`
5. `sendAccountLockedAlertEmail(...)`

### `JwtUtil`
1. `generateToken(...)`
2. `generateTokenFromEmail(...)`
3. `getEmailFromToken(...)`
4. `validateToken(...)`

### `JwtAuthFilter`
1. `doFilterInternal(...)`

### `CustomUserDetailsService`
1. `loadUserByUsername(...)`

### `RefreshTokenCookieService`
1. `buildRefreshTokenCookie(...)`
2. `clearRefreshTokenCookie()`
3. `getCookieName()`

### `OAuth2UserProvisioningService`
1. `loadOrCreateUser(...)`

### `OAuth2AuthenticationSuccessHandler`
1. `onAuthenticationSuccess(...)`

### `OAuth2AuthenticationFailureHandler`
1. `onAuthenticationFailure(...)`

### `LinkedInAuthorizationRequestResolver`
1. `resolve(request)`
2. `resolve(request, clientRegistrationId)`

---

## Daily Command Checklist (Backend)

1. `cd backend && docker compose up -d postgres redis`
2. `cd backend && mvn -q -DskipTests compile`
3. `cd backend && mvn -q test`
4. `cd backend && mvn spring-boot:run`

---

## Backend Definition of Done

1. All backend auth/user/admin endpoints implemented and tested.
2. JWT + refresh-token + logout revocation stable.
3. Password and OTP flows stable.
4. Abuse protection and lockouts verified.
5. OAuth2 flow integrated with backend token model.
6. Backend test suite passes.
