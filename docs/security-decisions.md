# Security Architecture: Detailed Implementation Guide

This document explains the security decisions used in the Full-Stack Authentication System and, just as importantly, the tradeoffs behind them. The goal is not just to say "we use JWT" or "we hash passwords," but to connect each decision to the actual code and to the threat it is trying to reduce.

The explanations below are aligned with the current implementation in:

| Security area | Main classes/files |
| --- | --- |
| HTTP security and authorization rules | `SecurityConfig`, `CorsConfig`, `JwtAuthFilter` |
| Access tokens and refresh tokens | `JwtUtil`, `AuthTokenService`, `RefreshTokenCookieService`, `AuthController` |
| Password and token hashing | `PasswordConfig`, `PasswordPolicyService`, `TokenHashService` |
| OTP, reset, login, and recovery flows | `AuthServiceImpl`, `OtpService` |
| Abuse protection and lockouts | `AuthAbuseProtectionService`, `RateLimitService` |
| OAuth2 social login | `OAuth2AuthenticationSuccessHandler`, `OAuth2AuthenticationFailureHandler`, `OAuth2UserProvisioningService` |
| Error handling and information leakage control | `GlobalExceptionHandler` |

---

## 1. Security Goals and Threat Model

This system is trying to balance four things at the same time:

1. **Protect secrets** such as passwords, OTPs, reset links, and refresh tokens from being exposed in the browser or database.
2. **Keep the API stateless for normal authenticated traffic** so the backend can scale without sticky server sessions.
3. **Reduce common web attack paths** such as XSS token theft, brute-force login attempts, clickjacking, token replay, and account enumeration.
4. **Keep the user experience practical** by allowing silent token refresh, email verification, password reset, and OAuth login without requiring the user to constantly sign in again.

This means the design is intentionally layered. No single mechanism is treated as "the security feature." Instead, the system combines:

- short-lived access tokens
- cookie-isolated refresh tokens
- strict route authorization
- hashed storage for secrets
- Redis-backed abuse controls
- generic error messages where disclosure would help an attacker
- browser hardening headers and explicit CORS rules

That layered approach matters because real systems rarely fail in one dramatic place. They usually fail when several small assumptions stack up in the same direction.

---

## 2. Token Strategy and Browser Security

The application uses a **dual-token model**:

| Token | Format | Lifetime | Where it lives | What it is used for |
| --- | --- | --- | --- | --- |
| Access token | JWT signed with HS256 | `900000 ms` = 15 minutes | Frontend memory only | Attached to protected API requests as `Authorization: Bearer ...` |
| Refresh token | Opaque random string | `604800000 ms` = 7 days | `HttpOnly` cookie | Used only to obtain a new access token |

### Why split tokens in two?

If we used one long-lived token for everything, it would be convenient but dangerous. A stolen token would remain useful for far too long.

By separating responsibilities:

- the **access token** is convenient and fast for normal API authorization
- the **refresh token** is better protected because the browser stores it in a cookie that JavaScript cannot read

This gives the system a better balance between usability and exposure time.

### How the access token works

The access token is created in `JwtUtil` and contains:

- the user's email as the JWT subject
- a `tokenType=access` claim
- the user's role list in a `roles` claim
- an expiration timestamp

Because the roles are embedded inside the token, `JwtAuthFilter` can validate the signature, extract the claims, and build a Spring Security `Authentication` object without querying the database on every request.

That is the performance benefit of JWT in this project: once a user has a valid access token, most protected requests can be authorized without a server-side session lookup.

### Why the access token is kept in memory

On the frontend, `frontend/src/services/api.js` keeps the access token in a module-level variable named `accessToken`. It is **not** written to `localStorage` or cookies.

That matters for XSS defense:

- if malicious JavaScript runs in the page, it can read values in `localStorage`
- it cannot directly read an in-memory variable unless it is already running inside the app's own execution context
- it also cannot read an `HttpOnly` cookie at all

Keeping the access token only in memory means a full page refresh clears it automatically. That is not a bug; it is a deliberate containment boundary.

### What is still stored in `localStorage`

The frontend does store a `user` object in `localStorage`, but it contains only profile data:

- `id`
- `name`
- `email`
- `enabled`
- `roles`

This is a useful distinction:

- **credential material** stays out of `localStorage`
- **non-secret UI state** is allowed there for smoother rendering after reload

This is safer than storing the actual bearer token in `localStorage`, but it is not the same thing as "nothing auth-related is stored locally." The profile metadata is still visible to browser JavaScript.

### How the refresh token works

The refresh token is generated in `AuthTokenService` as:

- 64 cryptographically random bytes
- Base64URL encoded
- opaque, meaning it carries no readable claims like a JWT

The raw refresh token is sent to the browser in a cookie built by `RefreshTokenCookieService`. The database never stores the raw token. Instead, the system stores:

- a SHA-256 hash of the token
- the refresh token expiry time

That means a database leak does not immediately give an attacker reusable refresh tokens.

### Why the refresh token is in an `HttpOnly` cookie

The refresh cookie is built with:

- `HttpOnly=true`
- `path=/api/v1/auth`
- `SameSite` decided dynamically
- `Secure` decided dynamically
- optional domain override

The important security effects are:

- `HttpOnly` stops frontend JavaScript from reading the token directly
- `path=/api/v1/auth` stops the cookie from being sent to unrelated application routes
- `Secure` ensures the cookie is only sent over HTTPS when enabled
- `SameSite` reduces cross-site abuse depending on deployment shape

### Auto-detected cookie policy

`RefreshTokenCookieService` does more than hard-code cookie flags. It inspects the request and deployment environment to choose the safest compatible policy:

- if the request is HTTPS, `Secure` becomes `true`
- if the frontend and backend are cross-site, `SameSite` becomes `None`
- if they are same-site, `SameSite` becomes `Lax`
- if local HTTP development would make `SameSite=None` invalid, it safely falls back to `Lax`

This is a practical engineering decision. A cookie policy that is secure in production but impossible to use locally often gets disabled entirely. Auto-detection tries to avoid that "security-by-commenting-it-out" failure mode.

### Refresh flow step by step

The refresh flow works like this:

1. The frontend makes a protected API request with the access token.
2. If the server responds with `401`, Axios in `frontend/src/services/api.js` attempts `/auth/refresh`.
3. The backend reads refresh token candidates from the request body first and then from cookies.
4. `AuthTokenService.refreshTokens(...)` hashes the raw token and looks up the matching stored hash.
5. If the stored token exists and is not expired, the service issues a **new access token and a new refresh token**.
6. The new refresh token hash overwrites the old one in the database.
7. The response sends the new refresh token back as a cookie.

This is **refresh token rotation**.

### Why rotation matters

Rotation means the refresh token is effectively single-use over time:

- after a successful refresh, the previously stored token hash is replaced
- the previous raw token no longer matches the database
- replaying an old refresh token fails

This reduces the value of a stolen refresh token because it stops working after the legitimate user refreshes again.

### Important tradeoff: one active refresh token per user

The current `User` entity stores only one `refreshToken` hash and one `refreshTokenExpiry`.

That means the system is simple to reason about, but it also means:

- logging in on a second device overwrites the first device's refresh token
- the older device can keep using its current access token until it expires
- after that, the older device will fail to refresh and will be forced to log in again

So the design strongly favors simple revocation over true multi-device session independence.

### Why the app is "mostly stateless," not absolutely stateless

For normal authenticated API traffic, the app is stateless: the server does not keep a traditional login session for bearer-token requests.

However, `SecurityConfig` sets session creation to `IF_REQUIRED` because the OAuth2 handshake needs temporary session storage for state and nonce validation. So the precise statement is:

> The application is stateless for normal JWT-based API traffic, with a temporary session exception for the OAuth2 authorization handshake.

That is more accurate than simply saying "the system has no sessions at all."

---

## 3. Request Authentication and Authorization

### JWT validation on every protected request

`JwtAuthFilter` runs before `UsernamePasswordAuthenticationFilter` and does three things:

1. reads the `Authorization` header
2. checks that it starts with `Bearer `
3. validates the JWT signature and expiry using `JwtUtil`

If the token is valid, the filter creates a Spring Security principal with the user's email and granted authorities. That principal is then placed into `SecurityContextHolder`, which makes downstream authorization work without a server-side session lookup.

### Route protection strategy

`SecurityConfig` uses a fail-closed style:

- `/api/v1/auth/**` is public
- `/oauth2/**` and `/login/oauth2/**` are public for the login handshake
- `/api/v1/admin/**` requires `ROLE_ADMIN`
- `/api/v1/user/**` requires `ROLE_USER` or `ROLE_ADMIN`
- `.anyRequest().authenticated()` blocks everything else by default

This is safer than an allow-everything-by-default model because forgotten routes do not accidentally become public.

### Method-level authorization

The app also uses `@EnableMethodSecurity` and controller-level `@PreAuthorize` rules.

That gives two layers of protection:

- route matching in the filter chain
- role checks on controller methods

This is helpful because authorization bugs often appear when one layer gets refactored and another does not.

### Important tradeoff: role claims are cached inside the JWT

Because roles are copied into the access token at issuance time:

- a request does not need a database lookup to know the user's role
- but role changes do not affect already-issued access tokens immediately

In practice, that means role revocation takes full effect when the user receives a fresh access token through login or refresh. With a 15-minute access-token lifetime, that window is limited, but it still exists.

### Important design choice: `enabled` is not used as a login gate

`CustomUserDetailsService` deliberately sets Spring Security's `enabled` flag to `true` for all loaded users. In this project, the `enabled` field on the entity means **email verified**, not **allowed to authenticate**.

Why do that?

- it allows users with pending verification to still sign in
- the frontend can then guide them through OTP verification and resend flows

What is the tradeoff?

- verification is now an application-level state, not a hard authentication barrier
- any business feature that truly requires "verified users only" must check that state explicitly

This is one of the most important behavioral details in the codebase because it is easy to assume `enabled=false` means Spring Security blocks login. In this app, it does not.

---

## 4. Browser and Network Hardening

### CORS is explicit because credentials are enabled

`CorsConfig` allows credentials and therefore refuses wildcard origins.

The allowed origins come from `app.cors.allowed-origins`, and the example config includes:

- `http://localhost:5173`
- `http://localhost:3000`
- `https://authsystem-plum.vercel.app`

This matters because when cookies are involved, permissive CORS can quietly become a data exfiltration problem.

The configuration also narrows:

- allowed methods to `GET, POST, PUT, DELETE, OPTIONS`
- allowed headers to `Authorization, Content-Type, X-Requested-With`
- exposed headers to `Authorization`

### Security headers currently enabled

`SecurityConfig` sets several browser hardening headers:

| Header | Purpose |
| --- | --- |
| `Strict-Transport-Security` | Tells browsers to keep using HTTPS after first contact |
| `X-Frame-Options: DENY` | Prevents clickjacking by blocking iframe embedding |
| `X-Content-Type-Options: nosniff` | Stops MIME-type sniffing tricks |
| `Referrer-Policy: no-referrer` | Prevents URL leakage through the `Referer` header |
| `Permissions-Policy: camera=(), microphone=(), geolocation=()` | Disables browser features the app does not need |

### Why `Referrer-Policy: no-referrer` is useful here

This header is especially relevant in authentication systems because URLs sometimes contain sensitive workflow state, such as reset-password links or OAuth-related transitions. Preventing referer leakage reduces the chance of those URLs being forwarded to third-party sites by the browser.

### CSRF is disabled, but not for a simplistic reason

`SecurityConfig` disables Spring Security's built-in CSRF protection.

The main reason is that most protected API requests are authenticated with an explicit bearer token in the `Authorization` header. Browsers do not automatically attach that header to cross-site requests the way they automatically attach ordinary cookies.

That said, the full story is more nuanced:

- protected business endpoints rely on bearer tokens, so classic cookie-based CSRF is greatly reduced there
- the refresh and logout endpoints can still use the refresh cookie
- those endpoints are constrained by cookie path, `SameSite`, and strict CORS rules

So the real design is not "CSRF does not exist." The real design is:

> Most privileged API access depends on explicit bearer presentation, while the cookie-backed refresh flow is narrowed with browser cookie controls and origin restrictions.

That is safer and more honest than saying "CSRF is disabled because we use JWT."

---

## 5. Cryptography, Randomness, and Secret Storage

### Password hashing

Passwords are hashed with `BCryptPasswordEncoder` from `PasswordConfig`.

Why BCrypt?

- it is intentionally slow compared to fast hash functions like SHA-256
- it includes a salt automatically
- it is a mature and widely used password hashing choice in Spring applications

This makes offline password cracking much more expensive than if raw SHA-256 were used for passwords.

### Password policy

`PasswordPolicyService` adds application-level guardrails before a password is even hashed. A password must:

- be at least 6 characters long
- contain no whitespace
- contain at least one letter
- contain at least one number
- not be in a small common-password blocklist
- not contain the email's local-part username when that part is meaningful

This is not a replacement for hashing. It is a separate control that tries to stop obviously weak secrets from entering the system at all.

### OTP, reset token, and refresh token generation

`OtpService` and `AuthTokenService` use `SecureRandom` for all token generation:

- OTPs are 6-digit numeric codes
- password reset tokens are 32 random bytes, Base64URL encoded
- refresh tokens are 64 random bytes, Base64URL encoded

The use of `SecureRandom` matters because tokens used for account recovery or long-lived session renewal must not be guessable.

### OTP, reset token, and refresh token storage

These values are **not** stored like passwords:

- passwords use BCrypt
- OTPs, reset tokens, and refresh tokens use `TokenHashService`
- `TokenHashService` computes SHA-256 over `pepper + ":" + rawToken`

This design is appropriate because OTPs and random tokens are already high-entropy secrets generated by the server. They do not need the same slow-password-hashing treatment as human-chosen passwords.

### Why use a pepper for token hashes

The token hash service supports a server-side pepper via `security.token-hash-pepper`, and if that value is empty it falls back to `jwt.secret`.

The pepper means that a database dump alone is not enough for an attacker to verify guessed token values offline. They would also need the server-side secret material.

### Constant-time comparison

When comparing a submitted OTP to the stored OTP hash, `TokenHashService.matches(...)` uses `MessageDigest.isEqual(...)`, which is a constant-time comparison primitive.

That reduces timing side-channel leakage during direct token comparison.

### JWT secret validation

`JwtUtil` also hardens configuration:

- `jwt.secret` must not be blank
- it can be plain text, Base64, or Base64URL encoded
- after decoding, it must be at least 32 bytes for HS256 signing
- `jwt.expiration` must be greater than zero

This is a good example of security work that happens before runtime attacks even begin. Misconfiguration is one of the easiest ways to weaken an otherwise correct design.

---

## 6. Abuse Protection and Brute-Force Resistance

### Why Redis is used here

`AuthAbuseProtectionService` delegates rate limiting to `RateLimitService`, which uses Redis through `StringRedisTemplate`.

Redis is a good fit because:

- counters are fast
- expirations are built in
- the protection can work across multiple backend instances

The current rate limiter uses a **fixed-window counter** model, not a sliding window or token bucket. That keeps the implementation simple and predictable.

### Current rate-limit rules

The example configuration sets the following limits:

| Flow | Scope | Limit |
| --- | --- | --- |
| Login | per IP | 5 requests per 60 seconds |
| Login | per email | 10 requests per 900 seconds |
| OTP verify | per IP | 20 requests per 600 seconds |
| OTP verify | per email | 5 requests per 600 seconds |
| Resend OTP | per email cooldown | 1 request per 60 seconds |
| Resend OTP | per email | 3 requests per 900 seconds |
| Resend OTP | per IP | 20 requests per 900 seconds |
| Reset password | per email | 3 requests per 1800 seconds |
| Reset password | per IP | 10 requests per 1800 seconds |

This is intentionally mixed:

- some rules protect infrastructure from spam
- some protect individual accounts from brute-force attention
- some protect email delivery channels from abuse

### Current lockout rules

In addition to Redis rate limits, the app stores account-specific failure counters in the `User` entity:

| Flow | Threshold | Lock duration |
| --- | --- | --- |
| Login | 10 failed attempts | 15 minutes |
| OTP verification | 5 failed attempts | 10 minutes |

This adds a second layer:

- Redis counters are fast and external
- user-level lock timestamps persist in the main database

That means an attacker cannot simply wait for one short Redis window to reset if they have already triggered an account-level lock.

### Why both IP-based and account-based limits exist

These limits solve different problems:

- **IP-based limits** slow noisy abuse from one source
- **email/account-based limits** help when attackers distribute attempts across many IPs

If the system used only IP limits, a botnet could bypass them. If it used only account limits, one attacker could still flood many unrelated accounts from a single machine. Using both is more balanced.

### Retry-after support

When a rate limit or lockout is hit, `GlobalExceptionHandler` adds a `Retry-After` header.

That is useful for:

- frontend UX
- API clients
- observability and debugging

It is also a sign of a well-behaved API: the system does not just say "no," it tells the client when retrying becomes reasonable.

### Important tradeoff: fail open on Redis outage

`RateLimitService` intentionally fails open if Redis is unavailable. That means:

- legitimate users are not locked out of authentication because Redis went down
- but brute-force and spam protections temporarily weaken during the outage

This is a classic availability-versus-protection tradeoff. The project explicitly chooses login continuity over full abuse protection in Redis failure scenarios.

### Account lock email alerts

When repeated login failures lock an account, the service attempts to send an alert email. This does not stop the lock from being applied if email delivery fails, which is the correct security priority.

---

## 7. Registration, Verification, Recovery, and Anti-Enumeration

### Email normalization

The app normalizes email addresses by trimming whitespace and lowercasing them through `EmailNormalizer`.

This prevents subtle account duplication problems such as treating `Alice@example.com` and `alice@example.com` as different identities in some code paths but not others.

### Registration flow

When a new user registers:

1. the email is normalized
2. uniqueness is checked
3. the password policy is enforced
4. the password is BCrypt-hashed
5. a user is created with `enabled=false`
6. a verification OTP is generated and hashed
7. the user receives the `ROLE_USER` role
8. an OTP email is sent

This means the database stores:

- the password hash
- the OTP hash
- the OTP expiry
- not the raw OTP itself

### OTP verification flow

When the user submits the OTP:

- abuse protections run first
- the user is loaded
- the system checks that the account is not already verified
- the submitted OTP is hash-compared with the stored OTP hash
- the expiry is checked
- the account is marked `enabled=true`
- OTP state and failure counters are cleared

Only after that does the app send the welcome email.

### Resend OTP flow

The resend endpoint is protected by:

- per-email cooldown
- per-email rate limit
- per-IP rate limit

This is important because OTP resend endpoints are often abused to harass users or burn email/SMS quotas.

### Password reset flow

The password reset request endpoint intentionally returns the same success message whether the account exists or not:

> "If an account exists with this email, a reset link will be sent."

This is an anti-enumeration control. It stops the endpoint from acting like an email-discovery oracle for attackers.

If the account does exist:

- a random reset token is generated
- only its hash is stored
- an expiry timestamp is stored
- the raw reset link is emailed to the user

### Password update with reset token

When the user follows the reset link and submits a new password:

- the raw token from the request is hashed
- the database is searched by hashed reset token
- expiry is checked
- the new password must pass the password policy
- the new password is BCrypt-hashed and stored
- the reset token and expiry are cleared

This is important because reset tokens should be one-time credentials, not reusable links.

### Authenticated password change

There is a separate flow for already-authenticated users changing their password from inside the app:

- the current password must match
- the new password must pass the password policy
- the updated password is re-hashed with BCrypt
- a confirmation email is sent

That confirmation email is a detection control. It helps the real user notice unauthorized password changes.

### Generic login errors

`GlobalExceptionHandler` returns a generic `401` message for bad credentials:

> "Invalid email or password!"

This avoids telling attackers whether they guessed a real account name correctly but the wrong password, or whether the account does not exist at all.

---

## 8. OAuth2 and Social Login Strategy

### Supported providers

The current provisioning service supports:

- Google
- GitHub
- Apple
- LinkedIn

### Why OAuth2 still uses local users

Even when a user signs in through an external provider, the application still creates or updates a local `User` record. That gives the app a consistent internal identity model for:

- roles
- dashboards
- admin features
- token issuance
- auditability

So the provider authenticates the user, but the application still controls its own authorization and domain data.

### How OAuth2 users are provisioned

`OAuth2UserProvisioningService`:

- normalizes the provider name
- extracts a provider user ID
- tries to find an existing user linked to that provider identity
- otherwise falls back to email matching
- creates a local user if needed
- marks the user as enabled
- assigns `ROLE_USER`

If a new OAuth user is created, the app still stores a password field, but it is filled with a random encoded UUID value. That is not meant for user login. It simply preserves a consistent local schema that expects a non-null password column.

### Provider-specific fallback logic

The provisioning flow contains some careful defensive logic:

- GitHub may not always return a primary email, so the app can derive a fallback noreply email
- if a provider does not return email at all, the app can derive a provider-scoped placeholder email

That is not just convenience logic. It avoids breaking identity creation on providers with inconsistent profile shapes.

### OAuth2 token issuance model

After OAuth2 login succeeds:

1. Spring Security completes the provider handshake
2. the app loads or creates the local user
3. the app issues its own access token and refresh token pair
4. the refresh token is set as an `HttpOnly` cookie
5. the browser is redirected to `/oauth2/callback` on the frontend
6. the frontend performs a refresh exchange to bootstrap its normal auth state

This is a very good design choice because the application does **not** place its own bearer token in a URL query string during the redirect. URLs are a bad place for secrets because they leak more easily into logs, browser history, and referer headers.

### Why OAuth2 still fits the rest of the architecture

Social login is not treated as a completely separate auth system. Instead, it feeds into the exact same application token model used by password login:

- same JWT access tokens
- same refresh cookie approach
- same authorization model
- same frontend auth bootstrap

That keeps the frontend simpler and reduces the chance of divergent security behavior between "local login users" and "Google login users."

---

## 9. Error Handling and Safe Failure Behavior

`GlobalExceptionHandler` turns domain exceptions into consistent API responses and prevents low-level exceptions from leaking raw stack traces to clients.

Important behaviors include:

- validation failures become `400`
- bad credentials become a generic `401`
- invalid or expired tokens become `401`
- duplicate registration becomes `409`
- account lockouts become `423 Locked`
- rate-limit violations become `429 Too Many Requests`
- unexpected exceptions become a safe `500` with a generic message

This improves security in two ways:

1. it reduces information leakage
2. it gives the frontend predictable failure semantics

Predictability is underrated in security. If errors are inconsistent, developers work around them in inconsistent ways, and security guarantees start eroding at the edges.

---

## 10. Key Tradeoffs and Residual Risks

No real security design is perfect. The current implementation makes several deliberate tradeoffs:

### 1. Verified is not the same as authenticated

Pending users can still log in and receive tokens. This is convenient for verification UX, but any verified-only business rule must be enforced explicitly at the application layer.

### 2. Role changes are not instantly reflected in existing access tokens

Because roles are embedded in JWT claims, already-issued access tokens remain valid until expiry or refresh.

### 3. Refresh tokens are simpler to revoke, but sessions are effectively single-device

One stored refresh token hash per user makes revocation simple, but multi-device refresh behavior is limited.

### 4. Redis outages reduce abuse protection

The system fails open for rate limiting to preserve availability. During Redis downtime, login and recovery endpoints remain available but less protected.

### 5. Some auth-related profile state still lives in browser storage

The access token does not live in `localStorage`, which is good. But the user profile still does, so any successful XSS can still read identity metadata even though it cannot directly read the refresh cookie.

### 6. CSRF protection depends on architectural discipline

Because framework CSRF protection is disabled, future protected endpoints should continue to rely on bearer authorization rather than silently introducing new cookie-authenticated state-changing behavior without additional safeguards.

These are not necessarily flaws. They are boundaries the team should understand so future changes do not accidentally invalidate the assumptions this design relies on.

---

## 11. The Dual-Token System in Plain English

If you want the shortest possible mental model, it is this:

1. The access token is the fast, short-lived token used on normal API calls.
2. The refresh token is the stronger-protected, longer-lived token used only to get a new access token.
3. The access token is kept in memory so it disappears on reload instead of lingering in browser storage.
4. The refresh token is stored in an `HttpOnly` cookie so JavaScript cannot read it directly.
5. The backend stores only hashed refresh tokens, not raw ones.
6. Every refresh rotates the stored token, which makes replay harder.
7. JWT role claims let the backend authorize requests without a database lookup on every call.
8. Redis slows brute-force and spam attacks before they overwhelm the core database.
9. Generic error messages stop the app from confirming whether an email address is registered.
10. The whole design is built around limiting blast radius: if one layer weakens, the others should still matter.

---

## 12. Final Takeaway

The most important thing to understand about this project is that its security model is not just "JWT plus BCrypt." It is a coordinated set of decisions:

- bearer tokens for normal API access
- cookie-isolated refresh tokens for session continuity
- one-way hashing for all recovery-style secrets
- explicit CORS and browser hardening headers
- Redis-backed abuse controls
- fail-closed authorization defaults
- carefully chosen generic error messages

That combination is what gives the system its real security posture. Each individual control helps, but the design only makes sense when you see how the parts reinforce each other.
