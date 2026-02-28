# Spring Boot Backend Layered Implementation Guide (Step by Step)

This guide explains exactly how to build this backend yourself using classic layered architecture (MVC/layered), in the correct order, with package creation sequence, file sequence, methods, and logic.

## 1) Target Architecture (Layered/MVC)

Use this package structure first:

```text
com.auth
├── AuthApplication.java
├── config
├── controller
├── dto
├── entity
├── exception
├── mapper
├── repository
├── security
├── service
└── service/impl
```

Layer responsibility:

- `controller`: request/response endpoints only.
- `service`: business contracts.
- `service.impl`: business logic implementation.
- `repository`: persistence access only.
- `entity`: JPA domain model.
- `dto`: API input/output models.
- `security`: JWT/OAuth2 filters/handlers/utilities.
- `config`: Spring beans/security/CORS/bootstrap data.
- `exception`: custom exceptions + global error mapping.
- `mapper`: entity <-> dto conversion.

Lombok convention for this backend:

- Use `@RequiredArgsConstructor` on Spring beans (`@Service`, `@Controller`, `@Component`, selected `@Configuration`) and declare injected dependencies as `final`.
- Do not use field injection (`@Autowired` on fields).
- Use `@Data` (or `@Getter/@Setter`) for DTOs and simple model holders.
- Use `@Slf4j` where structured logging is needed.

## 2) Build Order (Important)

Follow this order to avoid circular compile issues:

1. Project bootstrap (`AuthApplication`, `pom.xml`, dependencies)
2. Package folders
3. Entities (`Role`, `User`)
4. Repositories (`RoleRepository`, `UserRepository`)
5. DTOs
6. Service interfaces (`AuthService`, `UserService`, `RoleService`)
7. Core utility services (`OtpService`, `TokenHashService`, `PasswordPolicyService`, `RateLimitService`, `EmailService`)
8. Service implementations (`UserServiceImpl`, `RoleServiceImpl`, `AuthTokenService`, `OAuth2UserProvisioningService`, `AuthAbuseProtectionService`, `AuthServiceImpl`)
9. Security classes (`JwtUtil`, `CustomUserDetailsService`, `JwtAuthFilter`, OAuth handlers, cookie service)
10. Config classes (`PasswordConfig`, `CorsConfig`, `SecurityConfig`, `DataInitializer`)
11. Controllers (`AuthController`, `UserController`, `AdminController`)
12. Exceptions (`*Exception`, `GlobalExceptionHandler`)
13. `application.properties`
14. Build and run checks

---

## 3) Step-by-Step Implementation

## Step 0: Initialize Project

Create Spring Boot project with:

- Java 21
- Spring Web
- Spring Security
- Spring Data JPA
- Validation
- Mail
- OAuth2 Client
- Data Redis
- PostgreSQL driver
- Lombok
- MapStruct
- JWT (`jjwt-api`, `jjwt-impl`, `jjwt-jackson`)

Main class:

- File: `com.auth.AuthApplication`
- Method:
- `public static void main(String[] args)`
- Logic: start Spring context.

---

## Step 1: Create Entities First

### 1.1 `entity/Role.java`

Create:

- Enum `RoleName { ROLE_USER, ROLE_ADMIN }`
- Fields:
- `id`
- `name` (enum)

Purpose:

- Role vocabulary for authorization.

### 1.2 `entity/User.java`

Create fields:

- Identity/profile: `id`, `name`, `email`, `password`, `enabled`
- Verification: `verificationOtp`, `otpExpiry`
- Password reset: `resetToken`, `resetTokenExpiry`
- Session: `refreshToken`, `refreshTokenExpiry`
- Abuse tracking: `failedLoginAttempts`, `accountLockedUntil`, `failedOtpAttempts`, `otpLockedUntil`
- OAuth provider: `authProvider`
- Auditing: `createdAt`, `updatedAt`
- Roles relationship:
- `@ManyToMany(fetch = EAGER)` with `user_roles`

Methods:

- `@PrePersist onCreate()` set `createdAt` and `updatedAt`
- `@PreUpdate onUpdate()` update `updatedAt`

Logic:

- Keep all authentication state on user record.
- Persist only hashed token values, never raw refresh/reset/OTP.

---

## Step 2: Repositories

### 2.1 `repository/RoleRepository.java`

- Extend `JpaRepository<Role, Long>`
- Method:
- `Optional<Role> findByName(Role.RoleName name)`

### 2.2 `repository/UserRepository.java`

- Extend `JpaRepository<User, Long>` + `JpaSpecificationExecutor<User>`

Methods:

- `Optional<User> findByEmail(String email)`
- `boolean existsByEmail(String email)`
- `Optional<User> findByResetToken(String resetToken)`
- `Optional<User> findByRefreshToken(String refreshToken)`
- `long countByEnabledTrue()`

Logic:

- `JpaSpecificationExecutor` is needed for admin filters/search/pagination.

---

## Step 3: Create DTOs

Create all DTOs under `dto` package.

### 3.1 Auth request DTOs

- `RegisterRequest`:
- `name`, `email`, `password`
- validations: `@NotBlank`, `@Email`, password `@Size(min=12)`

- `LoginRequest`:
- `email`, `password`

- `OtpVerifyRequest`:
- `email`, `otp`

- `ResetPasswordRequest`:
- `email`

- `UpdatePasswordRequest`:
- `token`, `newPassword` (`min=12`)

- `TokenRefreshRequest`:
- `refreshToken` (optional fallback when cookie not used)

- `ChangePasswordRequest`:
- `currentPassword`, `newPassword`

### 3.2 Auth response DTOs

- `AuthResponse` fields:
- `accessToken`, `tokenType`, `accessTokenExpiresInMs`, `refreshTokenExpiresInMs`
- user data: `id`, `name`, `email`, `roles`

- `AuthTokens`:
- record wrapper with `AuthResponse response` + raw `refreshToken`

### 3.3 Dashboard and common DTOs

- `UserDto` (id, name, email, roles, enabled, createdAt)
- `UserDashboardDto`
- `AdminDashboardDto`
- `MessageResponse` (`message`, `success`)

Logic:

- Controllers should never directly expose entities.

---

## Step 4: Mapper

### `mapper/UserMapper.java`

MapStruct interface methods:

- `UserDto toDto(User user)`
- `User toEntity(RegisterRequest request)`
- `UserDashboardDto toUserDashboardDto(User user)`

Mapping logic:

- Convert `Set<Role>` to `Set<String>` role names
- Ignore sensitive fields (password/token hashes)

---

## Step 5: Service Interfaces

### `service/UserService.java`

Methods:

- `User getUserByEmail(String email)`
- `Optional<User> findByEmail(String email)`
- `boolean existsByEmail(String email)`
- `User save(User user)`
- `Optional<User> findByResetToken(String token)`
- `Optional<User> findByRefreshToken(String token)`

### `service/RoleService.java`

- `Role findOrCreateRole(Role.RoleName roleName)`
- `Optional<Role> findByName(Role.RoleName roleName)`

### `service/AuthService.java`

- `MessageResponse register(RegisterRequest request)`
- `MessageResponse verifyOtp(OtpVerifyRequest request)`
- `AuthTokens login(LoginRequest request)`
- `MessageResponse resetPassword(ResetPasswordRequest request)`
- `MessageResponse updatePassword(UpdatePasswordRequest request)`
- `MessageResponse resendOtp(String email)`
- `MessageResponse changePassword(String email, ChangePasswordRequest request)`

---

## Step 6: Utility/Support Services

### 6.1 `service/OtpService.java`

Methods:

- `generateOtp()` -> 6-digit random number
- `generateResetToken()` -> secure random token

Logic:

- OTP must be short and user-friendly.
- Reset token must be high entropy.

### 6.2 `service/TokenHashService.java`

Methods:

- `hash(String rawToken)` using SHA-256 + pepper
- `matches(String rawToken, String tokenHash)` constant-time compare

Logic:

- Store only hash of OTP/reset/refresh tokens.

### 6.3 `service/PasswordPolicyService.java`

Method:

- `validate(String password, String emailHint)`

Rules:

- min length 12
- uppercase + lowercase + digit + symbol
- no whitespace
- common-password blocklist
- should not include email local-part

### 6.4 `service/RateLimitService.java`

Method:

- `RateLimitDecision consume(String key, long limit, Duration window)`

Logic:

- Redis fixed-window counter using `increment` + `expire`
- return allowed/blocked + retry-after seconds
- fail-open policy on Redis outage

### 6.5 `service/EmailService.java`

Methods:

- `sendOtpEmail(String toEmail, String otp)`
- `sendPasswordResetEmail(String toEmail, String resetToken)`

Logic:

- Compose readable messages.
- Keep link format aligned with frontend reset route.

---

## Step 7: Service Implementations

### 7.1 `service/impl/UserServiceImpl.java`

Implement all `UserService` methods via `UserRepository`.

Lombok:

- annotate class with `@RequiredArgsConstructor`
- keep repository dependency `private final UserRepository userRepository`

Key logic:

- `getUserByEmail` throws `ResourceNotFoundException` when absent.

### 7.2 `service/impl/RoleServiceImpl.java`

Implement:

- `findOrCreateRole` uses repository lookup and creates role if missing
- `findByName`

Lombok:

- annotate class with `@RequiredArgsConstructor`
- keep repository dependency `private final RoleRepository roleRepository`

### 7.3 `service/AuthTokenService.java`

Methods:

- `issueTokens(User user)`
- create access token via `JwtUtil`
- generate new refresh token
- store hash in DB with expiry
- return `AuthTokens`

- `refreshTokens(String refreshToken)`
- hash incoming token
- find user by hashed token
- validate expiry
- rotate tokens by calling `issueTokens`

- `revokeRefreshToken(String refreshToken)`
- clear persisted refresh token hash/expiry

### 7.4 `service/OAuth2UserProvisioningService.java`

Method:

- `loadOrCreateUser(OAuth2AuthenticationToken authToken, OAuth2User oauth2User)`

Logic:

- extract provider id
- extract email/name safely (special fallback for GitHub)
- if user exists: enable provider data as needed
- else create enabled local user with random password and ROLE_USER

### 7.5 `service/AuthAbuseProtectionService.java`

Methods:

- `guardLoginAttempt`, `recordFailedLogin`, `clearLoginFailures`
- `guardOtpVerification`, `recordFailedOtp`, `clearOtpFailures`
- `guardResendOtp`, `guardResetPassword`

Logic:

- combine IP + email limits from properties
- use Redis counters for rate limiting
- use DB fields for temporary account/OTP lockouts
- throw `RateLimitExceededException` or `AccountLockedException`

### 7.6 `service/impl/AuthServiceImpl.java`

Main logic class. Implement in this order:

Lombok:

- annotate with `@RequiredArgsConstructor` and `@Slf4j`
- make all injected collaborators `private final ...`
- keep only configuration values under `@Value` as non-final fields

1. `register(RegisterRequest)`
- reject duplicate email
- enforce password policy
- map request to user
- encode password
- generate OTP and hash it
- assign ROLE_USER
- save user
- send OTP email (log warning if email send fails)

2. `verifyOtp(OtpVerifyRequest)`
- apply OTP abuse guards
- load user
- reject already enabled account
- compare incoming OTP with stored hash
- validate OTP expiry
- enable account and clear OTP fields
- clear OTP failures

3. `login(LoginRequest)`
- apply login abuse guards
- load user and ensure enabled
- authenticate with Spring `AuthenticationManager`
- on bad creds record failure
- on success clear failures
- issue access+refresh tokens

4. `resetPassword(ResetPasswordRequest)`
- apply rate limit
- return generic success even when user missing
- generate reset token and store hash+expiry
- send reset email

5. `updatePassword(UpdatePasswordRequest)`
- hash input token and find user
- validate token expiry
- enforce password policy
- encode and update password
- clear reset token fields

6. `resendOtp(String email)`
- apply resend OTP guard
- load user, ensure not already enabled
- generate and hash new OTP with expiry
- send OTP email

7. `changePassword(String email, ChangePasswordRequest)`
- load current user
- validate current password
- validate new password policy
- encode and save new password

---

## Step 8: Security Layer

### 8.1 `security/JwtUtil.java`

Methods:

- `generateToken(Authentication authentication)`
- `generateTokenFromEmail(String email)`
- `getEmailFromToken(String token)`
- `validateToken(String token)`

Logic:

- use `jwt.secret`
- enforce expiry from properties

### 8.2 `security/CustomUserDetailsService.java`

Method:

- `loadUserByUsername(String email)`

Logic:

- fetch user
- convert roles to `SimpleGrantedAuthority`
- return Spring `UserDetails`

Lombok:

- use `@RequiredArgsConstructor` instead of manual constructor.

### 8.3 `security/JwtAuthFilter.java`

Method:

- `doFilterInternal(...)`

Logic:

- parse `Authorization: Bearer ...`
- validate JWT
- load user details
- set authentication in `SecurityContextHolder`

Lombok:

- use `@RequiredArgsConstructor` with final dependencies (`JwtUtil`, `CustomUserDetailsService`).

### 8.4 `security/RefreshTokenCookieService.java`

Methods:

- `buildRefreshTokenCookie(String refreshToken)`
- `clearRefreshTokenCookie()`
- `getCookieName()`

Logic:

- centralized cookie behavior (name/path/secure/samesite/max-age)

### 8.5 OAuth helpers

- `OAuth2AuthenticationSuccessHandler`
- provision/load user
- issue tokens
- set refresh cookie
- redirect frontend to `/oauth2/callback` (without token in URL)

- `OAuth2AuthenticationFailureHandler`
- redirect to frontend login with `oauthError`

- `LinkedInAuthorizationRequestResolver`
- custom LinkedIn nonce handling to avoid mismatch

---

## Step 9: Config Classes

### 9.1 `config/PasswordConfig.java`

- expose `PasswordEncoder` bean (`BCryptPasswordEncoder`)

### 9.2 `config/CorsConfig.java`

- define allowed origins (`localhost:5173`, `localhost:3000`)
- methods, headers, credentials

### 9.3 `config/SecurityConfig.java`

Methods:

- `filterChain(HttpSecurity http)`
- enable CORS
- disable CSRF for stateless API
- authorize:
- `/api/auth/**` permit all
- `/oauth2/**`, `/login/oauth2/**` permit all
- `/api/admin/**` ROLE_ADMIN
- `/api/user/**` ROLE_USER/ROLE_ADMIN
- configure oauth2 handlers/resolver
- add `JwtAuthFilter` before `UsernamePasswordAuthenticationFilter`

- `authenticationManager(...)`
- `authenticationProvider(...)`

### 9.4 `config/DataInitializer.java`

Method:

- `run(String... args)`

Logic:

- ensure `ROLE_USER`, `ROLE_ADMIN` exist
- seed `admin@admin.com` if absent

---

## Step 10: Exception Handling

Create custom exceptions:

- `ResourceNotFoundException`
- `UserAlreadyExistsException`
- `TokenValidationException`
- `RateLimitExceededException` (include retry-after)
- `AccountLockedException` (include retry-after)

### `exception/GlobalExceptionHandler.java`

Implement `@RestControllerAdvice` with handlers for:

- validation errors
- bad credentials
- token/authorization errors
- rate-limit/account-lock errors
- generic `Exception`

Return consistent `MessageResponse` body + proper status codes.

---

## Step 11: Controllers (HTTP API Layer)

### 11.1 `controller/AuthController.java`

Base path: `/api/auth`

Methods:

- `register(RegisterRequest)` -> `POST /register`
- `verifyOtp(OtpVerifyRequest)` -> `POST /verify-otp`
- `login(LoginRequest, HttpServletResponse)` -> `POST /login`
- set refresh cookie
- return `AuthResponse`

- `refreshToken(HttpServletRequest, HttpServletResponse, TokenRefreshRequest)` -> `POST /refresh`
- read token from body first, fallback cookie
- rotate token, set new cookie

- `logout(...)` -> `POST /logout`
- revoke refresh token
- clear cookie

- `resetPassword(ResetPasswordRequest)` -> `POST /reset-password`
- `updatePassword(UpdatePasswordRequest)` -> `POST /update-password`
- `resendOtp(String email)` -> `POST /resend-otp`

Lombok:

- use `@RequiredArgsConstructor` with final service dependencies.

### 11.2 `controller/UserController.java`

Base path: `/api/user`

- `getDashboard()` -> `GET /dashboard`
- `getProfile()` -> `GET /profile`
- `changePassword(ChangePasswordRequest)` -> `POST /change-password`

Use authenticated principal email from `SecurityContextHolder`.

### 11.3 `controller/AdminController.java`

Base path: `/api/admin`, ROLE_ADMIN only

- `getDashboard()` -> counts via repository
- `getAllUsers(page,size,search,enabled,role,sortBy,sortDir)` -> paginated/filterable listing

Important logic:

- normalize page/size with max cap
- safe sortable field allowlist
- role normalization (`USER`/`ADMIN` + prefixed forms)
- specification-based search/filter query

---

## Step 12: `application.properties`

Configure in groups:

1. Server/cookie
2. Datasource (PostgreSQL)
3. Redis
4. Mail
5. JWT (`jwt.secret`, access/refresh expirations)
6. Frontend/backend URLs
7. Refresh cookie settings
8. Abuse-protection thresholds
9. OAuth2 providers
10. OTP expiry

Critical best-practice note:

- keep secrets in environment variables (do not hardcode in git).

---

## Step 13: Build and Verify

Run in this order:

```bash
cd backend
mvn -q -DskipTests compile
mvn -q test
mvn spring-boot:run
```

Recommended smoke checks:

```bash
# expected 401 without token
curl -i -X POST http://localhost:8080/api/auth/refresh

# CORS preflight check
curl -i -X OPTIONS http://localhost:8080/api/auth/register \
  -H 'Origin: http://localhost:5173' \
  -H 'Access-Control-Request-Method: POST'
```

---

## 4) Full File Checklist (Create in this exact package)

### Root

- `AuthApplication.java`

### `config`

- `CorsConfig.java`
- `DataInitializer.java`
- `PasswordConfig.java`
- `SecurityConfig.java`

### `controller`

- `AuthController.java`
- `UserController.java`
- `AdminController.java`

### `dto`

- `AdminDashboardDto.java`
- `AuthResponse.java`
- `AuthTokens.java`
- `ChangePasswordRequest.java`
- `LoginRequest.java`
- `MessageResponse.java`
- `OtpVerifyRequest.java`
- `RegisterRequest.java`
- `ResetPasswordRequest.java`
- `TokenRefreshRequest.java`
- `UpdatePasswordRequest.java`
- `UserDashboardDto.java`
- `UserDto.java`

### `entity`

- `Role.java`
- `User.java`

### `exception`

- `AccountLockedException.java`
- `GlobalExceptionHandler.java`
- `RateLimitExceededException.java`
- `ResourceNotFoundException.java`
- `TokenValidationException.java`
- `UserAlreadyExistsException.java`

### `mapper`

- `UserMapper.java`

### `repository`

- `RoleRepository.java`
- `UserRepository.java`

### `security`

- `CustomUserDetailsService.java`
- `JwtAuthFilter.java`
- `JwtUtil.java`
- `LinkedInAuthorizationRequestResolver.java`
- `OAuth2AuthenticationFailureHandler.java`
- `OAuth2AuthenticationSuccessHandler.java`
- `RefreshTokenCookieService.java`

### `service`

- `AuthAbuseProtectionService.java`
- `AuthService.java`
- `AuthTokenService.java`
- `EmailService.java`
- `OAuth2UserProvisioningService.java`
- `OtpService.java`
- `PasswordPolicyService.java`
- `RateLimitService.java`
- `RoleService.java`
- `TokenHashService.java`
- `UserService.java`

### `service/impl`

- `AuthServiceImpl.java`
- `RoleServiceImpl.java`
- `UserServiceImpl.java`

---

## 5) Implementation Strategy If You Are Building Alone

Use this routine per phase:

1. Create package + skeleton classes/interfaces.
2. Add method signatures first.
3. Wire dependencies via constructor injection.
4. Implement logic and exception paths.
5. Compile after each phase (`mvn -q -DskipTests compile`).
6. Add endpoint tests after controller phase.

This prevents large broken refactors and gives you fast feedback.

---
