# 🔐 Full-Stack Authentication System — Concepts & Security Deep Dive

> **Audience:** Developers, reviewers, and interviewers who want a thorough understanding of every security mechanism and architectural pattern implemented in this backend.
>
> **Source:** Every concept documented below was extracted directly from the project source code (`backend/src/main/java/com/auth/**`, `application.properties`, `Dockerfile`, and `docker-compose.yaml`).

---

## Table of Contents

1. [Stateless JWT Authentication](#1-stateless-jwt-authentication)
2. [Refresh Token Rotation & Revocation](#2-refresh-token-rotation--revocation)
3. [Peppered SHA-256 Token Hashing](#3-peppered-sha-256-token-hashing)
4. [HttpOnly Secure Cookie Transport](#4-httponly-secure-cookie-transport)
5. [BCrypt Password Hashing](#5-bcrypt-password-hashing)
6. [Password Policy Enforcement](#6-password-policy-enforcement)
7. [OTP-Based Email Verification](#7-otp-based-email-verification)
8. [Secure Password Reset Flow](#8-secure-password-reset-flow)
9. [Redis-Backed Fixed-Window Rate Limiting](#9-redis-backed-fixed-window-rate-limiting)
10. [Brute-Force Account & OTP Lockout](#10-brute-force-account--otp-lockout)
11. [OAuth2 / OpenID Connect Social Login](#11-oauth2--openid-connect-social-login)
12. [Role-Based Access Control (RBAC)](#12-role-based-access-control-rbac)
13. [Method-Level Security (@PreAuthorize)](#13-method-level-security-preauthorize)
14. [HTTP Security Headers Hardening](#14-http-security-headers-hardening)
15. [CORS Whitelisting (No Wildcards)](#15-cors-whitelisting-no-wildcards)
16. [CSRF Protection Strategy](#16-csrf-protection-strategy)
17. [Centralized Exception Handling & Information Leakage Prevention](#17-centralized-exception-handling--information-leakage-prevention)
18. [Bean Validation (Jakarta Validation)](#18-bean-validation-jakarta-validation)
19. [Email Normalization & Canonicalization](#19-email-normalization--canonicalization)
20. [Constant-Time Token Comparison](#20-constant-time-token-comparison)
21. [Secure Random Number Generation](#21-secure-random-number-generation)
22. [Spring Data JPA Auditing (BaseEntity)](#22-spring-data-jpa-auditing-baseentity)
23. [Redis Caching with Per-Cache TTL](#23-redis-caching-with-per-cache-ttl)
24. [API Versioning & Path Constants](#24-api-versioning--path-constants)
25. [MapStruct DTO Mapping](#25-mapstruct-dto-mapping)
26. [Layered Architecture & Separation of Concerns](#26-layered-architecture--separation-of-concerns)
27. [Global Configuration Externalization](#27-global-configuration-externalization)
28. [Docker Multi-Stage Build & Compose Orchestration](#28-docker-multi-stage-build--compose-orchestration)
29. [Seed Data Initializer](#29-seed-data-initializer)
30. [Thymeleaf HTML Email Templates](#30-thymeleaf-html-email-templates)
31. [Security Event Email Notifications](#31-security-event-email-notifications)
32. [Fail-Open Rate Limiting (Graceful Degradation)](#32-fail-open-rate-limiting-graceful-degradation)
33. [Stateless Session Policy with OAuth2 Exception](#33-stateless-session-policy-with-oauth2-exception)
34. [Retry-After Header for Rate-Limited & Locked Responses](#34-retry-after-header-for-rate-limited--locked-responses)

---

## 1. Stateless JWT Authentication

**Concept:** JSON Web Tokens (JWT) allow the server to authenticate requests without maintaining server-side session state.

### How It Works in This Project

| Component | File |
|-----------|------|
| Token generation & validation | `security/jwt/JwtUtil.java` |
| Per-request filter | `security/jwt/JwtAuthFilter.java` |
| Security filter chain setup | `config/SecurityConfig.java` |

**Detailed Mechanism:**

1. **Token Generation** — When a user logs in (or signs in via OAuth2), `JwtUtil.generateTokenFromEmailAndRoles()` creates a signed JWT containing:
   - `sub` (subject) → The user's email address
   - `roles` → A list of granted authority strings (e.g., `ROLE_USER`, `ROLE_ADMIN`)
   - `tokenType` → `"access"` (distinguishes from refresh tokens)
   - `iat` (issued-at) → Timestamp of creation
   - `exp` (expiration) → Current time + configurable `jwt.expiration` (default: 15 minutes / 900,000 ms)

2. **Signing Algorithm** — The token is signed using **HMAC-SHA256** (`HS256`) via the `io.jsonwebtoken` JJWT library. The signing key is derived from a secret that must be **at least 32 bytes** (validated at startup via `@PostConstruct`). The key decoding supports **Base64**, **Base64URL**, and **plain UTF-8** fallback for developer convenience.

3. **Per-Request Validation** — `JwtAuthFilter` extends `OncePerRequestFilter` and is inserted **before** `UsernamePasswordAuthenticationFilter` in the Spring Security filter chain. On every request, it:
   - Extracts the `Authorization: Bearer <token>` header
   - Validates the token's signature and expiry via `JwtUtil.validateToken()`
   - Extracts the email and roles from the token's claims
   - Builds a `UsernamePasswordAuthenticationToken` and places it in the `SecurityContextHolder`
   - **No database hit** occurs to verify the token — this is purely stateless

4. **Startup Validation** — The `@PostConstruct` method in `JwtUtil` ensures that:
   - The `jwt.expiration` value is greater than zero
   - The signing key can be successfully derived and meets the minimum length requirement
   - This prevents the application from starting with a misconfigured secret

**Why This Matters:**
- Eliminates server-side session storage, making the application horizontally scalable
- Each microservice or replica can independently verify tokens without shared state
- Short-lived access tokens (15 min default) limit the blast radius of token compromise

---

## 2. Refresh Token Rotation & Revocation

**Concept:** Refresh tokens are long-lived credentials that allow clients to obtain new access tokens without re-authenticating. **Token rotation** means a new refresh token is issued every time the old one is used — the old one is invalidated.

### How It Works in This Project

| Component | File |
|-----------|------|
| Token issuance, rotation, revocation | `service/auth/AuthTokenService.java` |
| Cookie handling | `security/RefreshTokenCookieService.java` |
| Controller logic | `controller/AuthController.java` |

**Detailed Mechanism:**

1. **Generation** — `AuthTokenService.generateRefreshToken()` creates a **64-byte cryptographically random** value using `java.security.SecureRandom`, then encodes it as a **Base64URL** string (no padding). This produces a token with **512 bits of entropy** — astronomically difficult to brute-force.

2. **Storage** — Only the **peppered SHA-256 hash** of the refresh token is stored in the `users` table (`refresh_token` column, up to 512 characters). The raw token is never persisted. This means a database breach does not directly expose usable refresh tokens.

3. **Rotation** — Every time `refreshTokens()` is called:
   - The incoming raw token is hashed and matched against the stored hash
   - If valid and not expired, **a completely new refresh token** is generated
   - The old hash is replaced with the new hash
   - A fresh access token is issued alongside
   - This means each refresh token is **single-use**

4. **Revocation** — `revokeRefreshToken()` hashes the incoming token, finds the matching user, and sets `refreshToken` and `refreshTokenExpiry` to `null`. This is called during logout.

5. **Expiry** — The refresh token expiry is configurable via `jwt.refresh.expiration` (default: 7 days / 604,800,000 ms). The `refreshTokenExpiry` is stored as a `LocalDateTime` in the database and checked server-side.

**Why This Matters:**
- Rotation ensures that if a refresh token is stolen, the attacker's window is limited to the single use before the legitimate user's next refresh invalidates it
- Server-side storage enables instant revocation (unlike stateless JWTs)

---

## 3. Peppered SHA-256 Token Hashing

**Concept:** Sensitive tokens (OTP codes, reset tokens, refresh tokens) should never be stored in plain text. This project uses SHA-256 hashing with a server-side **pepper** — a secret value that is concatenated with the token before hashing.

### How It Works in This Project

| Component | File |
|-----------|------|
| Hashing & matching logic | `service/support/TokenHashService.java` |

**Detailed Mechanism:**

1. **Hash Formula:** `SHA-256(pepper + ":" + rawToken)` → Base64URL encoding (no padding)
2. **Pepper Source:** Configured via `security.token-hash-pepper`, which defaults to the JWT secret (`jwt.secret`). This means even if an attacker gains read access to the database, they cannot reverse the hashes without also knowing the pepper.
3. **Constant-Time Comparison:** The `matches()` method uses `MessageDigest.isEqual()` — a JDK-provided constant-time comparison that prevents **timing side-channel attacks** where an attacker could infer partial hash correctness by measuring response latency.

**What Gets Hashed:**
- OTP codes (stored in `users.verification_otp`)
- Password reset tokens (stored in `users.reset_token`)
- Refresh tokens (stored in `users.refresh_token`)

**Why This Matters:**
- Database breach doesn't expose usable tokens
- Pepper adds a second layer beyond hashing: attackers need both the database dump AND the pepper
- Constant-time comparison prevents timing oracle attacks

---

## 4. HttpOnly Secure Cookie Transport

**Concept:** Refresh tokens are transported via HTTP-only cookies rather than JavaScript-accessible storage (localStorage/sessionStorage), preventing XSS attacks from stealing them.

### How It Works in This Project

| Component | File |
|-----------|------|
| Cookie construction | `security/RefreshTokenCookieService.java` |
| Cookie setting in responses | `controller/AuthController.java` |

**Cookie Attributes Set:**

| Attribute | Value | Purpose |
|-----------|-------|---------|
| `HttpOnly` | `true` (always) | JavaScript cannot access the cookie — prevents XSS token theft |
| `Secure` | `auto` / `true` / `false` | Auto-detects HTTPS from `X-Forwarded-Proto` or `request.isSecure()` |
| `SameSite` | `auto` / `Strict` / `Lax` / `None` | Auto-detects cross-site vs same-site using `Sec-Fetch-Site`, `Origin`, and `Referer` headers |
| `Path` | `/api/v1/auth` | Cookie is only sent to auth endpoints (not to the entire domain) |
| `Domain` | Configurable (empty by default) | Can be set for subdomain sharing |
| `Max-Age` | `jwt.refresh.expiration / 1000` seconds | Aligns with refresh token lifetime |

**Auto-Detection Logic:**

The `RefreshTokenCookieService` implements sophisticated auto-detection:

1. **`Secure` auto-detection** — Checks `X-Forwarded-Proto` header (for reverse proxies) or `request.isSecure()` to determine if the request arrived over HTTPS.
2. **`SameSite` auto-detection** — Inspects the `Sec-Fetch-Site` header first (the most reliable signal). Falls back to comparing `Origin` or `Referer` header domains against the backend's host. If a cross-site mismatch is detected, it uses `SameSite=None`; otherwise, `SameSite=Lax`.
3. **Safety Guardrail** — If `SameSite=None` would be set without `Secure=true`, the system falls back to `Lax` (since browsers reject `None` without `Secure`). A `@PostConstruct` validator prevents invalid static configurations at startup.

**Why This Matters:**
- HTTP-only cookies are immune to XSS-based token theft (the #1 attack vector for SPAs)
- Path-scoped cookies minimize exposure surface
- Auto-detection handles dev (HTTP localhost) and production (HTTPS) transparently

---

## 5. BCrypt Password Hashing

**Concept:** BCrypt is a deliberately slow, adaptive hashing algorithm designed for password storage.

### How It Works in This Project

| Component | File |
|-----------|------|
| Encoder bean | `config/PasswordConfig.java` |
| Usage | `service/impl/AuthServiceImpl.java`, `config/DataInitializer.java`, `service/auth/OAuth2UserProvisioningService.java` |

**Key Details:**
- Spring's `BCryptPasswordEncoder` is used with the **default strength factor of 10** (2^10 = 1,024 iterations)
- Every `register()`, `updatePassword()`, and `changePassword()` call uses `passwordEncoder.encode()` — BCrypt automatically generates a unique salt per hash
- Password verification uses `passwordEncoder.matches()` which internally uses constant-time comparison
- OAuth2 users are given a random UUID password (`UUID.randomUUID()`) encoded with BCrypt, making their password column non-nullable while being effectively unusable for credential login

**Why This Matters:**
- BCrypt's computational cost makes offline brute-force attacks impractical (each guess takes ~100ms)
- Per-hash unique salts prevent rainbow table attacks
- The `BCryptPasswordEncoder` default is OWASP-recommended

---

## 6. Password Policy Enforcement

**Concept:** A dedicated policy service rejects weak passwords before they're ever hashed and stored.

### How It Works in This Project

| Component | File |
|-----------|------|
| Policy engine | `service/support/PasswordPolicyService.java` |

**Rules Enforced:**

| Rule | Check |
|------|-------|
| Minimum length | ≥ 6 characters |
| No whitespace | Must not contain spaces |
| Letter required | At least one letter (A-Z, a-z) |
| Digit required | At least one number (0-9) |
| Common-password blocklist | 16+ blocked passwords (e.g., `password`, `123456`, `qwerty`, `admin`) — case-insensitive |
| Email similarity check | Rejects passwords containing the user's email local part (if ≥ 3 chars) |

**Where It's Called:**
- `register()` — Before creating the account
- `updatePassword()` — Before resetting via token
- `changePassword()` — Before changing for authenticated user

**Why This Matters:**
- Prevents the most common password choices that attackers try first
- Email-similarity check prevents trivially guessable passwords tied to the user's identity
- Centralized policy ensures consistent enforcement across all password-setting flows

---

## 7. OTP-Based Email Verification

**Concept:** After registration, the account is disabled until the user verifies ownership of the email address by entering a one-time password (OTP) sent to their inbox.

### How It Works in This Project

| Component | File |
|-----------|------|
| OTP generation | `service/support/OtpService.java` |
| Verification flow | `service/impl/AuthServiceImpl.java` |
| Email delivery | `service/support/EmailService.java` |

**Detailed Flow:**

```
User registers → Account created with enabled=false
                → 6-digit OTP generated via SecureRandom
                → OTP hashed (peppered SHA-256) and stored
                → Plain OTP sent via email (Thymeleaf HTML template)
                → User submits OTP
                → Hash comparison (constant-time)
                → If valid & not expired → enabled=true, OTP cleared, welcome email sent
```

**OTP Properties:**
- **6-digit numeric** code: `100_000 + SecureRandom.nextInt(900_000)` — exactly 6 digits, always
- **Expiry:** Configurable via `otp.expiration.minutes` (default: 5 minutes)
- **Single-use:** Cleared from the database immediately after successful verification
- **Hashed storage:** Never stored in plain text — always peppered SHA-256
- **Rate-limited resend:** Subject to per-email cooldown (60s), per-email window limit (3 per 15 min), and per-IP limit

---

## 8. Secure Password Reset Flow

**Concept:** A token-based password reset flow that prevents email enumeration and stores tokens securely.

### How It Works in This Project

| Component | File |
|-----------|------|
| Reset token generation | `service/support/OtpService.java` |
| Reset flow | `service/impl/AuthServiceImpl.java` |
| Email delivery | `service/support/EmailService.java` |

**Detailed Flow:**

```
User requests reset → Generic response ALWAYS returned (prevents email enumeration)
                    → If email exists: 32-byte random token generated
                    → Token hashed (peppered SHA-256) and stored
                    → URL-safe reset link sent via email
                    → User clicks link → Frontend submits token + new password
                    → Token hash compared (constant-time)
                    → If valid & not expired → Password updated, token cleared
                    → Confirmation email sent
```

**Anti-Enumeration Design:**
The `resetPassword()` method **always** returns `"If an account exists with this email, a reset link will be sent."` — regardless of whether the email exists. This is a deliberate OWASP-recommended practice to prevent attackers from discovering which emails are registered.

**Reset Token Properties:**
- **32-byte** cryptographically random (`SecureRandom`)
- **Base64URL-encoded** (URL-safe, no padding)
- **Expiry:** Configurable via `auth.reset-token.expiration.minutes` (default: 5 minutes)
- **Single-use:** Cleared immediately after successful password update

---

## 9. Redis-Backed Fixed-Window Rate Limiting

**Concept:** Each authentication endpoint has independent rate limits based on both IP address and email address, implemented using Redis atomic counters with TTL-based sliding windows.

### How It Works in This Project

| Component | File |
|-----------|------|
| Rate limit engine | `service/support/RateLimitService.java` |
| Endpoint coordination | `service/auth/AuthAbuseProtectionService.java` |

**Configured Limits:**

| Endpoint | Key Type | Limit | Window |
|----------|----------|-------|--------|
| Login | Per IP | 5 attempts | 60 seconds |
| Login | Per email | 10 attempts | 15 minutes |
| OTP verification | Per IP | 20 attempts | 10 minutes |
| OTP verification | Per email | 5 attempts | 10 minutes |
| Resend OTP | Per email (cooldown) | 1 | 60 seconds |
| Resend OTP | Per email (window) | 3 | 15 minutes |
| Resend OTP | Per IP | 20 | 15 minutes |
| Password reset | Per email | 3 | 30 minutes |
| Password reset | Per IP | 10 | 30 minutes |

**Redis Implementation:**
```
INCR auth:login:ip:192.168.1.1    → Returns count
EXPIRE auth:login:ip:192.168.1.1 60   → TTL set on first request
```
- Uses `StringRedisTemplate.opsForValue().increment()` (atomic `INCR`)
- TTL set via `expire()` only when the counter reaches `1` (first request in window)
- The `RateLimitDecision` record returns `allowed`, `retryAfterSeconds`, `count`, and `limit`

**IP Resolution:**
The `resolveClientIp()` method handles reverse-proxy scenarios:
1. First checks `X-Forwarded-For` header (takes the first IP in the chain)
2. Falls back to `X-Real-IP` header
3. Falls back to `request.getRemoteAddr()`
4. Returns `"unknown"` as a last resort

---

## 10. Brute-Force Account & OTP Lockout

**Concept:** Beyond rate limiting, per-user lockout counters provide an additional layer of protection against brute-force attacks.

### How It Works in This Project

| Component | File |
|-----------|------|
| Lockout logic | `service/auth/AuthAbuseProtectionService.java` |
| User entity fields | `entity/User.java` |

**Login Lockout:**
- **Threshold:** 10 failed attempts (`auth.bruteforce.login.max-attempts`)
- **Lock duration:** 15 minutes (`auth.bruteforce.login.lock-minutes`)
- After reaching the threshold, `accountLockedUntil` is set, and `failedLoginAttempts` is reset to 0
- An **account-locked security alert email** is sent to the user
- On successful login, both `failedLoginAttempts` and `accountLockedUntil` are cleared

**OTP Lockout:**
- **Threshold:** 5 failed attempts (`auth.bruteforce.otp.max-attempts`)
- **Lock duration:** 10 minutes (`auth.bruteforce.otp.lock-minutes`)
- Uses separate fields: `failedOtpAttempts` and `otpLockedUntil`
- On successful OTP verification, counters are cleared

**Lock Check:**
Both `assertLoginNotLocked()` and `assertOtpNotLocked()` compare the lock expiry against the current time. If the lock is still active, they throw `AccountLockedException` with a `retryAfterSeconds` value.

---

## 11. OAuth2 / OpenID Connect Social Login

**Concept:** Users can authenticate using their existing accounts from trusted identity providers (Google, GitHub, Apple, LinkedIn) via the OAuth2 Authorization Code flow.

### How It Works in This Project

| Component | File |
|-----------|------|
| Security config | `config/SecurityConfig.java` |
| Success handler | `security/oauth2/OAuth2AuthenticationSuccessHandler.java` |
| Failure handler | `security/oauth2/OAuth2AuthenticationFailureHandler.java` |
| User provisioning | `service/auth/OAuth2UserProvisioningService.java` |
| LinkedIn resolver | `security/oauth2/LinkedInAuthorizationRequestResolver.java` |
| Provider config | `application.properties` (lines 108–148) |

**Supported Providers:**
- **Google** — OpenID Connect (`openid, profile, email` scopes)
- **GitHub** — OAuth2 (`read:user, user:email` scopes)
- **Apple** — OpenID Connect (`openid, name, email` scopes)
- **LinkedIn** — OpenID Connect with custom nonce removal (LinkedIn doesn't handle nonces correctly)

**User Provisioning Logic (`loadOrCreateUser`):**
1. Normalize the provider name (lowercase, validated against `SUPPORTED_PROVIDERS`)
2. Extract the provider-specific user ID (GitHub uses `id`/`node_id`/`login`; others use `sub`/`id`)
3. First lookup: Find by `authProvider` + `authProviderUserId` (most reliable)
4. If not found: Extract email → Find by email → Link existing account OR create new one
5. New OAuth users get: `enabled=true`, `ROLE_USER`, a random BCrypt-encoded password

**LinkedIn-Specific Handling:**
The `LinkedInAuthorizationRequestResolver` removes the OIDC `nonce` parameter from LinkedIn authorization requests. LinkedIn's OIDC implementation has known compatibility issues with nonce validation, causing auth failures if the nonce is present.

**Post-Authentication:**
After successful OAuth2 login, the `OAuth2AuthenticationSuccessHandler`:
1. Provisions/loads the local user
2. Issues JWT access + refresh token pair
3. Sets the refresh token as an HTTP-only cookie
4. Redirects to the frontend's `/oauth2/callback` endpoint

---

## 12. Role-Based Access Control (RBAC)

**Concept:** The system restricts access to endpoints based on the user's assigned roles.

### How It Works in This Project

| Component | File |
|-----------|------|
| Role entity & enum | `entity/Role.java`, `entity/RoleName.java` |
| User-role mapping | `entity/User.java` (ManyToMany) |
| URL-pattern authorization | `config/SecurityConfig.java` |
| Method-level authorization | `controller/AdminController.java`, `controller/UserController.java` |

**Roles Defined:**
- `ROLE_USER` — Standard authenticated user
- `ROLE_ADMIN` — Administrative user with full user management access

**Authorization Matrix:**

| Path Pattern | Access |
|---|---|
| `/api/v1/auth/**` | Public (permitAll) |
| `/oauth2/**`, `/login/oauth2/**` | Public (permitAll) |
| `/api/v1/admin/**` | `ROLE_ADMIN` only |
| `/api/v1/user/**` | `ROLE_USER` or `ROLE_ADMIN` |
| All other paths | Authenticated (any role) |

**Database Design:**
- `roles` table with `IDENTITY`-generated ID and `UNIQUE` name (enum-backed `VARCHAR(20)`)
- `user_roles` join table (`user_id` ↔ `role_id` ManyToMany relationship with `EAGER` fetch)

---

## 13. Method-Level Security (@PreAuthorize)

**Concept:** Beyond URL-pattern rules, Spring's `@PreAuthorize` provides fine-grained per-method access control using SpEL expressions.

### How It Works in This Project

- Enabled via `@EnableMethodSecurity` on `SecurityConfig`
- `AdminController` uses `@PreAuthorize("hasRole('ADMIN')")` at the **class level** — all endpoints require admin
- `UserController` uses `@PreAuthorize("hasRole('USER') or hasRole('ADMIN')")` at the **method level** — both roles can access user endpoints

**Why Both URL-Pattern AND Method-Level?**
This implements **defense-in-depth**: even if a misconfiguration allows a request past the URL filter, the method-level check provides a second barrier.

---

## 14. HTTP Security Headers Hardening

**Concept:** HTTP response headers instruct browsers to enforce additional security policies.

### How It Works in This Project

| Header | Value | Purpose |
|--------|-------|---------|
| `X-Content-Type-Options` | `nosniff` | Prevents MIME-type sniffing attacks |
| `X-Frame-Options` | `DENY` | Prevents clickjacking via iframes |
| `Referrer-Policy` | `no-referrer` | Prevents leaking URLs to third parties |
| `Permissions-Policy` | `camera=(), microphone=(), geolocation=()` | Blocks browser APIs not needed by this app |
| `Strict-Transport-Security` | `max-age=31536000; includeSubDomains` | Forces HTTPS for 1 year with subdomain coverage |

All configured in `SecurityConfig.filterChain()` using Spring Security's `headers()` DSL.

---

## 15. CORS Whitelisting (No Wildcards)

**Concept:** Cross-Origin Resource Sharing (CORS) controls which frontend origins can make API requests.

### How It Works in This Project

| Component | File |
|-----------|------|
| CORS config | `config/CorsConfig.java` |

**Key Design Decisions:**
- **No wildcard (`*`) origins allowed** — An explicit `IllegalStateException` is thrown at startup if `*` is detected when `allowCredentials=true`
- **Origins are comma-separated and configurable** via `app.cors.allowed-origins`
- **Allowed methods:** `GET`, `POST`, `PUT`, `DELETE`, `OPTIONS`
- **Allowed headers:** `Authorization`, `Content-Type`, `X-Requested-With`
- **Exposed headers:** `Authorization` (so the frontend can read it)
- **Credentials:** `true` (required for cookie-based refresh token transport)
- **Preflight cache:** `maxAge=3600` (1 hour) — reduces OPTIONS request overhead

---

## 16. CSRF Protection Strategy

**Concept:** CSRF (Cross-Site Request Forgery) protection is **explicitly disabled** in this project — this is a deliberate and correct decision for a stateless JWT-based API.

### Rationale

CSRF attacks exploit the browser's automatic cookie-sending behavior with traditional session cookies. This application:
- Uses **stateless JWT Bearer tokens** in the `Authorization` header for authentication — these are not automatically attached by browsers
- Refresh tokens are in cookies but are **path-scoped to `/api/v1/auth`** only, limiting the attack surface
- All mutation endpoints require the `Authorization` header, which cannot be injected by a CSRF attack

This aligns with OWASP guidance: _"If your application is using JWTs for authentication, CSRF protection is typically not needed."_

---

## 17. Centralized Exception Handling & Information Leakage Prevention

**Concept:** A `@ControllerAdvice` translates all exceptions into consistent, safe API responses — never leaking stack traces, internal paths, or implementation details.

### How It Works in This Project

| Component | File |
|-----------|------|
| Global handler | `exception/GlobalExceptionHandler.java` |
| Custom exceptions | `exception/AccountLockedException.java`, `exception/RateLimitExceededException.java`, etc. |
| Response wrapper | `dto/response/ApiResponse.java` |

**Exception → HTTP Status Mapping:**

| Exception | HTTP Status | Notes |
|-----------|-------------|-------|
| `MethodArgumentNotValidException` | 400 Bad Request | Validation errors aggregated into a single message |
| `IllegalArgumentException` | 400 Bad Request | Service-layer validation failures |
| `BadCredentialsException` | 401 Unauthorized | **Generic message: "Invalid email or password!"** (prevents user enumeration) |
| `TokenValidationException` | 401 Unauthorized | Invalid/expired JWT or reset token |
| `ResourceNotFoundException` | 404 Not Found | User/role not found |
| `UserAlreadyExistsException` | 409 Conflict | Duplicate email registration |
| `AccountLockedException` | 423 Locked | With `Retry-After` header |
| `RateLimitExceededException` | 429 Too Many Requests | With `Retry-After` header |
| `Exception` (catch-all) | 500 Internal Server Error | **Generic message** — original error is logged server-side only |

**Anti-Enumeration in Login:**
The `BadCredentialsException` handler always returns `"Invalid email or password!"` — it never reveals whether the email exists. The `AuthServiceImpl.login()` also records a failed login attempt even when the user doesn't exist, to prevent timing-based enumeration.

---

## 18. Bean Validation (Jakarta Validation)

**Concept:** Request DTOs use Jakarta Bean Validation annotations to enforce input constraints before the request reaches the service layer.

### How It Works in This Project

**RegisterRequest Example:**
```java
@NotBlank(message = "Name is required")
private String name;

@NotBlank(message = "Email is required")
@Email(message = "Please provide a valid email")
private String email;

@NotBlank(message = "Password is required")
@Size(min = 6, message = "Password must be at least 6 characters")
private String password;
```

**Admin Controller Query Params:**
```java
@Min(0) int page
@Min(1) @Max(100) int size
@Pattern(regexp = "(?i)USER|ADMIN|ROLE_USER|ROLE_ADMIN") String role
@Pattern(regexp = "(?i)asc|desc") String sortDir
```

Controllers use `@Valid` on `@RequestBody` parameters, and validation errors are caught by the `GlobalExceptionHandler`.

---

## 19. Email Normalization & Canonicalization

**Concept:** All email addresses are normalized (trimmed + lowercased) before any comparison, storage, or lookup to prevent case-mismatch bypasses.

### How It Works in This Project

| Component | File |
|-----------|------|
| Normalizer utility | `util/EmailNormalizer.java` |
| Used throughout | `AuthServiceImpl`, `CustomUserDetailsService`, `AuthAbuseProtectionService`, `OAuth2UserProvisioningService` |

**Methods:**
- `normalizeOrNull(email)` — Returns `email.trim().toLowerCase(Locale.ROOT)` or `null` if blank
- `normalizeOr(email, fallback)` — Same, with a default fallback value

**Why This Matters:**
Without normalization, `User@Example.COM` and `user@example.com` would be treated as different users. This could lead to:
- Duplicate account creation
- Rate-limit bypass by varying email casing
- OTP/reset token mismatches

---

## 20. Constant-Time Token Comparison

**Concept:** Token comparison must execute in the same amount of time regardless of how many characters match, to prevent timing side-channel attacks.

### How It Works in This Project

`TokenHashService.matches()` uses `MessageDigest.isEqual()`:
```java
byte[] computed = hash(rawToken).getBytes(StandardCharsets.UTF_8);
byte[] stored = storedHash.getBytes(StandardCharsets.UTF_8);
return MessageDigest.isEqual(computed, stored);
```

**Why This Matters:**
A naive string comparison like `equals()` returns `false` as soon as the first mismatch is found. An attacker could measure response times to deduce how many leading characters of their guess are correct. `MessageDigest.isEqual()` always iterates through all bytes.

---

## 21. Secure Random Number Generation

**Concept:** All security-critical random values use `java.security.SecureRandom` — a cryptographically strong random number generator.

### Where It's Used

| Random Value | Entropy | File |
|---|---|---|
| OTP codes | 6-digit numeric | `OtpService.java` |
| Password reset tokens | 32 bytes → Base64URL | `OtpService.java` |
| Refresh tokens | 64 bytes → Base64URL | `AuthTokenService.java` |
| OAuth2 user passwords | UUID (128-bit) | `OAuth2UserProvisioningService.java` |

`SecureRandom` obtains entropy from the operating system's CSPRNG (e.g., `/dev/urandom` on Linux), making it suitable for cryptographic use cases.

---

## 22. Spring Data JPA Auditing (BaseEntity)

**Concept:** All entities automatically track creation and modification timestamps via a shared `BaseEntity` superclass using JPA lifecycle callbacks.

### How It Works in This Project

| Component | File |
|-----------|------|
| Base entity | `entity/BaseEntity.java` |
| Timestamp utility | `util/DateTimeUtil.java` |

```java
@MappedSuperclass
public abstract class BaseEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist → sets createdAt + updatedAt to IST now
    @PreUpdate  → sets updatedAt to IST now
}
```

**Both `User` and `Role`** extend `BaseEntity`, gaining automatic audit fields.

**Why This Matters:**
- Provides a tamper-evident audit trail for all entities
- `updatedAt` changes on every write, useful for detecting suspicious account modifications
- `createdAt` is `updatable = false`, ensuring it can never be overwritten

---

## 23. Redis Caching with Per-Cache TTL

**Concept:** Frequently accessed read data is cached in Redis with configurable TTL (Time-To-Live) per cache region.

### How It Works in This Project

| Component | File |
|-----------|------|
| Cache config | `config/CacheConfig.java`, `config/CacheNames.java` |
| Admin cache | `service/impl/AdminServiceImpl.java` |
| User profile cache | `service/impl/UserPortalServiceImpl.java` |

**Cache Regions & TTLs:**

| Cache Name | TTL | What's Cached |
|---|---|---|
| `user-profile` | 60 seconds | User profile DTO (keyed by email) |
| `admin-dashboard` | 30 seconds | Admin dashboard metrics (keyed by admin email) |
| Default | 30 seconds | Any other cacheable data |

**Key Design Decisions:**
- Uses `@Cacheable` with SpEL key expressions: `#email == null ? 'unknown' : #email.toLowerCase()`
- JSON serialization via `GenericJackson2JsonRedisSerializer` (human-readable in Redis)
- `disableCachingNullValues()` prevents caching empty results
- Short TTLs intentionally balance performance with data freshness

---

## 24. API Versioning & Path Constants

**Concept:** All API paths are centralized in a constants class and use URI-based versioning (`/api/v1/...`).

### How It Works in This Project

```java
public final class ApiPaths {
    public static final String API_BASE    = "/api";
    public static final String API_V1_BASE = API_BASE + "/v1";
    public static final String AUTH_V1     = API_V1_BASE + "/auth";   // /api/v1/auth
    public static final String USER_V1     = API_V1_BASE + "/user";   // /api/v1/user
    public static final String ADMIN_V1    = API_V1_BASE + "/admin";  // /api/v1/admin
}
```

**Security Relevance:**
- Security rules in `SecurityConfig` reference `ApiPaths.AUTH_V1 + "/**"` instead of hard-coded strings
- Reduces the risk of typos causing security misconfigurations
- Makes API version migration straightforward (add `API_V2_BASE`)

---

## 25. MapStruct DTO Mapping

**Concept:** MapStruct generates type-safe, compile-time mapping code between entities and DTOs, eliminating manual boilerplate and reducing the risk of exposing sensitive entity fields.

### How It Works in This Project

| Component | File |
|-----------|------|
| Mapper interface | `mapper/UserMapper.java` |

**Key Mappings:**
- `RegisterRequest → User` — Ignores 14+ managed fields (password, roles, tokens, lock counters) to prevent mass-assignment vulnerabilities
- `User → UserDto` — Safely exposes only public-facing fields; adds computed `loginSource`
- `User → UserDashboardDto` — Maps name to `user` field, adds message/timestamp

**Security Relevance:**
By explicitly `@Mapping(target = "...", ignore = true)` on sensitive fields, the mapper acts as a whitelist — only explicitly mapped fields are transferred. This prevents accidental exposure of hashed passwords, tokens, or lock state in API responses.

---

## 26. Layered Architecture & Separation of Concerns

**Concept:** The project follows a strict layered architecture pattern.

### Layer Design

```
┌───────────────────────────────────────────────┐
│  Controller Layer (REST endpoints)            │
│  AuthController, UserController, AdminController │
├───────────────────────────────────────────────┤
│  Service Layer (Business Logic)               │
│  AuthService, UserService, AdminService       │
│  AuthTokenService, AuthAbuseProtectionService │
│  Support: EmailService, OtpService, etc.      │
├───────────────────────────────────────────────┤
│  Security Layer (Authentication/Authorization)│
│  JwtAuthFilter, CustomUserDetailsService      │
│  OAuth2 Handlers, RefreshTokenCookieService   │
├───────────────────────────────────────────────┤
│  Repository Layer (Data Access)               │
│  UserRepository, RoleRepository               │
├───────────────────────────────────────────────┤
│  Entity Layer (Domain Model)                  │
│  User, Role, BaseEntity                       │
└───────────────────────────────────────────────┘
```

**Interface Segregation:**
All service classes implement interfaces (`AuthService`, `UserService`, `AdminService`, `UserPortalService`, `RoleService`), enabling:
- Mockability in unit tests
- Dependency inversion: controllers depend on abstractions, not implementations
- Easy swapping of implementations (e.g., replacing email provider)

---

## 27. Global Configuration Externalization

**Concept:** All configuration values are externalized via `application.properties` with environment variable overrides, following the **12-Factor App** methodology.

### How It Works

Every configurable value uses the pattern:
```properties
property.name=${ENV_VAR_NAME:default_value}
```

**Categories Externalized:**
- Database connection (`SPRING_DATASOURCE_*`)
- Redis connection (`SPRING_DATA_REDIS_*`)
- JWT secrets and expiration (`JWT_SECRET`, `JWT_EXPIRATION`)
- Mail server credentials (`SPRING_MAIL_*`)
- OAuth2 provider credentials (`OAUTH_GOOGLE_*`, `OAUTH_GITHUB_*`, etc.)
- Frontend URLs (`APP_FRONTEND_URL`)
- CORS origins (`APP_CORS_ALLOWED_ORIGINS`)
- Cookie behavior (`AUTH_REFRESH_TOKEN_COOKIE_*`)
- Rate limits and lockout thresholds
- Seed admin credentials (`APP_SEED_ADMIN_*`)

**Special: .env Import**
```properties
spring.config.import=optional:file:.env[.properties]
```
This allows a local `.env` file to supply secrets without modifying `application.properties`.

**Security-Critical Configs:**
- `JWT_SECRET` — **Required** — No default, application fails to start without it
- `SPRING_DATASOURCE_PASSWORD` — **Required** in Docker compose (`:?` syntax)
- All OAuth secrets default to `"disabled"` when not configured

---

## 28. Docker Multi-Stage Build & Compose Orchestration

**Concept:** The project uses Docker multi-stage builds for minimal image size and Docker Compose for full-stack orchestration.

### Dockerfile (Multi-Stage Build)

```dockerfile
# Stage 1: Build with full Maven + JDK 21
FROM maven:3.9.11-eclipse-temurin-21 AS build
COPY pom.xml . && RUN mvn dependency:go-offline
COPY src ./src && RUN mvn package

# Stage 2: Run with minimal JRE 21
FROM eclipse-temurin:21-jre
COPY --from=build /app/target/*.jar app.jar
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
```

**Why Multi-Stage:** The final image contains ONLY the JRE and the JAR — no Maven, no source code, no build tools. This reduces attack surface and image size.

### Docker Compose Services

| Service | Image | Purpose |
|---------|-------|---------|
| `postgres` | `postgres:16-alpine` | Primary database with health checks |
| `adminer` | `adminer:latest` | Database admin UI (port 5050) |
| `redis` | `redis:7-alpine` | Cache & rate limiting store with AOF persistence |
| `redis-commander` | `rediscommander/redis-commander` | Redis admin UI (port 8081) |
| `app` | Custom build | Spring Boot backend (port 8080) |
| `frontend` | Custom build | React frontend (port 5173 via nginx) |

**Health Checks:** Both `postgres` and `redis` have health checks. The `app` service depends on both being `service_healthy` before starting, ensuring the backend never starts with unavailable dependencies.

---

## 29. Seed Data Initializer

**Concept:** An optional `CommandLineRunner` seeds default roles and an initial admin account at application startup.

### How It Works

| Component | File |
|-----------|------|
| Initializer | `config/DataInitializer.java` |

**Behavior:**
1. **Always** creates `ROLE_USER` and `ROLE_ADMIN` roles if they don't exist (idempotent)
2. **If `app.seed.admin.enabled=true`:**
   - Validates that name, email, and password are all configured
   - Creates the admin user with both `ROLE_ADMIN` and `ROLE_USER` roles
   - Password is BCrypt-encoded
   - Account is created as `enabled=true` (pre-verified)
   - Skips creation if the email already exists (idempotent)

---

## 30. Thymeleaf HTML Email Templates

**Concept:** All transactional emails use Thymeleaf templates for rich HTML rendering with plain-text fallbacks.

### Email Types

| Email Type | Template File | Trigger |
|---|---|---|
| OTP Verification | `emails/otp-verification.html` | Registration, OTP resend |
| Password Reset Request | `emails/password-reset-request.html` | Forgot password |
| Welcome | `emails/welcome-account.html` | Successful OTP verification |
| Password Changed | `emails/password-changed-confirmation.html` | Password update or change |
| Account Locked Alert | `emails/account-locked-alert.html` | Brute-force lockout triggered |

All emails include:
- **Brand name** (configurable via `app.email.brand-name`)
- **Recipient name** (falls back to email local part or `"there"`)
- **Current year** (dynamic copyright)
- **Both HTML and plain-text** bodies (multipart MIME)

---

## 31. Security Event Email Notifications

**Concept:** Users receive email notifications for security-sensitive events, enabling rapid response to unauthorized activity.

### Events That Trigger Emails

| Event | Email Type | Purpose |
|---|---|---|
| Account locked | Security Alert | Warns user of brute-force attempt |
| Password changed | Confirmation | Alerts user to verify the change was intentional |
| Password reset via token | Confirmation | Same as above |
| OTP verification success | Welcome | Confirms account activation |

**Resilience:**
All email-sending calls are wrapped in `try/catch` blocks (e.g., `sendPasswordChangedEmailSafely()`). Email delivery failures are logged as warnings but **never fail the primary operation**. This ensures that a mail server outage doesn't prevent password changes or logins.

---

## 32. Fail-Open Rate Limiting (Graceful Degradation)

**Concept:** If Redis is unavailable, rate limiting silently allows all requests rather than rejecting them — ensuring authentication availability isn't impacted by a cache outage.

### How It Works

In `RateLimitService.consume()`:
```java
catch (Exception exception) {
    log.warn("Rate limiting unavailable for key={}", key, exception);
    return new RateLimitDecision(true, -1, 0, limit); // allowed=true
}
```

**Why Fail-Open:**
- Authentication is a critical path — users must be able to log in even during Redis outages
- The alternative (fail-closed) would cause a total authentication outage if Redis goes down
- The risk is acceptable because brute-force protection is still partially provided by per-user lockouts (stored in PostgreSQL, not Redis)

---

## 33. Stateless Session Policy with OAuth2 Exception

**Concept:** The application uses `SessionCreationPolicy.IF_REQUIRED` instead of the more typical `STATELESS` — this is a deliberate architectural decision to support OAuth2.

### Why Not STATELESS?

OAuth2 Authorization Code flow requires **temporary session storage** for:
- CSRF state parameter validation
- OIDC nonce validation
- Authorization request attributes during the redirect dance

Setting `STATELESS` would break OAuth2 social login because Spring Security wouldn't be able to recover the original authorization request after the provider redirects back.

The `IF_REQUIRED` policy creates sessions **only when needed** (during OAuth2 flows) and doesn't create them for regular JWT-authenticated API calls.

---

## 34. Retry-After Header for Rate-Limited & Locked Responses

**Concept:** When a request is rejected due to rate limiting or account lockout, the response includes a `Retry-After` HTTP header telling the client exactly how long to wait.

### How It Works

```java
// RateLimitExceededException → 429
headers.set(HttpHeaders.RETRY_AFTER, String.valueOf(Math.max(1, ex.getRetryAfterSeconds())));

// AccountLockedException → 423
headers.set(HttpHeaders.RETRY_AFTER, String.valueOf(Math.max(1, ex.getRetryAfterSeconds())));
```

The `retryAfterSeconds` value is computed from:
- **Rate limits:** Redis key TTL (time until the window resets)
- **Account locks:** Duration between now and `accountLockedUntil` / `otpLockedUntil`

The `Math.max(1, ...)` ensures the header is never 0 or negative.

**Why This Matters:**
- Enables the frontend to display accurate countdown timers
- Follows HTTP standard (RFC 6585 and RFC 7231)
- Prevents clients from hammering the server with retries

---

## Summary

This project implements a **defense-in-depth** security architecture with **34 distinct security and architectural concepts**, layered together to provide comprehensive protection:

| Layer | Concepts Applied |
|-------|-----------------|
| **Transport** | HTTPS enforcement (HSTS), HTTP-only cookies, Secure/SameSite flags, CORS whitelist |
| **Authentication** | Stateless JWT, BCrypt passwords, OAuth2/OIDC, OTP email verification |
| **Authorization** | RBAC, URL-pattern rules, `@PreAuthorize` method security |
| **Token Security** | Peppered SHA-256 hashing, refresh token rotation, secure random generation, constant-time comparison |
| **Anti-Abuse** | Redis rate limiting, per-user brute-force lockout, email enumeration prevention |
| **Data Protection** | DTO whitelisting (MapStruct), generic error messages, no stack trace exposure |
| **Operational** | Docker multi-stage builds, environment externalization, graceful degradation, security event emails |
| **Browser Hardening** | X-Frame-Options DENY, Content-Type nosniff, Referrer-Policy, Permissions-Policy |
