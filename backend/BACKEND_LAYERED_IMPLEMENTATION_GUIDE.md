# Build Full-Stack Authentication Backend: Implementation Handbook

Version: backend handbook for `Full-Stack-Authentication-System`

This guide is rewritten in a chapter format similar to your PDF example:
- project overview
- technical implementation notes
- architecture design
- setup steps
- feature-by-feature implementation
- testing and validation steps

It is intentionally practical and build-oriented.

---

## Table of Contents

1. Project Overview
2. Technical Implementation Notes
3. Design the Architecture
4. REST API Design
5. Set Up the Project
6. Implement Foundation Layer (Entity + Repository)
7. Implement Data Transfer Layer (DTO + Mapper)
8. Implement Security Core
9. Implement Authentication Flow
10. Implement Token Rotation and Session Continuity
11. Implement Password Recovery Flow
12. Implement User Portal Flow
13. Implement Admin Flow
14. Implement OAuth Flow
15. Implement Abuse Protection and Rate Limiting
16. Implement Exception Handling
17. Full File-by-File Implementation Map
18. Testing Setup and Test Strategy
19. Deployment Readiness Checklist
20. Troubleshooting Guide

---

## 1. Project Overview

### 1.1 Goal

Build a production-style authentication backend with:
- email/password authentication
- OTP verification
- JWT access token + rotating refresh token
- OAuth login (Google, GitHub, Apple, LinkedIn)
- role-based authorization (`ROLE_USER`, `ROLE_ADMIN`)
- password reset
- abuse protection and lockout controls

### 1.2 User Types

- Guest
- User
- Admin

### 1.3 Main User Journeys

Guest:
- register
- verify OTP
- login
- reset password
- OAuth login

User:
- view dashboard
- view profile
- change password

Admin:
- view admin dashboard
- list users with pagination/filtering/sorting

---

## 2. Technical Implementation Notes

### 2.1 Backend Stack

- Java 21
- Spring Boot 3.5.x
- Spring Security
- Spring Data JPA
- PostgreSQL
- Redis (rate limit support)
- JWT (JJWT)
- MapStruct
- Lombok
- OAuth2 Client

### 2.2 Key Build Dependencies (`backend/pom.xml`)

Core runtime:
- spring-boot-starter-web
- spring-boot-starter-security
- spring-boot-starter-data-jpa
- spring-boot-starter-oauth2-client
- spring-boot-starter-validation
- spring-boot-starter-mail
- spring-boot-starter-data-redis
- postgresql
- jjwt-api / jjwt-impl / jjwt-jackson

Code quality/productivity:
- lombok
- mapstruct

Testing:
- spring-boot-starter-test
- spring-security-test

### 2.3 Important Security Note

`application.properties` currently contains real-looking secrets in plaintext.
Before deployment, move all secrets to environment variables or secret manager.

---

## 3. Design the Architecture

### 3.1 Layered Architecture

```text
Controller  -> handles HTTP, validation boundary
Service     -> business rules and orchestration
Repository  -> database data access
Entity      -> persistence model
DTO         -> API request/response contract
Security    -> JWT, OAuth handlers, auth filters
Config      -> app wiring and beans
```

### 3.2 Request Lifecycle

```text
HTTP Request
  -> Security Filter Chain
  -> Controller
  -> Service
  -> Repository
  -> DB
  -> Service
  -> Controller
HTTP Response
```

### 3.3 Package Layout

```text
com.auth
├── config
├── controller
├── dto
├── entity
├── exception
├── mapper
├── repository
├── security
├── service
│   ├── auth
│   ├── support
│   └── impl
└── AuthApplication.java
```

---

## 4. REST API Design

### 4.1 Guest Flow Endpoints

Auth endpoints:
- `POST /api/auth/register`
- `POST /api/auth/verify-otp`
- `POST /api/auth/login`
- `POST /api/auth/refresh`
- `POST /api/auth/logout`
- `POST /api/auth/reset-password`
- `POST /api/auth/update-password`
- `POST /api/auth/resend-otp`

OAuth endpoints:
- `GET /oauth2/authorization/{provider}`
- `GET /login/oauth2/code/{provider}` (framework callback)

### 4.2 User Flow Endpoints

- `GET /api/user/dashboard`
- `GET /api/user/profile`
- `POST /api/user/change-password`

### 4.3 Admin Flow Endpoints

- `GET /api/admin/dashboard`
- `GET /api/admin/users`

### 4.4 Security Access Rules

From `SecurityConfig`:
- `/api/auth/**` -> permit all
- `/oauth2/**`, `/login/oauth2/**` -> permit all
- `/api/admin/**` -> `ROLE_ADMIN`
- `/api/user/**` -> `ROLE_USER` or `ROLE_ADMIN`

---

## 5. Set Up the Project

### 5.1 Create Infrastructure

1. Create PostgreSQL DB `auth_db`
2. Create DB user for backend
3. Start Redis
4. Configure SMTP credentials for mail flow

### 5.2 Configure `application.properties`

Groups to configure:
- server
- datasource + JPA
- redis
- mail
- JWT secret and expirations
- refresh token cookie settings
- abuse-protection settings
- OAuth provider registrations

Example template:

```properties
# Server
server.port=8080

# Database
spring.datasource.url=jdbc:postgresql://localhost:5432/auth_db
spring.datasource.username=auth_user
spring.datasource.password=${DB_PASSWORD}
spring.jpa.hibernate.ddl-auto=update

# JWT
jwt.secret=${JWT_SECRET_BASE64_OR_LONG_RANDOM}
jwt.expiration=900000
jwt.refresh.expiration=604800000

# Frontend URLs
app.frontend-url=http://localhost:5173
app.backend-url=http://localhost:8080

# Refresh cookie
auth.refresh-token.cookie-name=refreshToken
auth.refresh-token.cookie-path=/api/auth
auth.refresh-token.cookie-secure=false
auth.refresh-token.cookie-same-site=Lax
```

### 5.3 Start the Backend

```bash
cd backend
mvn spring-boot:run
```

### 5.4 Baseline Data

`DataInitializer` creates:
- role records (`ROLE_USER`, `ROLE_ADMIN`)
- default admin (if configured and missing)

---

## 6. Implement Foundation Layer (Entity + Repository)

## 6.1 Design Overview

This layer defines persistent state and query contracts.
No business logic here.

## 6.2 Entity Implementation

Files:
- `entity/Role.java`
- `entity/User.java`

`Role`:
- enum `RoleName { ROLE_USER, ROLE_ADMIN }`

`User` key fields:
- identity: `id`, `name`, `email`, `password`
- verification: `verificationOtp`, `otpExpiry`, `enabled`
- reset: `resetToken`, `resetTokenExpiry`
- refresh session: `refreshToken`, `refreshTokenExpiry`
- abuse tracking: failed attempts + lock timestamps
- OAuth metadata: `authProvider`
- timestamps: `createdAt`, `updatedAt`
- relation: many-to-many with `Role`

Code snippet (`Role.java`):

```java
@Entity
@Table(name = "roles")
@Data
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(length = 20, unique = true, nullable = false)
    private RoleName name;

    public enum RoleName {
        ROLE_USER,
        ROLE_ADMIN
    }
}
```

Code snippet (`User.java` core structure):

```java
@Entity
@Table(name = "users")
@Data
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private boolean enabled = false;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles = new HashSet<>();

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
}
```

## 6.3 Repository Implementation

Files:
- `repository/UserRepository.java`
- `repository/RoleRepository.java`

Important query methods:

`UserRepository`:
- `findByEmail`
- `existsByEmail`
- `findByResetToken`
- `findByRefreshToken`
- `countByEnabledTrue`

`RoleRepository`:
- `findByName`

Code snippet (`UserRepository.java`):

```java
@Repository
public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
    Optional<User> findByResetToken(String resetToken);
    Optional<User> findByRefreshToken(String refreshToken);
    long countByEnabledTrue();
}
```

Code snippet (`RoleRepository.java`):

```java
@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(Role.RoleName name);
}
```

## 6.4 Validation Step

1. Run app startup
2. Confirm table generation succeeds
3. Confirm no JPA mapping exception

---

## 7. Implement Data Transfer Layer (DTO + Mapper)

## 7.1 Design Overview

DTOs protect API contracts and prevent leaking entity internals.

## 7.2 Request DTOs

Files:
- `RegisterRequest`
- `LoginRequest`
- `OtpVerifyRequest`
- `ResetPasswordRequest`
- `UpdatePasswordRequest`
- `ChangePasswordRequest`
- `TokenRefreshRequest`

Use Bean Validation for input safety.

Code snippet (`RegisterRequest.java`):

```java
@Data
public class RegisterRequest {
    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 8, max = 128, message = "Password must be 8-128 characters")
    private String password;
}
```

Code snippet (`LoginRequest.java`):

```java
@Data
public class LoginRequest {
    @NotBlank
    @Email
    private String email;

    @NotBlank
    private String password;
}
```

## 7.3 Response DTOs

Files:
- `MessageResponse`
- `AuthResponse`
- `AuthTokens`
- `UserDto`
- `UserDashboardDto`
- `AdminDashboardDto`

## 7.4 Mapper

File:
- `mapper/UserMapper.java`

Responsibilities:
- DTO -> Entity conversion for register flow
- Entity -> DTO conversion for profile/list views

Code snippet (`UserMapper.java`):

```java
@Mapper(componentModel = "spring")
public interface UserMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "enabled", ignore = true)
    @Mapping(target = "roles", ignore = true)
    @Mapping(target = "authProvider", ignore = true)
    User toEntity(RegisterRequest request);

    UserDto toDto(User user);
}
```

## 7.5 Validation Step

- Verify controller endpoints accept/return DTO only
- Ensure `User` entity is never serialized directly in API

---

## 8. Implement Security Core

## 8.1 Design Overview

Security core handles:
- authentication context
- JWT generation/validation
- filter chain
- OAuth login hooks
- refresh-token cookie behavior

## 8.2 Password Encoder and Security Beans

Files:
- `config/PasswordConfig.java`
- `config/SecurityConfig.java`

Required beans:
- `PasswordEncoder`
- `AuthenticationManager`
- `DaoAuthenticationProvider`
- `SecurityFilterChain`

Code snippet (`PasswordConfig.java`):

```java
@Configuration
public class PasswordConfig {
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
```

Code snippet (`SecurityConfig.java` filter chain):

```java
@Bean
public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http
            .cors(Customizer.withDefaults())
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session ->
                    session.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED))
            .authorizeHttpRequests(auth -> auth
                    .requestMatchers("/api/auth/**").permitAll()
                    .requestMatchers("/oauth2/**", "/login/oauth2/**").permitAll()
                    .requestMatchers("/api/admin/**").hasAuthority("ROLE_ADMIN")
                    .requestMatchers("/api/user/**").hasAnyAuthority("ROLE_USER", "ROLE_ADMIN")
                    .anyRequest().authenticated())
            .oauth2Login(oauth -> oauth
                    .authorizationEndpoint(endpoint ->
                            endpoint.authorizationRequestResolver(linkedInAuthorizationRequestResolver))
                    .successHandler(successHandler)
                    .failureHandler(failureHandler))
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

    return http.build();
}
```

## 8.3 JWT Utility and Filter

Files:
- `security/JwtUtil.java`
- `security/JwtAuthFilter.java`

`JwtUtil`:
- generate token
- parse email
- validate token

`JwtAuthFilter`:
- read `Authorization` header
- validate JWT
- load user details
- set security context

Code snippet (`JwtUtil.java`):

```java
public String generateTokenFromEmail(String email) {
    Date now = new Date();
    Date expiryDate = new Date(now.getTime() + jwtExpirationMs);

    return Jwts.builder()
            .subject(email)
            .issuedAt(now)
            .expiration(expiryDate)
            .signWith(getSigningKey())
            .compact();
}

public boolean validateToken(String token) {
    try {
        Jwts.parser().verifyWith(getSigningKey()).build().parseSignedClaims(token);
        return true;
    } catch (JwtException | IllegalArgumentException ex) {
        return false;
    }
}
```

Code snippet (`JwtAuthFilter.java`):

```java
@Override
protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
        throws ServletException, IOException {
    String token = extractJwtFromRequest(request);
    if (token != null && jwtUtil.validateToken(token)) {
        String email = jwtUtil.getEmailFromToken(token);
        UserDetails userDetails = customUserDetailsService.loadUserByUsername(email);
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
    filterChain.doFilter(request, response);
}
```

## 8.4 UserDetails Service

File:
- `security/CustomUserDetailsService.java`

Loads user and authorities from DB for Spring Security.

## 8.5 Refresh Cookie Helper

File:
- `security/RefreshTokenCookieService.java`

Handles:
- creating refresh cookie
- clearing cookie
- exposing configured cookie name

## 8.6 OAuth Security Handlers

Files:
- `OAuth2AuthenticationSuccessHandler`
- `OAuth2AuthenticationFailureHandler`
- `LinkedInAuthorizationRequestResolver`

Responsibilities:
- success: provision user, issue tokens, set cookie, redirect frontend callback
- failure: redirect frontend login with `oauthError`
- LinkedIn resolver: LinkedIn-compatible authorization request handling

## 8.7 Validation Step

- Protected routes reject unauthenticated users
- JWT authenticated requests reach user/admin endpoints
- OAuth callback reaches frontend callback URL

---

## 9. Implement Authentication Flow

This chapter covers register, OTP verify, and login.

## 9.1 Service Interface Design

File:
- `service/AuthService.java`

Main methods:
- `register`
- `verifyOtp`
- `login`
- `resetPassword`
- `updatePassword`
- `resendOtp`
- `changePassword`

Code snippet (`AuthService.java`):

```java
public interface AuthService {
    MessageResponse register(RegisterRequest request);
    MessageResponse verifyOtp(OtpVerifyRequest request);
    AuthTokens login(LoginRequest request);
    MessageResponse resetPassword(ResetPasswordRequest request);
    MessageResponse updatePassword(UpdatePasswordRequest request);
    MessageResponse resendOtp(String email);
    MessageResponse changePassword(String email, ChangePasswordRequest request);
}
```

## 9.2 Register Implementation

### Files Involved

- `AuthServiceImpl.register`
- `AuthController.register`
- `UserMapper`
- `RoleService`
- `OtpService`
- `TokenHashService`
- `EmailService`

### Logic

1. check existing email
2. validate password policy
3. map DTO to user entity
4. hash password
5. generate OTP and hash it
6. assign `ROLE_USER`
7. save user
8. send OTP mail

Code snippet (`AuthServiceImpl.register`):

```java
@Override
@Transactional
public MessageResponse register(RegisterRequest request) {
    if (userService.existsByEmail(request.getEmail())) {
        throw new UserAlreadyExistsException("Email already registered!");
    }

    passwordPolicyService.validate(request.getPassword(), request.getEmail());

    User user = userMapper.toEntity(request);
    user.setPassword(passwordEncoder.encode(request.getPassword()));

    String otp = assignVerificationOtp(user);
    Role userRole = roleService.findOrCreateRole(Role.RoleName.ROLE_USER);
    user.setRoles(Set.of(userRole));

    userService.save(user);
    emailService.sendOtpEmail(user.getEmail(), otp);

    return new MessageResponse("Registration successful! Please check your email for OTP verification.", true);
}
```

### API Testing

Request:

```json
{
  "name": "Alice",
  "email": "alice@example.com",
  "password": "Password1"
}
```

Expected:
- HTTP 200
- success message
- user persisted with `enabled=false`

## 9.3 OTP Verification Implementation

### Files Involved

- `AuthServiceImpl.verifyOtp`
- `AuthController.verifyOtp`
- `AuthAbuseProtectionService`

### Logic

1. guard against brute-force OTP attempts
2. find user by email
3. reject if already enabled
4. compare raw OTP with hashed OTP
5. validate expiry
6. enable user, clear OTP fields
7. clear failure counters
8. persist

Code snippet (`AuthServiceImpl.verifyOtp`):

```java
@Override
@Transactional
public MessageResponse verifyOtp(OtpVerifyRequest request) {
    authAbuseProtectionService.guardOtpVerification(request.getEmail());
    User user = requireUserByEmail(request.getEmail());

    if (user.isEnabled()) {
        throw new UserAlreadyExistsException("Email already verified!");
    }
    if (!tokenHashService.matches(request.getOtp(), user.getVerificationOtp())) {
        authAbuseProtectionService.recordFailedOtp(user);
        throw new TokenValidationException("Invalid OTP!");
    }
    if (user.getOtpExpiry() == null || user.getOtpExpiry().isBefore(LocalDateTime.now())) {
        throw new TokenValidationException("OTP has expired! Please request a new one.");
    }

    user.setEnabled(true);
    user.setVerificationOtp(null);
    user.setOtpExpiry(null);
    authAbuseProtectionService.clearOtpFailures(user);
    userService.save(user);

    return new MessageResponse("Email verified successfully! You can now login.", true);
}
```

### API Testing

Request:

```json
{
  "email": "alice@example.com",
  "otp": "123456"
}
```

Expected:
- HTTP 200
- account enabled

## 9.4 Login Implementation

### Files Involved

- `AuthServiceImpl.login`
- `AuthController.login`
- `AuthTokenService.issueTokens`
- `RefreshTokenCookieService`

### Logic

1. guard login abuse
2. find user
3. reject if not enabled
4. authenticate credentials via `AuthenticationManager`
5. clear login failure state
6. issue access + refresh
7. set refresh cookie
8. return auth response

Code snippet (`AuthServiceImpl.login`):

```java
@Override
public AuthTokens login(LoginRequest request) {
    authAbuseProtectionService.guardLoginAttempt(request.getEmail());

    User user = userService.findByEmail(request.getEmail())
            .orElseThrow(() -> new BadCredentialsException("Invalid email or password!"));

    if (!user.isEnabled()) {
        throw new TokenValidationException("Please verify your email first!");
    }

    authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));

    authAbuseProtectionService.clearLoginFailures(user);
    return authTokenService.issueTokens(user);
}
```

Code snippet (`AuthController.login`):

```java
@PostMapping("/login")
public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request, HttpServletResponse response) {
    AuthTokens authTokens = authService.login(request);
    response.addHeader(HttpHeaders.SET_COOKIE,
            refreshTokenCookieService.buildRefreshTokenCookie(authTokens.refreshToken()));
    return ResponseEntity.ok(authTokens.response());
}
```

### API Testing

Request:

```json
{
  "email": "alice@example.com",
  "password": "Password1"
}
```

Expected:
- HTTP 200
- access token in response
- refresh token in Set-Cookie

---

## 10. Implement Token Rotation and Session Continuity

## 10.1 Service Layer

File:
- `service/auth/AuthTokenService.java`

Key methods:
- `issueTokens(User)`
- `refreshTokens(String)`
- `revokeRefreshToken(String)`

Code snippet (`AuthTokenService.java`):

```java
@Transactional
public AuthTokens issueTokens(User user) {
    String accessToken = jwtUtil.generateTokenFromEmail(user.getEmail());
    String refreshToken = generateRefreshToken();

    user.setRefreshToken(tokenHashService.hash(refreshToken));
    user.setRefreshTokenExpiry(LocalDateTime.now().plusSeconds(refreshTokenExpirationMs / 1000));
    userService.save(user);

    return new AuthTokens(buildAuthResponse(user, accessToken), refreshToken);
}

@Transactional
public AuthTokens refreshTokens(String refreshToken) {
    if (refreshToken == null || refreshToken.isBlank()) {
        throw new TokenValidationException("Refresh token is required.");
    }
    String hash = tokenHashService.hash(refreshToken);
    User user = userService.findByRefreshToken(hash)
            .orElseThrow(() -> new TokenValidationException("Invalid refresh token."));
    if (user.getRefreshTokenExpiry() == null || user.getRefreshTokenExpiry().isBefore(LocalDateTime.now())) {
        user.setRefreshToken(null);
        user.setRefreshTokenExpiry(null);
        userService.save(user);
        throw new TokenValidationException("Refresh token has expired. Please login again.");
    }
    return issueTokens(user);
}
```

## 10.2 Refresh Endpoint

File:
- `controller/AuthController.refreshToken`

Refresh token source order:
1. request body (`TokenRefreshRequest`)
2. refresh cookie

On success:
- rotate refresh token
- set new cookie
- return new access token response payload

Code snippet (`AuthController.refreshToken`):

```java
@PostMapping("/refresh")
public ResponseEntity<AuthResponse> refreshToken(
        HttpServletRequest request,
        HttpServletResponse response,
        @RequestBody(required = false) TokenRefreshRequest body) {
    String refreshToken = resolveRefreshToken(request, body);
    AuthTokens authTokens = authTokenService.refreshTokens(refreshToken);
    response.addHeader(HttpHeaders.SET_COOKIE,
            refreshTokenCookieService.buildRefreshTokenCookie(authTokens.refreshToken()));
    return ResponseEntity.ok(authTokens.response());
}
```

## 10.3 Logout Endpoint

File:
- `controller/AuthController.logout`

Logic:
- resolve refresh token (body or cookie)
- revoke stored refresh token hash
- clear cookie in response

Code snippet (`AuthController.logout`):

```java
@PostMapping("/logout")
public ResponseEntity<MessageResponse> logout(
        HttpServletRequest request,
        HttpServletResponse response,
        @RequestBody(required = false) TokenRefreshRequest body) {
    String refreshToken = resolveRefreshToken(request, body);
    authTokenService.revokeRefreshToken(refreshToken);
    response.addHeader(HttpHeaders.SET_COOKIE, refreshTokenCookieService.clearRefreshTokenCookie());
    return ResponseEntity.ok(new MessageResponse("Logged out successfully.", true));
}
```

## 10.4 API Testing

### Refresh

- login first
- call `POST /api/auth/refresh` with cookie
- expect new access token and renewed cookie

### Logout

- call `POST /api/auth/logout`
- expect success message
- cookie cleared

---

## 11. Implement Password Recovery Flow

## 11.1 Reset Password Request

Files:
- `AuthServiceImpl.resetPassword`
- `AuthController.resetPassword`
- `EmailService.sendPasswordResetEmail`

Logic:
- apply abuse guard
- if email missing, return generic success (no user enumeration)
- if exists, generate reset token, hash + expiry, save
- email raw token link

Code snippet (`AuthServiceImpl.resetPassword`):

```java
@Override
@Transactional
public MessageResponse resetPassword(ResetPasswordRequest request) {
    authAbuseProtectionService.guardResetPassword(request.getEmail());
    Optional<User> userOpt = userService.findByEmail(request.getEmail());
    if (userOpt.isEmpty()) {
        return new MessageResponse("If an account exists with this email, a reset link will be sent.", true);
    }

    User user = userOpt.get();
    String rawResetToken = otpService.generateResetToken();
    user.setResetToken(tokenHashService.hash(rawResetToken));
    user.setResetTokenExpiry(LocalDateTime.now().plusMinutes(resetTokenExpirationMinutes));
    userService.save(user);
    emailService.sendPasswordResetEmail(user.getEmail(), rawResetToken);

    return new MessageResponse("If an account exists with this email, a reset link will be sent.", true);
}
```

## 11.2 Update Password

Files:
- `AuthServiceImpl.updatePassword`
- `AuthController.updatePassword`

Logic:
- hash incoming reset token
- find user by hashed token
- check expiry
- validate password policy
- hash new password
- clear reset token fields

Code snippet (`AuthServiceImpl.updatePassword`):

```java
@Override
@Transactional
public MessageResponse updatePassword(UpdatePasswordRequest request) {
    String hash = tokenHashService.hash(request.getToken());
    User user = userService.findByResetToken(hash)
            .orElseThrow(() -> new TokenValidationException("Invalid or expired reset token!"));
    if (user.getResetTokenExpiry() == null || user.getResetTokenExpiry().isBefore(LocalDateTime.now())) {
        throw new TokenValidationException("Reset token has expired! Please request a new one.");
    }

    passwordPolicyService.validate(request.getNewPassword(), user.getEmail());
    user.setPassword(passwordEncoder.encode(request.getNewPassword()));
    user.setResetToken(null);
    user.setResetTokenExpiry(null);
    userService.save(user);

    return new MessageResponse("Password updated successfully! You can now login.", true);
}
```

## 11.3 API Testing

Request reset:

```json
{
  "email": "alice@example.com"
}
```

Update password:

```json
{
  "token": "reset-token-from-email",
  "newPassword": "NewPassword1"
}
```

Expected:
- generic success for reset request
- successful password update for valid token

---

## 12. Implement User Portal Flow

## 12.1 Controller Design

File:
- `controller/UserController.java`

Endpoints:
- `GET /api/user/dashboard`
- `GET /api/user/profile`
- `POST /api/user/change-password`

Access:
- `ROLE_USER` or `ROLE_ADMIN`

Code snippet (`UserController.java`):

```java
@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {
    private final AuthService authService;
    private final UserPortalService userPortalService;

    @GetMapping("/dashboard")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<UserDashboardDto> getDashboard(Authentication authentication) {
        return ResponseEntity.ok(userPortalService.getDashboard(authentication.getName()));
    }

    @PostMapping("/change-password")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<MessageResponse> changePassword(
            Authentication authentication, @Valid @RequestBody ChangePasswordRequest request) {
        return ResponseEntity.ok(authService.changePassword(authentication.getName(), request));
    }
}
```

## 12.2 Service Layer

Files:
- `service/UserPortalService.java`
- `service/impl/UserPortalServiceImpl.java`

Responsibilities:
- load dashboard data by authenticated email
- load profile DTO

## 12.3 Change Password

Uses:
- `AuthServiceImpl.changePassword`

Logic:
- validate current password
- validate new password policy
- hash and persist

## 12.4 API Testing

- authenticate user
- call dashboard/profile endpoints
- verify role access and response shape

---

## 13. Implement Admin Flow

## 13.1 Controller Design

File:
- `controller/AdminController.java`

Endpoints:
- `GET /api/admin/dashboard`
- `GET /api/admin/users`

Class-level security:
- `@PreAuthorize("hasRole('ADMIN')")`

Code snippet (`AdminController.java`):

```java
@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminController {
    private final AdminService adminService;

    @GetMapping("/dashboard")
    public ResponseEntity<AdminDashboardDto> getDashboard(Authentication authentication) {
        return ResponseEntity.ok(adminService.getDashboard(authentication.getName()));
    }

    @GetMapping("/users")
    public ResponseEntity<Page<UserDto>> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String search) {
        return ResponseEntity.ok(adminService.getUsers(page, size, search, null, null, "createdAt", "desc"));
    }
}
```

## 13.2 Service Layer

Files:
- `service/AdminService.java`
- `service/impl/AdminServiceImpl.java`

Responsibilities:
- admin metrics
- paginated user listing
- optional search/filter/sort support

## 13.3 Repository Support

`UserRepository` supports admin metrics and list filtering via JPA specs/paging.

## 13.4 API Testing

- login as admin
- call `/api/admin/dashboard`
- call `/api/admin/users?page=0&size=20&sortBy=createdAt&sortDir=desc`
- verify user account cannot access admin endpoints

---

## 14. Implement OAuth Flow

## 14.1 Design Overview

OAuth in this project uses Spring Security OAuth2 client.

Providers configured:
- Google
- GitHub
- Apple
- LinkedIn

## 14.2 Provisioning Service

File:
- `service/auth/OAuth2UserProvisioningService.java`

Responsibilities:
- extract provider identity data
- resolve email and display name
- find user by email
- create new user if absent
- patch existing user if needed

Provider-specific fallback:
- GitHub fallback email: `<login>@users.noreply.github.com`

Code snippet (`OAuth2UserProvisioningService.java`):

```java
@Service
@RequiredArgsConstructor
public class OAuth2UserProvisioningService {
    private final UserService userService;
    private final RoleService roleService;
    private final PasswordEncoder passwordEncoder;

    public User loadOrCreateUser(OAuth2AuthenticationToken token, OAuth2User oauth2User) {
        String provider = token.getAuthorizedClientRegistrationId();
        Map<String, Object> attrs = oauth2User.getAttributes();
        String email = extractEmail(provider, attrs);
        String name = extractDisplayName(attrs, email);

        return userService.findByEmail(email).orElseGet(() -> {
            User user = new User();
            user.setName(name);
            user.setEmail(email);
            user.setPassword(passwordEncoder.encode(UUID.randomUUID().toString()));
            user.setEnabled(true);
            user.setAuthProvider(provider);
            user.setRoles(Set.of(roleService.findOrCreateRole(Role.RoleName.ROLE_USER)));
            return userService.save(user);
        });
    }
}
```

## 14.3 Success Handler

File:
- `security/OAuth2AuthenticationSuccessHandler.java`

Flow:
1. read OAuth principal
2. load/create local user
3. issue access/refresh tokens
4. set refresh cookie
5. redirect to frontend callback (`/oauth2/callback`)

Code snippet (`OAuth2AuthenticationSuccessHandler.java`):

```java
@Override
public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication auth)
        throws IOException, ServletException {
    OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) auth;
    OAuth2User oauth2User = oauthToken.getPrincipal();

    User user = oAuth2UserProvisioningService.loadOrCreateUser(oauthToken, oauth2User);
    AuthTokens tokens = authTokenService.issueTokens(user);

    response.addHeader(HttpHeaders.SET_COOKIE,
            refreshTokenCookieService.buildRefreshTokenCookie(tokens.refreshToken()));
    getRedirectStrategy().sendRedirect(request, response, frontendUrl + "/oauth2/callback");
}
```

## 14.4 Failure Handler

File:
- `security/OAuth2AuthenticationFailureHandler.java`

Flow:
- resolve error message
- redirect frontend login with `oauthError` query param

Code snippet (`OAuth2AuthenticationFailureHandler.java`):

```java
@Override
public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
        AuthenticationException exception) throws IOException, ServletException {
    String message = exception.getMessage() == null ? "OAuth login failed." : exception.getMessage();
    String targetUrl = UriComponentsBuilder
            .fromUriString(frontendUrl + "/login")
            .queryParam("oauthError", message)
            .build()
            .encode()
            .toUriString();
    getRedirectStrategy().sendRedirect(request, response, targetUrl);
}
```

## 14.5 LinkedIn Authorization Resolver

File:
- `security/LinkedInAuthorizationRequestResolver.java`

Purpose:
- LinkedIn-specific authorization request adjustments

Code snippet (`LinkedInAuthorizationRequestResolver.java`):

```java
@Override
public OAuth2AuthorizationRequest resolve(HttpServletRequest request, String clientRegistrationId) {
    OAuth2AuthorizationRequest authRequest = delegate.resolve(request, clientRegistrationId);
    if (!"linkedin".equals(clientRegistrationId) || authRequest == null) {
        return authRequest;
    }
    return OAuth2AuthorizationRequest.from(authRequest)
            .additionalParameters(params -> params.remove(OidcParameterNames.NONCE))
            .attributes(attrs -> attrs.remove(OidcParameterNames.NONCE))
            .build();
}
```

## 14.6 Manual Testing

1. open frontend login
2. click provider button
3. complete provider auth
4. verify backend sets refresh cookie
5. verify frontend callback calls `/api/auth/refresh`
6. verify user lands on dashboard/admin page by role

---

## 15. Implement Abuse Protection and Rate Limiting

## 15.1 Components

Files:
- `service/auth/AuthAbuseProtectionService.java`
- `service/support/RateLimitService.java`
- lockout-related fields in `User`

## 15.2 Controls Applied

- login rate limits
- OTP verification limits
- resend OTP cooldown and limits
- reset password limits
- failed login lockout window
- failed OTP lockout window

## 15.3 Configuration

Properties in `application.properties` under:
- `auth.rate-limit.*`
- `auth.bruteforce.*`

Code snippet (rate-limit properties):

```properties
auth.protection.enabled=true

auth.rate-limit.login.ip.limit=5
auth.rate-limit.login.ip.window-seconds=60
auth.rate-limit.login.email.limit=10
auth.rate-limit.login.email.window-seconds=900

auth.bruteforce.login.max-attempts=10
auth.bruteforce.login.lock-minutes=15
auth.bruteforce.otp.max-attempts=5
auth.bruteforce.otp.lock-minutes=10
```

## 15.4 Testing Scenarios

- repeated failed login attempts trigger lockout
- repeated invalid OTP attempts trigger lockout
- resend OTP cooldown enforces delay

---

## 16. Implement Exception Handling

## 16.1 Exception Classes

Files:
- `AccountLockedException`
- `RateLimitExceededException`
- `ResourceNotFoundException`
- `TokenValidationException`
- `UserAlreadyExistsException`

## 16.2 Global Handler

File:
- `exception/GlobalExceptionHandler.java`

Responsibilities:
- map domain exceptions to stable API responses
- avoid leaking stack traces
- maintain consistent response structure

Code snippet (`GlobalExceptionHandler.java`):

```java
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(TokenValidationException.class)
    public ResponseEntity<MessageResponse> handleTokenValidation(TokenValidationException ex) {
        return ResponseEntity.badRequest().body(new MessageResponse(ex.getMessage(), false));
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<MessageResponse> handleUserExists(UserAlreadyExistsException ex) {
        return ResponseEntity.badRequest().body(new MessageResponse(ex.getMessage(), false));
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<MessageResponse> handleNotFound(ResourceNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new MessageResponse(ex.getMessage(), false));
    }
}
```

## 16.3 Testing

- invalid token -> token validation response
- unknown user -> resource not found response
- duplicate register -> user already exists response

---

## 17. Full File-by-File Implementation Map

This section gives a complete implementation index.

### 17.1 Application Entry

- `AuthApplication.java`: Spring Boot bootstrap

### 17.2 Config Package

- `CorsConfig.java`: CORS policy
- `DataInitializer.java`: seed roles/admin at startup
- `PasswordConfig.java`: password encoder bean
- `SecurityConfig.java`: security filter chain, auth provider, manager

### 17.3 Controller Package

- `AuthController.java`: auth API endpoints
- `UserController.java`: user profile/dashboard/password APIs
- `AdminController.java`: admin dashboard/users APIs

### 17.4 DTO Package

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

### 17.5 Entity Package

- `Role.java`
- `User.java`

### 17.6 Exception Package

- `AccountLockedException.java`
- `GlobalExceptionHandler.java`
- `RateLimitExceededException.java`
- `ResourceNotFoundException.java`
- `TokenValidationException.java`
- `UserAlreadyExistsException.java`

### 17.7 Mapper Package

- `UserMapper.java`

### 17.8 Repository Package

- `RoleRepository.java`
- `UserRepository.java`

### 17.9 Security Package

- `CustomUserDetailsService.java`
- `JwtAuthFilter.java`
- `JwtUtil.java`
- `LinkedInAuthorizationRequestResolver.java`
- `OAuth2AuthenticationFailureHandler.java`
- `OAuth2AuthenticationSuccessHandler.java`
- `RefreshTokenCookieService.java`

### 17.10 Service Interfaces

- `AdminService.java`
- `AuthService.java`
- `RoleService.java`
- `UserPortalService.java`
- `UserService.java`

### 17.11 Service Auth Package

- `AuthAbuseProtectionService.java`
- `AuthTokenService.java`
- `OAuth2UserProvisioningService.java`

### 17.12 Service Implementation Package

- `AdminServiceImpl.java`
- `AuthServiceImpl.java`
- `RoleServiceImpl.java`
- `UserPortalServiceImpl.java`
- `UserServiceImpl.java`

### 17.13 Service Support Package

- `EmailService.java`
- `OtpService.java`
- `PasswordPolicyService.java`
- `RateLimitService.java`
- `TokenHashService.java`

---

## 18. Testing Setup and Test Strategy

## 18.1 Existing Unit Tests

Current test classes:
- `controller/AuthControllerTest`
- `security/CustomUserDetailsServiceTest`
- `service/auth/AuthTokenServiceTest`
- `service/auth/OAuth2UserProvisioningServiceTest`
- `service/impl/AdminServiceImplTest`
- `service/impl/AuthServiceImplTest`
- `service/impl/UserServiceImplTest`

## 18.2 Run All Tests

```bash
cd backend
mvn test
```

## 18.3 Suggested Test Order

1. token service tests
2. auth service tests
3. admin/user service tests
4. controller tests

## 18.4 Integration Testing Plan

For each endpoint group:
- success case
- auth failure case
- validation failure case
- permission failure case

---

## 19. Deployment Readiness Checklist

Security:
- move secrets to env vars
- disable `ddl-auto=create-drop`
- enable secure cookies in production
- enforce HTTPS
- audit CORS origins

Database:
- use managed PostgreSQL
- add migrations (Flyway/Liquibase)
- backup strategy

Observability:
- structured logging
- error monitoring
- health endpoint checks

OAuth:
- verify exact redirect URIs in all provider consoles
- verify backend/frontend domain config

---

## 20. Troubleshooting Guide

## 20.1 OAuth Login Fails

Check:
- provider `client-id` and `client-secret`
- redirect URI exact match
- frontend callback URL
- backend `app.frontend-url` config

## 20.2 Refresh Token Not Rotating

Check:
- cookie is sent (`withCredentials=true` in frontend)
- cookie path matches `/api/auth`
- refresh token hash exists in user record

## 20.3 JWT Validation Fails

Check:
- `jwt.secret` length and consistency
- token expiry window
- auth header format `Bearer <token>`

## 20.4 Reset Password Token Invalid

Check:
- raw token is hashed before lookup
- reset token expiry not passed
- token copied correctly from email link

## 20.5 Admin Access Denied

Check:
- user has `ROLE_ADMIN`
- `SecurityConfig` and `@PreAuthorize` align
- JWT contains roles from DB state at token issue time

---

## Appendix A: Recommended Refactor Path

If you want cleaner long-term architecture, refactor in this order:

1. move secrets out of `application.properties`
2. split config into profile files (`application-dev.properties`, `application-prod.properties`)
3. add migration tool (Flyway)
4. add integration tests with Testcontainers
5. add audit logging for auth-sensitive actions

---

## Appendix B: Fast Build Sequence for New Team Members

Day 1:
- read sections 1 to 5
- run backend locally

Day 2:
- implement or review sections 6 to 8

Day 3:
- implement or review sections 9 to 11

Day 4:
- implement or review sections 12 to 14

Day 5:
- implement or review sections 15 to 20
- run full tests and manual endpoint checks

This sequence avoids context overload and matches the layered design.
