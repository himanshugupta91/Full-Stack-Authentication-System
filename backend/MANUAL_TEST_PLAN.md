# Backend Manual Testing Plan (Step by Step)

This plan is for the current backend implementation under `backend/src/main/java/com/auth` using layered architecture.

## 1. Goal

Validate all major backend flows manually:

1. Authentication and session lifecycle
2. OTP verification and password reset
3. Role-based authorization (`USER`, `ADMIN`)
4. Refresh-token cookie flow
5. Admin pagination/filter/search
6. Abuse protection (rate limit + lockouts)
7. Optional OAuth2 login

---

## 2. Test Environment

## 2.1 Required tools

1. Terminal
2. `curl`
3. Optional: Postman
4. Optional: `jq` for JSON parsing

## 2.2 Start dependencies and app

1. Start infra:

```bash
cd backend
docker compose up -d postgres redis
```

2. Run backend:

```bash
cd backend
mvn spring-boot:run
```

3. Confirm backend is listening on `http://localhost:8080`.

## 2.3 Test variables

Run once in terminal:

```bash
BASE="http://localhost:8080"
API="$BASE/api"
COOKIE_JAR="./cookies.txt"
rm -f "$COOKIE_JAR"
```

Use unique test emails each run (because OTP/reset are tied to email). Example:

1. `user1+<timestamp>@example.com`
2. `user2+<timestamp>@example.com`

Password samples:

1. Valid: `StrongPass@1234`
2. Valid2: `NewStrongPass@1234`
3. Invalid: `weakpass`

---

## 3. Phase A: Startup + Baseline Checks

## Step A1: Admin seed login check

```bash
curl -i -c "$COOKIE_JAR" -X POST "$API/auth/login" \
  -H 'Content-Type: application/json' \
  -d '{"email":"admin@admin.com","password":"admin123"}'
```

Expected:

1. HTTP `200`
2. Response body has `accessToken`
3. Roles include `ROLE_ADMIN`
4. `Set-Cookie` contains refresh token

## Step A2: Unauthorized profile check

```bash
curl -i "$API/user/profile"
```

Expected:

1. HTTP `401`

---

## 4. Phase B: Registration + OTP Flow

Pick a new email, example `USER_EMAIL`.

```bash
USER_EMAIL="user1.$(date +%s)@example.com"
USER_PASS="StrongPass@1234"
```

## Step B1: Register new user

```bash
curl -i -X POST "$API/auth/register" \
  -H 'Content-Type: application/json' \
  -d "{\"name\":\"Test User\",\"email\":\"$USER_EMAIL\",\"password\":\"$USER_PASS\"}"
```

Expected:

1. HTTP `200`
2. Message says registration successful and OTP sent

## Step B2: Register duplicate email

Run Step B1 again with same email.

Expected:

1. HTTP `409`
2. Message indicates email already registered

## Step B3: Register with weak password

```bash
curl -i -X POST "$API/auth/register" \
  -H 'Content-Type: application/json' \
  -d "{\"name\":\"Weak User\",\"email\":\"weak.$(date +%s)@example.com\",\"password\":\"weakpass\"}"
```

Expected:

1. HTTP `400`
2. Message from password policy/validation

## Step B4: Verify OTP with wrong code

```bash
curl -i -X POST "$API/auth/verify-otp" \
  -H 'Content-Type: application/json' \
  -d "{\"email\":\"$USER_EMAIL\",\"otp\":\"000000\"}"
```

Expected:

1. HTTP `401` (invalid OTP)

## Step B5: Resend OTP first time

```bash
curl -i -X POST "$API/auth/resend-otp?email=$USER_EMAIL"
```

Expected:

1. HTTP `200`

## Step B6: Resend OTP immediately (cooldown test)

Run Step B5 again immediately.

Expected:

1. HTTP `429`
2. `Retry-After` header present

## Step B7: Verify OTP with real OTP from email inbox

1. Open mailbox for `USER_EMAIL`.
2. Take latest OTP.
3. Execute:

```bash
REAL_OTP="<otp_from_email>"
curl -i -X POST "$API/auth/verify-otp" \
  -H 'Content-Type: application/json' \
  -d "{\"email\":\"$USER_EMAIL\",\"otp\":\"$REAL_OTP\"}"
```

Expected:

1. HTTP `200`
2. Message says email verified

---

## 5. Phase C: Login + User Endpoints

## Step C1: Login with wrong password

```bash
curl -i -c "$COOKIE_JAR" -X POST "$API/auth/login" \
  -H 'Content-Type: application/json' \
  -d "{\"email\":\"$USER_EMAIL\",\"password\":\"Wrong@12345\"}"
```

Expected:

1. HTTP `401`

## Step C2: Login with correct password

```bash
LOGIN_RESPONSE=$(curl -s -c "$COOKIE_JAR" -X POST "$API/auth/login" \
  -H 'Content-Type: application/json' \
  -d "{\"email\":\"$USER_EMAIL\",\"password\":\"$USER_PASS\"}")

echo "$LOGIN_RESPONSE"
```

Expected:

1. HTTP `200` (if using `-i`)
2. JSON has `accessToken`
3. Cookie jar file gets refresh token

Capture token:

```bash
ACCESS_TOKEN=$(echo "$LOGIN_RESPONSE" | jq -r '.accessToken')
echo "$ACCESS_TOKEN"
```

## Step C3: User profile with token

```bash
curl -i "$API/user/profile" -H "Authorization: Bearer $ACCESS_TOKEN"
```

Expected:

1. HTTP `200`
2. Correct user email/roles

## Step C4: User dashboard with token

```bash
curl -i "$API/user/dashboard" -H "Authorization: Bearer $ACCESS_TOKEN"
```

Expected:

1. HTTP `200`

## Step C5: Change password with wrong current password

```bash
curl -i -X POST "$API/user/change-password" \
  -H "Authorization: Bearer $ACCESS_TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{"currentPassword":"WrongCurrent@123","newPassword":"NewStrongPass@1234"}'
```

Expected:

1. HTTP `401`

## Step C6: Change password with weak new password

```bash
curl -i -X POST "$API/user/change-password" \
  -H "Authorization: Bearer $ACCESS_TOKEN" \
  -H 'Content-Type: application/json' \
  -d "{\"currentPassword\":\"$USER_PASS\",\"newPassword\":\"weak\"}"
```

Expected:

1. HTTP `400`

## Step C7: Change password successfully

```bash
NEW_USER_PASS="NewStrongPass@1234"
curl -i -X POST "$API/user/change-password" \
  -H "Authorization: Bearer $ACCESS_TOKEN" \
  -H 'Content-Type: application/json' \
  -d "{\"currentPassword\":\"$USER_PASS\",\"newPassword\":\"$NEW_USER_PASS\"}"
```

Expected:

1. HTTP `200`

## Step C8: Verify old password fails and new password works

1. Login with old password => expect `401`
2. Login with new password => expect `200`

---

## 6. Phase D: Refresh + Logout Cookie Flow

Login first to refresh cookie jar, then:

## Step D1: Refresh with cookie jar

```bash
curl -i -b "$COOKIE_JAR" -c "$COOKIE_JAR" -X POST "$API/auth/refresh"
```

Expected:

1. HTTP `200`
2. New access token in body
3. Rotated refresh cookie (`Set-Cookie`)

## Step D2: Refresh without cookie and without body

```bash
curl -i -X POST "$API/auth/refresh"
```

Expected:

1. HTTP `401`
2. Message: refresh token required

## Step D3: Logout

```bash
curl -i -b "$COOKIE_JAR" -c "$COOKIE_JAR" -X POST "$API/auth/logout"
```

Expected:

1. HTTP `200`
2. Cookie cleared (Max-Age=0 or empty value)

## Step D4: Refresh after logout

```bash
curl -i -b "$COOKIE_JAR" -X POST "$API/auth/refresh"
```

Expected:

1. HTTP `401` (invalid/expired/missing token)

---

## 7. Phase E: Forgot/Reset Password

## Step E1: Reset request for existing email

```bash
curl -i -X POST "$API/auth/reset-password" \
  -H 'Content-Type: application/json' \
  -d "{\"email\":\"$USER_EMAIL\"}"
```

Expected:

1. HTTP `200`
2. Generic message (does not reveal account existence)

## Step E2: Reset request for non-existing email

```bash
curl -i -X POST "$API/auth/reset-password" \
  -H 'Content-Type: application/json' \
  -d '{"email":"doesnotexist@example.com"}'
```

Expected:

1. HTTP `200`
2. Same generic message as Step E1

## Step E3: Update password with invalid token

```bash
curl -i -X POST "$API/auth/update-password" \
  -H 'Content-Type: application/json' \
  -d '{"token":"invalid-token","newPassword":"ResetPass@1234"}'
```

Expected:

1. HTTP `401`

## Step E4: Update password with valid token from email

1. Get reset token from email message.
2. Execute:

```bash
RESET_TOKEN="<token_from_email>"
RESET_PASS="ResetPass@1234"

curl -i -X POST "$API/auth/update-password" \
  -H 'Content-Type: application/json' \
  -d "{\"token\":\"$RESET_TOKEN\",\"newPassword\":\"$RESET_PASS\"}"
```

Expected:

1. HTTP `200`

## Step E5: Login with reset password

Expected:

1. Old password fails
2. New reset password succeeds

---

## 8. Phase F: Admin Authorization + Pagination/Filters

## Step F1: Login as admin and capture token

```bash
ADMIN_LOGIN=$(curl -s -c "$COOKIE_JAR" -X POST "$API/auth/login" \
  -H 'Content-Type: application/json' \
  -d '{"email":"admin@admin.com","password":"admin123"}')

ADMIN_TOKEN=$(echo "$ADMIN_LOGIN" | jq -r '.accessToken')
echo "$ADMIN_TOKEN"
```

## Step F2: Admin dashboard as admin

```bash
curl -i "$API/admin/dashboard" -H "Authorization: Bearer $ADMIN_TOKEN"
```

Expected:

1. HTTP `200`
2. Includes total and active users

## Step F3: Admin dashboard as normal user

Use normal user token from earlier.

Expected:

1. HTTP `403`

## Step F4: Users list default

```bash
curl -i "$API/admin/users" -H "Authorization: Bearer $ADMIN_TOKEN"
```

Expected:

1. HTTP `200`
2. Spring Page shape: `content`, `totalElements`, `totalPages`, `number`, `size`

## Step F5: Search filter

```bash
curl -i "$API/admin/users?search=admin" -H "Authorization: Bearer $ADMIN_TOKEN"
```

Expected:

1. HTTP `200`
2. Results include matching `name` or `email`

## Step F6: Status filter

```bash
curl -i "$API/admin/users?enabled=true" -H "Authorization: Bearer $ADMIN_TOKEN"
```

Expected:

1. HTTP `200`
2. Returned users are enabled

## Step F7: Role filter

```bash
curl -i "$API/admin/users?role=ADMIN" -H "Authorization: Bearer $ADMIN_TOKEN"
```

Expected:

1. HTTP `200`
2. Users contain admin role

## Step F8: Pagination + size cap

```bash
curl -i "$API/admin/users?page=0&size=500" -H "Authorization: Bearer $ADMIN_TOKEN"
```

Expected:

1. HTTP `200`
2. Effective size should be max 100 (as normalized in controller)

## Step F9: Sort validation

```bash
curl -i "$API/admin/users?sortBy=createdAt&sortDir=desc" -H "Authorization: Bearer $ADMIN_TOKEN"
```

Expected:

1. HTTP `200`
2. Sorted response

Try invalid sort key:

```bash
curl -i "$API/admin/users?sortBy=invalidField" -H "Authorization: Bearer $ADMIN_TOKEN"
```

Expected:

1. HTTP `200`
2. Fallback sort applied

---

## 9. Phase G: Abuse Protection and Rate Limiting

Important expected statuses from `GlobalExceptionHandler`:

1. `429 Too Many Requests` for rate limits with `Retry-After`
2. `423 Locked` for account/OTP lock with `Retry-After`

## Step G1: Login IP limit test

Send multiple bad login requests quickly from same client.

Expected:

1. First requests: `401`
2. Beyond configured threshold: `429`

## Step G2: Login brute-force lock test (per account)

Keep wrong password attempts for same email until lock threshold.

Expected:

1. `423 Locked`
2. `Retry-After` header present

## Step G3: OTP verify rate/lock

Call verify OTP repeatedly with wrong OTP.

Expected:

1. mix of `401`
2. then `429` and/or `423`

## Step G4: Reset-password spam

Call `/auth/reset-password` repeatedly for same email.

Expected:

1. eventually `429`

## Step G5: Resend OTP spam

Call `/auth/resend-otp` repeatedly.

Expected:

1. cooldown-based `429`
2. window-limit `429`

---

## 10. Phase H: CORS + Cookie Attributes

## Step H1: CORS preflight

```bash
curl -i -X OPTIONS "$API/auth/register" \
  -H 'Origin: http://localhost:5173' \
  -H 'Access-Control-Request-Method: POST'
```

Expected:

1. HTTP `200`
2. `Access-Control-Allow-Origin: http://localhost:5173`
3. `Access-Control-Allow-Credentials: true`

## Step H2: Refresh cookie attributes on login

Check response headers on login.

Expected:

1. Cookie name matches config (`refreshToken` default)
2. Path is `/api/auth`
3. `SameSite` matches config (`Lax` default)
4. `HttpOnly` present

---

## 11. Optional OAuth2 Manual Tests

If provider credentials are configured:

1. Open `http://localhost:8080/oauth2/authorization/google` (or github/apple/linkedin).
2. Complete provider login.
3. Expect redirect to frontend `/oauth2/callback`.
4. Frontend should call `/api/auth/refresh` and establish session.

Expected backend behavior:

1. Local user created or updated
2. Refresh cookie set
3. Access token available after callback refresh

---

## 12. Pass/Fail Checklist (Quick Summary)

Mark each as PASS/FAIL:

1. Startup and admin seed login
2. Register/duplicate/weak password
3. OTP verify + resend cooldown
4. Login success/failure
5. User endpoints authorization
6. Change-password flow
7. Refresh/logout cookie flow
8. Reset/update-password flow
9. Admin role checks
10. Admin pagination/filter/search/sort
11. Rate-limit + lockout behavior
12. CORS and cookie attributes
13. OAuth2 callback flow (if configured)

---
