# Backend Authentication System: Senior Engineering Build Guide

## Purpose of This Document

This document explains how to build the backend of this project in the same order an experienced backend engineer would normally implement it in a real company codebase. The goal is not only to say which file comes first, but also to explain why that file should come first, what methods belong inside it, and what engineering problem it solves. This backend is not just a simple login API. It combines Spring Boot, Spring Security, JWT access tokens, hashed refresh tokens, OTP-based email verification, password reset, Redis-backed rate limiting, role-based authorization, and OAuth2 social login. That means implementation order matters, because the higher layers depend on the lower layers being stable. If we build the system out of order, we usually end up rewriting controllers, changing service contracts, or weakening security later. A senior engineer avoids that by creating the backend from the inside out: domain first, security primitives second, flows third, and endpoints last.

## What This Backend Is Responsible For

The backend owns account creation, email verification, login, refresh-token rotation, logout, password reset, password change, user-profile APIs, admin APIs, and social login onboarding. It persists users and roles in PostgreSQL, uses Redis for caching and rate limiting, and sends transactional emails using SMTP plus Thymeleaf templates. The access token is short-lived and stateless, which makes API authorization fast and horizontally scalable. The refresh token is long-lived but stored in the database only as a one-way hash, which reduces damage if the database is leaked. OTP codes and reset tokens are also hashed before storage for the same reason. The project follows a layered architecture so that controllers stay thin, services contain business rules, repositories handle persistence, and security components stay focused on authentication concerns.

## Architecture Summary

At a high level, the request enters through a controller, gets validated with DTOs, then moves into a service where business rules are enforced. The service talks to repositories and helper services such as email, OTP, token hashing, password policy, and abuse protection. Security is handled separately through `SecurityConfig`, `JwtAuthFilter`, `CustomUserDetailsService`, and `JwtUtil`, because authentication and authorization are cross-cutting concerns that should not be duplicated in controllers. `AuthTokenService` is responsible for issuing and rotating tokens, while `AuthServiceImpl` is responsible for business workflows such as registration and password reset. `GlobalExceptionHandler` converts failures into a stable API contract so the frontend never has to guess what error shape will come back. This separation is why the codebase stays maintainable as features grow.

## Recommended Implementation Order

### Task 1: Bootstrap the project and infrastructure contract

Create `pom.xml` first because it defines the runtime capabilities of the system: web, security, JPA, validation, mail, OAuth2 client, Redis, cache, JWT, Lombok, and MapStruct. After that, create `src/main/resources/application.properties`, `Dockerfile`, `docker-compose.yaml`, and `AuthApplication.java` so the project can boot with local defaults and infrastructure assumptions from day one. A senior engineer does this first because every later design decision depends on what libraries and runtime services are available. In this project, PostgreSQL is the source of truth, Redis is an accelerator and protection layer, and SMTP is part of the authentication workflow, so these are not optional afterthoughts. We externalize all important values with environment variables because secrets, URLs, cookie policy, OAuth credentials, and rate-limit thresholds vary by environment. We keep `spring.jpa.open-in-view=false` because business logic should finish inside the service layer, not lazily access the database during response rendering.

Files to create in this step:
`backend/pom.xml`, `backend/src/main/resources/application.properties`, `backend/Dockerfile`, `backend/docker-compose.yaml`, `backend/src/main/java/com/auth/AuthApplication.java`

### Task 2: Define shared constants and utility foundations

Before modeling users, create the low-level shared files that other classes will depend on: `ApiPaths`, `CacheNames`, and `DateTimeUtil`. Then create `BaseEntity` so every persistent entity gets `id`, `createdAt`, and `updatedAt` consistently. This is the right order because constants and base abstractions prevent repeated string literals and repeated timestamp logic later. A senior engineer centralizes API paths early because hard-coded routes scattered across controllers become painful to change when versioning evolves. `BaseEntity` exists so auditing behavior is uniform across tables and future entities can inherit it without reimplementation. This project uses explicit utility-based IST timestamps, which makes formatting and persistence behavior predictable for dashboards, emails, and admin reporting.

Files to create in this step:
`backend/src/main/java/com/auth/config/ApiPaths.java`, `backend/src/main/java/com/auth/config/CacheNames.java`, `backend/src/main/java/com/auth/util/DateTimeUtil.java`, `backend/src/main/java/com/auth/entity/BaseEntity.java`

### Task 3: Model the domain before writing business logic

Now create `RoleName`, `Role`, and `User`, because the data model is the backbone of the entire authentication system. Start with `RoleName` as an enum, then `Role`, then `User`, because the user entity references roles and should not be designed in isolation. In this project, the `User` entity contains more than profile data; it also stores verification state, OTP hash and expiry, reset-token hash and expiry, refresh-token hash and expiry, failed-attempt counters, account lock timestamps, and OAuth provider metadata. That is intentional because authentication state is part of the domain, not just a session concern. We store hashed OTP, reset token, and refresh token values rather than plain values because these tokens are credentials; if the database is compromised, plaintext tokens would become immediately usable. We use a many-to-many role association with eager fetching because role checks are needed frequently at authentication time and the role set is small.

Files to create in this step:
`backend/src/main/java/com/auth/entity/RoleName.java`, `backend/src/main/java/com/auth/entity/Role.java`, `backend/src/main/java/com/auth/entity/User.java`

### Task 4: Create repository interfaces as the persistence boundary

Once entities exist, create `UserRepository` and `RoleRepository`. Write the repository methods that the service layer will truly need, not generic methods “just in case”. For this codebase, that means case-insensitive lookup by email, existence check by email, lookup by reset-token hash, lookup by refresh-token hash, lookup by OAuth provider identity, and enabled-user counts for admin metrics. This step comes before services because services should be written against real persistence capabilities, not imagined ones. Case-insensitive email queries are especially important because email identity is logically case-insensitive for account lookup, and failing to normalize here creates duplicate-user and login bugs. `JpaSpecificationExecutor` is added to `UserRepository` because the admin listing requires filtered and searchable queries without custom SQL explosion.

Files to create in this step:
`backend/src/main/java/com/auth/repository/UserRepository.java`, `backend/src/main/java/com/auth/repository/RoleRepository.java`

### Task 5: Define DTOs and a stable API contract early

Before controllers and complex services, define request DTOs, response DTOs, and a wrapper response format. Create request objects such as `RegisterRequest`, `LoginRequest`, `OtpVerifyRequest`, `ResetPasswordRequest`, `UpdatePasswordRequest`, `ChangePasswordRequest`, and `TokenRefreshRequest`, then define response objects such as `ApiResponse`, `MessageResponse`, `AuthResponse`, `AuthTokens`, `UserDto`, `UserDashboardDto`, and `AdminDashboardDto`. This order matters because services and controllers should depend on explicit contracts rather than exposing entities directly. A senior engineer never returns the `User` entity from the API, because entities contain internal fields like password hash, token hashes, lock-state metadata, and other persistence details that should never leak. Validation annotations belong on request DTOs so bad requests are rejected at the API boundary before business logic starts. `ApiResponse` is useful because it gives the frontend a consistent `success`, `message`, and `data` shape across every endpoint.

Files to create in this step:
`backend/src/main/java/com/auth/dto/request/*.java`, `backend/src/main/java/com/auth/dto/response/*.java`

### Task 6: Build the core service abstractions for users and roles

Create `UserService` and `RoleService` interfaces first, then implement them in `UserServiceImpl` and `RoleServiceImpl`. Add `UserMapper` in the same phase because mapping between entities and DTOs is part of the application boundary and should not be scattered in controllers. The key methods to write first are `findByEmail`, `existsByEmail`, `save`, `findByResetToken`, `findByRefreshToken`, and `findByAuthProviderAndAuthProviderUserId` in the user service, plus `findOrCreateRole` in the role service. This layer should stay small and predictable; it is the reusable core that higher authentication workflows call repeatedly. Email normalization is done here again before persistence so the system is defensive at multiple boundaries, not only at the controller. `findOrCreateRole` is important because startup seeding, registration, and OAuth provisioning all depend on stable role creation without duplicating repository logic.

Files to create in this step:
`backend/src/main/java/com/auth/service/UserService.java`, `backend/src/main/java/com/auth/service/RoleService.java`, `backend/src/main/java/com/auth/service/impl/UserServiceImpl.java`, `backend/src/main/java/com/auth/service/impl/RoleServiceImpl.java`, `backend/src/main/java/com/auth/mapper/UserMapper.java`

### Task 7: Add password encoding and Spring Security user loading

Now create `PasswordConfig` and `CustomUserDetailsService`. The reason to do this before JWT and controllers is that username/password authentication in Spring Security depends on `UserDetailsService` plus a `PasswordEncoder`, and these are foundational primitives. In `CustomUserDetailsService`, implement `loadUserByUsername` first, then helper methods like `normalizeEmail`, `getRequiredUser`, `buildAuthorities`, and `buildSecurityUser`. This service translates your domain `User` into Spring Security’s `UserDetails`, which allows the framework to compare the raw password with the stored BCrypt hash safely. We use BCrypt instead of homemade hashing because password storage must be adaptive and slow by design; SHA-256 is acceptable for opaque tokens, but not for human passwords. This separation is also why the JWT filter later can stay stateless while the login flow still reuses Spring Security’s mature authentication engine.

Files to create in this step:
`backend/src/main/java/com/auth/config/PasswordConfig.java`, `backend/src/main/java/com/auth/security/CustomUserDetailsService.java`

### Task 8: Implement JWT, refresh-token cookies, and token issuance

Create `JwtUtil` first, then `RefreshTokenCookieService`, then `AuthTokenService`. In `JwtUtil`, write `generateTokenFromEmailAndRoles`, `getEmailFromToken`, `getRolesFromToken`, `validateToken`, and the signing-key helpers. `JwtUtil` should only know how to create and parse access tokens; it should not decide business rules like rotation or revocation. Then create `RefreshTokenCookieService` to centralize cookie flags such as `HttpOnly`, `Secure`, `SameSite`, `Path`, and optional domain. Finally, create `AuthTokenService` with `issueTokens`, `refreshTokens`, and `revokeRefreshToken`, because token lifecycle belongs in one dedicated place. The design choice here is deliberate: access tokens are short-lived JWTs so APIs stay stateless, but refresh tokens are random opaque secrets stored hashed in the database so they can be revoked and rotated. This is safer than issuing one long-lived JWT because long-lived stateless tokens are hard to revoke after logout, device loss, or account compromise.

Files to create in this step:
`backend/src/main/java/com/auth/security/jwt/JwtUtil.java`, `backend/src/main/java/com/auth/security/RefreshTokenCookieService.java`, `backend/src/main/java/com/auth/service/auth/AuthTokenService.java`

### Task 9: Build the auth support services before the main auth workflow

Before writing `AuthServiceImpl`, create the support services it depends on: `OtpService`, `TokenHashService`, `PasswordPolicyService`, `EmailService`, and `RateLimitService`. Then build `AuthAbuseProtectionService` on top of rate limiting and user lock-state rules. This order keeps `AuthServiceImpl` focused on orchestration rather than low-level mechanics. `OtpService` generates secure random OTPs and reset tokens, `TokenHashService` hashes those values with a server-side pepper, `PasswordPolicyService` enforces strength rules, `EmailService` sends HTML and plain-text mail, and `RateLimitService` uses Redis to track attempts. `AuthAbuseProtectionService` is created after those because it is a policy coordinator, not a primitive. The important design decision is that Redis failures are handled in a fail-open way for rate limiting, because temporary protection degradation is better than full authentication outage in most production systems.

Files to create in this step:
`backend/src/main/java/com/auth/service/support/OtpService.java`, `backend/src/main/java/com/auth/service/support/TokenHashService.java`, `backend/src/main/java/com/auth/service/support/PasswordPolicyService.java`, `backend/src/main/java/com/auth/service/support/EmailService.java`, `backend/src/main/java/com/auth/service/support/RateLimitService.java`, `backend/src/main/java/com/auth/service/auth/AuthAbuseProtectionService.java`, `backend/src/main/resources/templates/emails/*.html`

### Task 10: Write the main authentication workflow service

Create `AuthService` first, then `AuthServiceImpl`. Inside `AuthServiceImpl`, implement the public methods in this exact order: `register`, `verifyOtp`, `login`, `resetPassword`, `updatePassword`, `resendOtp`, and `changePassword`, then add the private helpers. That order mirrors the product journey and reduces mental context switching while coding. `register` should normalize email, check uniqueness, validate the password, map the request, hash the password, generate and hash the OTP, assign the default role, save the user, and send the verification email. `verifyOtp` should enforce abuse protection, verify hash match, verify expiry, enable the account, clear OTP state, save, and send the welcome email. `login` should check rate limits and lockouts, authenticate with `AuthenticationManager`, and issue fresh tokens through `AuthTokenService`, while the password-reset and password-change flows should validate password policy and always clear used reset tokens. This service is the best example of senior-level design in the project because it composes many collaborators while keeping each operation transactional and readable.

Files to create in this step:
`backend/src/main/java/com/auth/service/AuthService.java`, `backend/src/main/java/com/auth/service/impl/AuthServiceImpl.java`

Recommended method order in `AuthServiceImpl`:
`register`, `verifyOtp`, `login`, `resetPassword`, `updatePassword`, `resendOtp`, `changePassword`, `requireUserByEmail`, `requireEmailNotVerified`, `verifyOtpMatch`, `requireTokenNotExpired`, `storeVerificationOtp`, `storeResetToken`, `clearResetToken`, `authenticateCredentials`, `normalizeEmail`, `sendOtpEmailSafely`, `sendResetEmailSafely`, `sendWelcomeEmailSafely`, `sendPasswordChangedEmailSafely`

### Task 11: Configure the HTTP security pipeline

After the business auth flow exists, create `JwtAuthFilter` and `SecurityConfig`, then add OAuth2-specific handlers and resolvers. `JwtAuthFilter` should do only three things: extract the bearer token, validate it, and build an authenticated `SecurityContext`. Then `SecurityConfig` should wire CORS, CSRF policy, headers, stateless behavior for JWT requests, temporary session behavior for OAuth2 state handling, authorization rules, OAuth2 success and failure handlers, and the placement of the JWT filter before `UsernamePasswordAuthenticationFilter`. This comes after the auth services because the filter chain needs real collaborators, not placeholders. We do not place business logic in filters, because filters should remain transport-level and reusable. Security headers and cookie policy are configured centrally because security decisions should be uniform across the application, not manually repeated per endpoint.

Files to create in this step:
`backend/src/main/java/com/auth/security/jwt/JwtAuthFilter.java`, `backend/src/main/java/com/auth/config/SecurityConfig.java`, `backend/src/main/java/com/auth/security/oauth2/OAuth2AuthenticationSuccessHandler.java`, `backend/src/main/java/com/auth/security/oauth2/OAuth2AuthenticationFailureHandler.java`, `backend/src/main/java/com/auth/security/oauth2/LinkedInAuthorizationRequestResolver.java`, `backend/src/main/java/com/auth/service/auth/OAuth2UserProvisioningService.java`

### Task 12: Expose REST endpoints only after the service layer is stable

Create the controllers after the service layer and security pipeline are already working. Start with `AuthController`, then `UserController`, then `AdminController`, because authentication endpoints are foundational, user endpoints are simpler protected endpoints, and admin endpoints usually depend on more filtering and authorization detail. In `AuthController`, implement `register`, `verifyOtp`, `login`, `refreshToken`, `logout`, `resetPassword`, `updatePassword`, and `resendOtp`, then add private helpers for refresh-token candidate extraction and cookie writing. Controllers should remain thin: validate input, call the service, and shape the HTTP response. They should not hash tokens, enforce password policy, or query repositories directly, because that makes behavior inconsistent and hard to test. `RefreshTokenCookieService` is used here so the controller does not manually construct cookie headers and accidentally drift from security policy.

Files to create in this step:
`backend/src/main/java/com/auth/controller/AuthController.java`, `backend/src/main/java/com/auth/controller/UserController.java`, `backend/src/main/java/com/auth/controller/AdminController.java`

Recommended method order in `AuthController`:
`register`, `verifyOtp`, `login`, `refreshToken`, `logout`, `resetPassword`, `updatePassword`, `resendOtp`, `resolveRefreshTokenCandidates`, `extractRefreshTokensFromCookies`, `setRefreshTokenCookie`

### Task 13: Build user-portal and admin read models

Now create `UserPortalService`, `UserPortalServiceImpl`, `AdminService`, and `AdminServiceImpl`. These services are read-oriented and should stay separate from the authentication write flows so dashboards do not become tangled with login logic. `UserPortalServiceImpl` should provide `getDashboard` and `getProfile`, while `AdminServiceImpl` should provide `getDashboard` and `getUsers` with pagination, filtering, sorting, and search specifications. Add caching here, not earlier, because caching is an optimization over stable read behavior rather than a prerequisite for correctness. The admin user listing uses `JpaSpecificationExecutor` because dynamic filters are easier to evolve with specifications than with one repository method per filter combination. The allowed sort-field whitelist is also important because letting clients sort by arbitrary fields can produce fragile queries and security surprises.

Files to create in this step:
`backend/src/main/java/com/auth/service/UserPortalService.java`, `backend/src/main/java/com/auth/service/AdminService.java`, `backend/src/main/java/com/auth/service/impl/UserPortalServiceImpl.java`, `backend/src/main/java/com/auth/service/impl/AdminServiceImpl.java`

### Task 14: Add cross-cutting framework configuration and startup seeding

After the main use cases are complete, create `CorsConfig`, `CacheConfig`, `SpringDataWebConfig`, and `DataInitializer`. `CorsConfig` should be explicit and never use a wildcard when credentials are enabled, because browsers reject that combination and it is insecure. `CacheConfig` should define short TTLs because profile and dashboard data benefit from speed but should not remain stale for long. `SpringDataWebConfig` ensures page responses serialize predictably for the frontend. `DataInitializer` should seed roles every time and an admin account only when explicitly enabled by configuration. A senior engineer keeps seeding opt-in because creating privileged accounts automatically in all environments is risky and can cause surprise access paths.

Files to create in this step:
`backend/src/main/java/com/auth/config/CorsConfig.java`, `backend/src/main/java/com/auth/config/CacheConfig.java`, `backend/src/main/java/com/auth/config/SpringDataWebConfig.java`, `backend/src/main/java/com/auth/config/DataInitializer.java`

### Task 15: Standardize failure handling

Create the custom exceptions and `GlobalExceptionHandler` after the service contracts are known. Start with focused exception classes like `UserAlreadyExistsException`, `ResourceNotFoundException`, `TokenValidationException`, `RateLimitExceededException`, and `AccountLockedException`, then map them centrally in `GlobalExceptionHandler`. This keeps controllers clean and makes the API behavior consistent across the entire application. A good backend does not leak stack traces or framework-specific messages to the frontend. It returns safe, predictable responses and adds headers like `Retry-After` where they matter for rate limits and temporary lockouts. Centralized error handling is also one of the easiest ways to keep frontend integration stable while backend logic evolves internally.

Files to create in this step:
`backend/src/main/java/com/auth/exception/*.java`

### Task 16: Add tests after the architecture stabilizes

Finally, add unit and slice tests for the most critical components: `AuthServiceImpl`, `AuthTokenService`, `OAuth2UserProvisioningService`, `CustomUserDetailsService`, and the controllers. A senior engineer does not wait until the very end of the project to think about testability, but full test implementation becomes much easier after service boundaries and DTOs are stable. Service tests should validate business rules such as duplicate registration, token rotation, OTP expiry, password reset, and abuse-protection behavior. Controller tests should verify endpoint status codes, request validation, response shapes, and security access rules. This repository already follows that direction with targeted tests under `src/test/java`. The reason we test these layers specifically is that they carry most of the behavioral risk and regressions in an authentication system.

Files to create in this step:
`backend/src/test/java/com/auth/service/**/*.java`, `backend/src/test/java/com/auth/controller/**/*.java`, `backend/src/test/java/com/auth/security/**/*.java`

## Why These Design Choices Are Strong

### Why use layered architecture instead of putting logic in controllers?

Controllers are HTTP adapters, not business engines. If registration, login, or password reset logic lives inside controllers, it becomes difficult to reuse, difficult to test, and difficult to protect with transactions. Services are the right place for business workflows because they can compose repositories, encoders, token services, and emails in one controlled unit. This also keeps the API transport independent from domain logic, which matters if the project later adds GraphQL, messaging, or scheduled jobs. Senior teams protect maintainability by keeping boundaries strict early.

### Why use JWT access tokens plus database-backed refresh tokens?

A short-lived access token is good for stateless API authorization because the server does not need to load session data on every request. But a long-lived JWT used as both access and refresh token is hard to revoke, hard to rotate, and risky after compromise. This project solves that by using JWT only for the short-lived access token and random opaque strings for refresh tokens. The refresh token is stored in hashed form and rotated every time it is used. That means logout, expiry, and compromise handling become much more practical. This is a stronger real-world design than “single JWT for everything”.

### Why hash OTPs, reset tokens, and refresh tokens?

These values are bearer credentials. Anyone who gets the raw value can act as the user for that specific flow. If such values are stored in plain text, a database leak immediately becomes an account-takeover event. By hashing them with a server-side pepper, the stored database value is not directly usable. This mirrors the same security principle behind password hashing, although implementation details differ because OTPs and refresh tokens are verified differently. Senior engineers treat all reusable secrets as credentials, not as harmless temporary strings.

### Why use `AuthenticationManager` and `CustomUserDetailsService` instead of manual password comparison?

Spring Security already provides a mature authentication pipeline with password encoders, error handling, and integration points. Reusing that pipeline reduces custom security code and makes the behavior more standard. `CustomUserDetailsService` adapts the project’s domain model to that pipeline cleanly. Manual password verification sprinkled across services or controllers often leads to duplicated logic and subtle mistakes. In security code, boring and standard is usually better than clever and custom.

### Why use Redis for both cache and rate limiting?

Redis is fast, external, and safe for shared counters and short-lived cached data. That makes it a good fit for both request throttling and small read-model acceleration. If the application runs on multiple backend instances, in-memory rate limiting becomes inconsistent, but Redis stays shared across nodes. Short cache TTLs improve dashboard and profile latency without introducing long-lived stale state. The project is correctly using Redis as a support system, not as the primary source of truth.

### Why centralize cookies in `RefreshTokenCookieService`?

Cookie behavior becomes surprisingly tricky once you deal with local HTTP, production HTTPS, `SameSite=None`, proxy headers, and frontend/backend cross-site flows. If every controller or handler sets cookies manually, those rules drift and bugs appear only in certain environments. Centralizing cookie creation gives one place to enforce `HttpOnly`, `Secure`, `SameSite`, `Path`, and expiration policy. This is the kind of small architectural move that prevents a lot of production debugging later. Good engineering often looks like moving fragile logic into one dedicated abstraction.

## Practical Method-by-Method Build Sequence

If you were recreating this backend from scratch, the most practical coding sequence would be:

1. Configure dependencies and environment properties.
2. Create constants and shared utilities.
3. Create entities and enums.
4. Create repositories.
5. Create DTOs and response wrappers.
6. Create user/role services and mapper.
7. Create password encoder and `CustomUserDetailsService`.
8. Create JWT utility, refresh-cookie service, and token service.
9. Create OTP, token-hash, password-policy, email, and rate-limit support services.
10. Create abuse-protection service.
11. Create auth workflow service.
12. Create JWT filter and security configuration.
13. Create OAuth2 provisioning and handlers.
14. Create auth, user, and admin controllers.
15. Create admin/user portal services with caching.
16. Create startup seeding and centralized exception handling.
17. Add tests for service logic, controllers, and security behavior.

## Key Classes to Study First in This Codebase

If someone joins the project and wants to understand the backend quickly, the best reading order is:

1. `backend/src/main/resources/application.properties`
2. `backend/src/main/java/com/auth/entity/User.java`
3. `backend/src/main/java/com/auth/config/SecurityConfig.java`
4. `backend/src/main/java/com/auth/service/impl/AuthServiceImpl.java`
5. `backend/src/main/java/com/auth/service/auth/AuthTokenService.java`
6. `backend/src/main/java/com/auth/security/jwt/JwtAuthFilter.java`
7. `backend/src/main/java/com/auth/controller/AuthController.java`
8. `backend/src/main/java/com/auth/service/auth/AuthAbuseProtectionService.java`
9. `backend/src/main/java/com/auth/security/RefreshTokenCookieService.java`
10. `backend/src/main/java/com/auth/exception/GlobalExceptionHandler.java`

## Senior Engineering Notes for Future Improvements

If this project grows further, the next improvements I would consider are database migrations with Flyway or Liquibase instead of relying on Hibernate DDL, device-aware refresh-token tracking instead of one refresh token per user, audit-event persistence for security-sensitive actions, background email delivery for resilience, and stronger observability around lockouts and token failures. I would also consider splitting authentication, user portal, and admin reporting into clearer modules if the team and feature set continue to expand. None of those changes are necessary to understand the current architecture, but they are natural next steps for production hardening. The current structure is already solid because it separates security primitives, workflow orchestration, and transport concerns in a clean way. That is exactly the kind of design that scales better in a team environment.

## Final Takeaway

The correct way to build this backend is not “controller first” and not “JWT first”. The correct order is foundation, domain, persistence, contracts, reusable services, security primitives, auth workflows, HTTP security, controllers, and finally optimizations such as caching and seeding. That order keeps the project stable because each layer depends on lower layers that are already defined. This repository follows that pattern well, especially in its token design, email verification flow, abuse protection, and exception handling. If you explain this backend in an interview, a document review, or a company handoff, focus on that structure and on the security tradeoffs it makes deliberately. That is what makes the codebase feel like senior-level backend engineering rather than just a collection of Spring Boot files.
