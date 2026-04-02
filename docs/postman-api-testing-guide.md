# 🚀 Postman API Testing & Debugging Guide

> **Full-Stack Authentication System — Complete API Reference**
>
> This guide covers how to test every API endpoint using Postman, including request setup,
> expected responses, authentication flow, and debugging tips.

---

## 📋 Table of Contents

1. [Prerequisites & Setup](#1-prerequisites--setup)
2. [Environment Configuration](#2-environment-configuration)
3. [Collection Structure](#3-collection-structure)
4. [Auth APIs — Testing Guide](#4-auth-apis--testing-guide)
   - [Register](#41-register-a-new-user)
   - [Verify OTP](#42-verify-otp)
   - [Resend OTP](#43-resend-otp)
   - [Login](#44-login)
   - [Refresh Token](#45-refresh-token)
   - [Logout](#46-logout)
   - [Reset Password (Forgot Password)](#47-reset-password-forgot-password)
   - [Update Password (Set New Password)](#48-update-password-set-new-password)
5. [User APIs — Testing Guide](#5-user-apis--testing-guide)
   - [Get Dashboard](#51-get-user-dashboard)
   - [Get Profile](#52-get-user-profile)
   - [Change Password](#53-change-password)
6. [Admin APIs — Testing Guide](#6-admin-apis--testing-guide)
   - [Get Admin Dashboard](#61-get-admin-dashboard)
   - [Get All Users](#62-get-all-users-with-filters)
7. [OAuth2 Social Login](#7-oauth2-social-login)
8. [Automating Token Injection](#8-automating-token-injection-with-pre-request-scripts)
9. [Debugging Guide](#9-debugging-guide)
   - [Common HTTP Errors](#91-common-http-errors--fixes)
   - [JWT Token Issues](#92-jwt-token-debugging)
   - [Cookie / Refresh Token Issues](#93-cookie--refresh-token-issues)
   - [CORS Issues](#94-cors-issues)
   - [Validation Errors](#95-validation-errors)
   - [Server-Side Debugging](#96-server-side-debugging)
10. [Testing Flows (End-to-End)](#10-testing-flows-end-to-end)

---

## 1. Prerequisites & Setup

### Requirements
- [Postman](https://www.postman.com/downloads/) — Desktop app recommended (v10+)
- Backend running locally on `http://localhost:8080` (or your deployed URL)
- A valid email account for OTP testing (configured in `.env`)

### Start the Backend
Make sure the backend is running before testing:

```bash
# From the project root
cd backend
./mvnw spring-boot:run
```

Or with Docker:

```bash
docker-compose up -d
```

---

## 2. Environment Configuration

Create a **Postman Environment** to avoid hardcoding values.

### Step-by-step: Create Environment

1. In Postman, click **Environments** (left sidebar) → **"+"** (New Environment)
2. Name it: `Full-Stack Auth - Local`
3. Add the following variables:

| Variable Name     | Initial Value                   | Current Value                   | Notes                          |
|-------------------|---------------------------------|---------------------------------|--------------------------------|
| `BASE_URL`        | `http://localhost:8080`         | `http://localhost:8080`         | Change for prod                |
| `ACCESS_TOKEN`    | *(leave empty)*                 | *(leave empty)*                 | Auto-filled after login        |
| `REFRESH_TOKEN`   | *(leave empty)*                 | *(leave empty)*                 | Auto-filled after login        |
| `USER_EMAIL`      | `testuser@example.com`          | `testuser@example.com`          | Your test email                |
| `USER_PASSWORD`   | `TestPass123!`                  | `TestPass123!`                  | Your test password             |
| `ADMIN_EMAIL`     | `admin@example.com`             | `admin@example.com`             | Seeded admin email             |
| `ADMIN_PASSWORD`  | `AdminPass123!`                 | `AdminPass123!`                 | Seeded admin password          |
| `OTP_CODE`        | *(leave empty)*                 | *(leave empty)*                 | Fill after receiving OTP email |
| `RESET_TOKEN`     | *(leave empty)*                 | *(leave empty)*                 | Fill from reset email link     |

4. Click **Save**
5. Select the environment from the top-right dropdown

---

## 3. Collection Structure

Create a **Postman Collection** called `Full-Stack Auth System` with folders:

```
📁 Full-Stack Auth System
├── 📂 Auth
│   ├── Register
│   ├── Verify OTP
│   ├── Resend OTP
│   ├── Login
│   ├── Refresh Token
│   ├── Logout
│   ├── Reset Password (Forgot)
│   └── Update Password (New)
├── 📂 User (Protected)
│   ├── Get Dashboard
│   ├── Get Profile
│   └── Change Password
└── 📂 Admin (Protected - Admin Only)
    ├── Get Admin Dashboard
    └── Get All Users
```

### Collection-level Authorization (Recommended)

Set auth once at the **collection level** so all child requests inherit it:

1. Click the collection → **Authorization** tab
2. Type: `Bearer Token`
3. Token: `{{ACCESS_TOKEN}}`
4. All requests inside will automatically use this token unless overridden

---

## 4. Auth APIs — Testing Guide

**Base URL:** `{{BASE_URL}}/api/v1/auth`

> ⚠️ All auth endpoints are **public** (no token required).

---

### 4.1 Register a New User

| Field   | Value                             |
|---------|-----------------------------------|
| Method  | `POST`                            |
| URL     | `{{BASE_URL}}/api/v1/auth/register` |
| Auth    | None                              |

**Headers:**
```
Content-Type: application/json
```

**Request Body (raw JSON):**
```json
{
  "name": "John Doe",
  "email": "{{USER_EMAIL}}",
  "password": "TestPass123!"
}
```

**Expected Success Response — `200 OK`:**
```json
{
  "success": true,
  "data": {
    "message": "Registration successful. Please verify your email.",
    "success": true
  }
}
```

**What Happens Next:**
- A 6-digit OTP code is sent to the provided email
- The user account is created but **not yet active** until OTP is verified

**Common Errors:**

| Status | Cause                          | Fix                             |
|--------|--------------------------------|---------------------------------|
| `400`  | Missing/invalid fields         | Check all required fields       |
| `409`  | Email already registered       | Use a different email           |
| `400`  | Password too short (< 6 chars) | Use a stronger password         |

---

### 4.2 Verify OTP

| Field   | Value                              |
|---------|------------------------------------|
| Method  | `POST`                             |
| URL     | `{{BASE_URL}}/api/v1/auth/verify-otp` |
| Auth    | None                               |

**Headers:**
```
Content-Type: application/json
```

**Request Body (raw JSON):**
```json
{
  "email": "{{USER_EMAIL}}",
  "otp": "{{OTP_CODE}}"
}
```

> 💡 Check your email inbox for the OTP code. Set `{{OTP_CODE}}` in your environment variable before sending.

**Expected Success Response — `200 OK`:**
```json
{
  "success": true,
  "data": {
    "message": "Email verified successfully.",
    "success": true
  }
}
```

**Common Errors:**

| Status | Cause                          | Fix                               |
|--------|--------------------------------|-----------------------------------|
| `400`  | Invalid or expired OTP         | Request a new OTP via Resend OTP  |
| `400`  | OTP already used               | Request a new OTP                 |
| `404`  | Email not found                | Register first                    |

---

### 4.3 Resend OTP

| Field   | Value                                        |
|---------|----------------------------------------------|
| Method  | `POST`                                       |
| URL     | `{{BASE_URL}}/api/v1/auth/resend-otp?email={{USER_EMAIL}}` |
| Auth    | None                                         |

**Query Params:**

| Key     | Value              |
|---------|--------------------|
| `email` | `{{USER_EMAIL}}`   |

> No request body needed. The email is passed as a query parameter.

**Expected Success Response — `200 OK`:**
```json
{
  "success": true,
  "data": {
    "message": "OTP resent successfully.",
    "success": true
  }
}
```

---

### 4.4 Login

| Field   | Value                            |
|---------|----------------------------------|
| Method  | `POST`                           |
| URL     | `{{BASE_URL}}/api/v1/auth/login` |
| Auth    | None                             |

**Headers:**
```
Content-Type: application/json
```

**Request Body (raw JSON):**
```json
{
  "email": "{{USER_EMAIL}}",
  "password": "{{USER_PASSWORD}}"
}
```

**Expected Success Response — `200 OK`:**
```json
{
  "success": true,
  "data": {
    "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "tokenType": "Bearer",
    "expiresIn": 900000
  }
}
```

> 🍪 **Important:** The **refresh token** is returned as an `HttpOnly` cookie named `refresh_token`. Postman automatically stores cookies — check the **Cookies** tab after login.

**Post-Request Script — Auto-save the Access Token:**

Go to the **Tests** tab of this request and add:

```javascript
// Auto-save access token to environment
const response = pm.response.json();
if (response.success && response.data && response.data.accessToken) {
    pm.environment.set("ACCESS_TOKEN", response.data.accessToken);
    console.log("✅ Access token saved to environment");
} else {
    console.log("❌ Login failed:", JSON.stringify(response));
}
```

**Common Errors:**

| Status | Cause                        | Fix                                         |
|--------|------------------------------|---------------------------------------------|
| `401`  | Wrong credentials            | Check email/password                        |
| `403`  | Email not verified           | Complete OTP verification first             |
| `403`  | Account disabled             | Contact admin                               |
| `400`  | Missing email or password    | Verify request body fields                  |

---

### 4.5 Refresh Token

| Field   | Value                               |
|---------|-------------------------------------|
| Method  | `POST`                              |
| URL     | `{{BASE_URL}}/api/v1/auth/refresh`  |
| Auth    | None                                |

**Option A — Via Cookie (automatic if logged in via Postman):**

No body needed. Postman automatically sends the stored `refresh_token` cookie.

**Option B — Via Request Body (for non-browser clients):**

```json
{
  "refreshToken": "{{REFRESH_TOKEN}}"
}
```

**Expected Success Response — `200 OK`:**
```json
{
  "success": true,
  "data": {
    "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "tokenType": "Bearer",
    "expiresIn": 900000
  }
}
```

**Post-Request Script — Auto-update Access Token:**

```javascript
const response = pm.response.json();
if (response.success && response.data && response.data.accessToken) {
    pm.environment.set("ACCESS_TOKEN", response.data.accessToken);
    console.log("✅ Access token refreshed");
}
```

**Common Errors:**

| Status | Cause                        | Fix                            |
|--------|------------------------------|--------------------------------|
| `401`  | Refresh token expired        | Login again                    |
| `400`  | No refresh token provided    | Ensure cookie is present or send body |

---

### 4.6 Logout

| Field   | Value                              |
|---------|------------------------------------|
| Method  | `POST`                             |
| URL     | `{{BASE_URL}}/api/v1/auth/logout`  |
| Auth    | None                               |

No body required. The refresh token cookie will be automatically invalidated.

**Expected Success Response — `200 OK`:**
```json
{
  "success": true,
  "data": {
    "message": "Logged out successfully.",
    "success": true
  }
}
```

> The server also clears the `refresh_token` cookie in the response headers.

---

### 4.7 Reset Password (Forgot Password)

| Field   | Value                                    |
|---------|------------------------------------------|
| Method  | `POST`                                   |
| URL     | `{{BASE_URL}}/api/v1/auth/reset-password` |
| Auth    | None                                     |

**Headers:**
```
Content-Type: application/json
```

**Request Body (raw JSON):**
```json
{
  "email": "{{USER_EMAIL}}"
}
```

**Expected Success Response — `200 OK`:**
```json
{
  "success": true,
  "data": {
    "message": "If the email is registered, a reset link has been sent.",
    "success": true
  }
}
```

> 📧 Check your email for the reset link. Copy the **token** from the link URL and set it as `{{RESET_TOKEN}}` in your environment.
>
> The link format is usually: `http://localhost:5173/reset-password?token=<YOUR_TOKEN>`

---

### 4.8 Update Password (Set New Password)

| Field   | Value                                      |
|---------|--------------------------------------------|
| Method  | `POST`                                     |
| URL     | `{{BASE_URL}}/api/v1/auth/update-password` |
| Auth    | None                                       |

**Headers:**
```
Content-Type: application/json
```

**Request Body (raw JSON):**
```json
{
  "token": "{{RESET_TOKEN}}",
  "newPassword": "NewSecurePass456!"
}
```

**Expected Success Response — `200 OK`:**
```json
{
  "success": true,
  "data": {
    "message": "Password updated successfully.",
    "success": true
  }
}
```

**Common Errors:**

| Status | Cause                          | Fix                               |
|--------|--------------------------------|-----------------------------------|
| `400`  | Token expired or invalid       | Request a new reset link          |
| `400`  | Password too short (< 6 chars) | Use a longer password             |
| `400`  | Token already used             | Request a new reset link          |

---

## 5. User APIs — Testing Guide

**Base URL:** `{{BASE_URL}}/api/v1/user`

> 🔒 All User endpoints require a **valid JWT access token**.

**Authorization header (auto-applied if using collection-level auth):**
```
Authorization: Bearer {{ACCESS_TOKEN}}
```

---

### 5.1 Get User Dashboard

| Field   | Value                                    |
|---------|------------------------------------------|
| Method  | `GET`                                    |
| URL     | `{{BASE_URL}}/api/v1/user/dashboard`     |
| Auth    | Bearer Token (`{{ACCESS_TOKEN}}`)        |

No request body needed.

**Expected Success Response — `200 OK`:**
```json
{
  "success": true,
  "data": {
    "email": "john.doe@example.com",
    "name": "John Doe",
    "roles": ["ROLE_USER"],
    "lastLogin": "2026-04-01T10:00:00Z",
    "accountAge": "5 days"
  }
}
```

**Common Errors:**

| Status | Cause                  | Fix                                  |
|--------|------------------------|--------------------------------------|
| `401`  | Missing/invalid token  | Login and refresh `{{ACCESS_TOKEN}}` |
| `403`  | Insufficient role      | Ensure user has USER or ADMIN role   |

---

### 5.2 Get User Profile

| Field   | Value                                    |
|---------|------------------------------------------|
| Method  | `GET`                                    |
| URL     | `{{BASE_URL}}/api/v1/user/profile`       |
| Auth    | Bearer Token (`{{ACCESS_TOKEN}}`)        |

No request body needed.

**Expected Success Response — `200 OK`:**
```json
{
  "success": true,
  "data": {
    "id": 1,
    "name": "John Doe",
    "email": "john.doe@example.com",
    "enabled": true,
    "roles": ["ROLE_USER"],
    "provider": "LOCAL",
    "createdAt": "2026-03-27T08:00:00Z"
  }
}
```

---

### 5.3 Change Password

| Field   | Value                                         |
|---------|-----------------------------------------------|
| Method  | `POST`                                        |
| URL     | `{{BASE_URL}}/api/v1/user/change-password`    |
| Auth    | Bearer Token (`{{ACCESS_TOKEN}}`)             |

**Headers:**
```
Content-Type: application/json
```

**Request Body (raw JSON):**
```json
{
  "currentPassword": "TestPass123!",
  "newPassword": "NewSecurePass456!"
}
```

**Expected Success Response — `200 OK`:**
```json
{
  "success": true,
  "data": {
    "message": "Password changed successfully.",
    "success": true
  }
}
```

**Common Errors:**

| Status | Cause                            | Fix                              |
|--------|----------------------------------|----------------------------------|
| `400`  | Current password incorrect       | Verify your existing password    |
| `400`  | New password too short           | Use at least 6 characters        |
| `401`  | Not authenticated                | Login and update access token    |

---

## 6. Admin APIs — Testing Guide

**Base URL:** `{{BASE_URL}}/api/v1/admin`

> 🛡️ All Admin endpoints require a **JWT access token with `ROLE_ADMIN`**.
> Login with an admin account to get a valid token.

---

### 6.1 Get Admin Dashboard

| Field   | Value                                     |
|---------|-------------------------------------------|
| Method  | `GET`                                     |
| URL     | `{{BASE_URL}}/api/v1/admin/dashboard`     |
| Auth    | Bearer Token (`{{ACCESS_TOKEN}}` — Admin) |

No request body needed.

**Expected Success Response — `200 OK`:**
```json
{
  "success": true,
  "data": {
    "adminName": "Super Admin",
    "adminEmail": "admin@example.com",
    "totalUsers": 42,
    "activeUsers": 38,
    "totalAdmins": 2
  }
}
```

**Common Errors:**

| Status | Cause              | Fix                                  |
|--------|--------------------|--------------------------------------|
| `401`  | No/expired token   | Login as admin and refresh token     |
| `403`  | Not admin role     | Use an account with `ROLE_ADMIN`     |

---

### 6.2 Get All Users (with Filters)

| Field   | Value                                           |
|---------|-------------------------------------------------|
| Method  | `GET`                                           |
| URL     | `{{BASE_URL}}/api/v1/admin/users`               |
| Auth    | Bearer Token (`{{ACCESS_TOKEN}}` — Admin)       |

**Query Parameters (all optional):**

| Parameter | Type      | Default      | Description                               | Example           |
|-----------|-----------|--------------|-------------------------------------------|-------------------|
| `page`    | `int`     | `0`          | Page number (0-based)                     | `0`               |
| `size`    | `int`     | `20`         | Results per page (max 100)                | `10`              |
| `search`  | `string`  | *(none)*     | Search by name or email                   | `john`            |
| `enabled` | `boolean` | *(none)*     | Filter by account status                  | `true`            |
| `role`    | `string`  | *(none)*     | Filter by role (`USER`, `ADMIN`)          | `USER`            |
| `sortBy`  | `string`  | `createdAt`  | Field to sort by                          | `name`            |
| `sortDir` | `string`  | `desc`       | Sort direction (`asc` or `desc`)          | `asc`             |

**Example URLs:**

```
# Get first page of all users
GET {{BASE_URL}}/api/v1/admin/users?page=0&size=10

# Search users by name
GET {{BASE_URL}}/api/v1/admin/users?search=john&sortBy=name&sortDir=asc

# Get only enabled users with ADMIN role
GET {{BASE_URL}}/api/v1/admin/users?enabled=true&role=ADMIN

# Get second page of users sorted by name ascending
GET {{BASE_URL}}/api/v1/admin/users?page=1&size=5&sortBy=name&sortDir=asc
```

**Expected Success Response — `200 OK`:**
```json
{
  "success": true,
  "data": {
    "content": [
      {
        "id": 1,
        "name": "John Doe",
        "email": "john.doe@example.com",
        "enabled": true,
        "roles": ["ROLE_USER"],
        "provider": "LOCAL",
        "createdAt": "2026-03-27T08:00:00Z"
      }
    ],
    "totalElements": 42,
    "totalPages": 5,
    "number": 0,
    "size": 10
  }
}
```

---

## 7. OAuth2 Social Login

OAuth2 flows (Google, GitHub, LinkedIn) use browser redirects and **cannot be directly tested with Postman in the usual way**. Use the following approach:

### Testing via Browser + Postman

1. Open your browser and navigate to:
   ```
   http://localhost:8080/oauth2/authorization/google
   ```
2. Complete the Google login in the browser
3. After success, the backend redirects to the frontend with an access token in the URL fragment or as a cookie
4. Copy the `accessToken` from the URL or browser cookies
5. Set `{{ACCESS_TOKEN}}` in Postman and test protected endpoints

### Available OAuth2 Providers

| Provider | Authorization URL                                               |
|----------|-----------------------------------------------------------------|
| Google   | `http://localhost:8080/oauth2/authorization/google`            |
| GitHub   | `http://localhost:8080/oauth2/authorization/github`            |
| LinkedIn | `http://localhost:8080/oauth2/authorization/linkedin`          |

---

## 8. Automating Token Injection with Pre-Request Scripts

Add this script at the **Collection level** to auto-refresh the token before any request:

### Collection → Pre-request Script

```javascript
// Auto-refresh access token if it's expired or close to expiring
const accessToken = pm.environment.get("ACCESS_TOKEN");

if (!accessToken) {
    console.log("⚠️ No access token found. Please login first.");
    return;
}

// Decode the JWT to check expiry
try {
    const parts = accessToken.split(".");
    const payload = JSON.parse(atob(parts[1]));
    const expiresAt = payload.exp * 1000; // Convert to ms
    const now = Date.now();
    const bufferMs = 60000; // 1 minute buffer

    if (expiresAt - now < bufferMs) {
        console.log("🔄 Token expiring soon, refreshing...");

        const baseUrl = pm.environment.get("BASE_URL");

        pm.sendRequest({
            url: `${baseUrl}/api/v1/auth/refresh`,
            method: "POST",
            header: { "Content-Type": "application/json" }
        }, (err, response) => {
            if (!err && response.code === 200) {
                const data = response.json();
                if (data.success && data.data.accessToken) {
                    pm.environment.set("ACCESS_TOKEN", data.data.accessToken);
                    console.log("✅ Token refreshed successfully");
                }
            } else {
                console.log("❌ Token refresh failed. Re-login required.");
            }
        });
    }
} catch (e) {
    console.log("⚠️ Could not parse token:", e.message);
}
```

---

## 9. Debugging Guide

### 9.1 Common HTTP Errors & Fixes

| HTTP Status | Name                  | Common Causes                                     | How to Fix                                          |
|-------------|-----------------------|---------------------------------------------------|-----------------------------------------------------|
| `400`       | Bad Request           | Missing required fields, validation failures      | Check request body fields and types                 |
| `401`       | Unauthorized          | Missing/expired/malformed token                   | Login again and update `{{ACCESS_TOKEN}}`           |
| `403`       | Forbidden             | Insufficient role, email not verified             | Check account role / verify email via OTP           |
| `404`       | Not Found             | Wrong URL or resource doesn't exist               | Double-check the endpoint URL                       |
| `409`       | Conflict              | Email already registered                          | Use a different email or login                      |
| `422`       | Unprocessable Entity  | Semantic validation error                         | Check field constraints and formats                 |
| `500`       | Internal Server Error | Backend exception / misconfiguration              | Check backend logs                                  |

---

### 9.2 JWT Token Debugging

**Decode a JWT token instantly:**

1. Go to [https://jwt.io](https://jwt.io)
2. Paste your `{{ACCESS_TOKEN}}` into the **Encoded** field
3. Read the decoded **Payload** to see:
   - `sub` — the user's email
   - `roles` — the user's roles (`ROLE_USER`, `ROLE_ADMIN`)
   - `iat` — issued at (Unix timestamp)
   - `exp` — expiry time (Unix timestamp)

**Check if your token is expired in Postman Console:**

```javascript
// Paste into Postman Console (Ctrl+Alt+C / Cmd+Alt+C)
const token = pm.environment.get("ACCESS_TOKEN");
const payload = JSON.parse(atob(token.split(".")[1]));
const exp = new Date(payload.exp * 1000);
console.log("Token expires:", exp.toLocaleString());
console.log("Expired?", Date.now() > payload.exp * 1000);
```

**Token Expiry Settings (from `.env`):**

| Token           | Default Expiry  | Env Variable             |
|-----------------|-----------------|--------------------------|
| Access Token    | 15 minutes      | `JWT_EXPIRATION=900000`  |
| Refresh Token   | 7 days          | `JWT_REFRESH_EXPIRATION=604800000` |

---

### 9.3 Cookie / Refresh Token Issues

**Problem:** Refresh token not being sent automatically

**Fix:**

1. In Postman, go to **Settings** (gear icon) → **General**
2. Enable **Automatically follow redirects**
3. Ensure **Send cookies** is enabled
4. Check the **Cookies** tab under the request to see if `refresh_token` cookie is stored

**Manually inspect cookies:**

1. In the request view, click the **Cookies** link (next to Send button area)
2. Check for a cookie named `refresh_token` under `localhost`

**Cookie attributes set by the server:**

| Attribute  | Development      | Production  |
|------------|------------------|-------------|
| `HttpOnly` | ✅ Always        | ✅ Always   |
| `Secure`   | ❌ (HTTP OK)     | ✅ (HTTPS)  |
| `Path`     | `/api/v1/auth`   | `/api/v1/auth` |
| `SameSite` | `Lax`            | `Strict`    |

> The cookie is **scoped to `/api/v1/auth`** — it will **only** be sent to auth endpoints, not to `/api/v1/user` or `/api/v1/admin`.

---

### 9.4 CORS Issues

If you see CORS errors in the browser or Postman:

**Check allowed origins in `.env`:**
```
APP_CORS_ALLOWED_ORIGINS=http://localhost:5173,http://localhost:3000
```

**For Postman:**
- Postman does **not** enforce CORS — CORS errors only affect browsers
- If you see CORS in Postman, it's likely a different issue

**For Browser-based testing (e.g., fetch from DevTools):**
- Make sure your frontend URL is listed in `APP_CORS_ALLOWED_ORIGINS`
- Ensure `Content-Type: application/json` header is set on all requests

---

### 9.5 Validation Errors

When you get a `400 Bad Request`, look at the response body:

```json
{
  "success": false,
  "error": {
    "code": "VALIDATION_ERROR",
    "details": {
      "email": "Please provide a valid email",
      "password": "Password must be at least 6 characters"
    }
  }
}
```

**Field validation rules:**

| Field           | Rule                          | Error Message                        |
|-----------------|-------------------------------|--------------------------------------|
| `name`          | Required, not blank           | "Name is required"                   |
| `email`         | Required, valid email format  | "Please provide a valid email"       |
| `password`      | Required, min 6 chars         | "Password must be at least 6 chars"  |
| `otp`           | Required, not blank           | "OTP is required"                    |
| `token`         | Required, not blank           | "Token is required"                  |
| `currentPassword` | Required, not blank         | "Current password is required"       |
| `newPassword`   | Required, min 6 chars         | "New password must be at least 6 chars" |

---

### 9.6 Server-Side Debugging

**View backend logs (local):**

```bash
# If running directly
cd backend
./mvnw spring-boot:run

# Logs will print to terminal. Look for:
# ERROR — for exceptions
# WARN  — for configuration issues
# INFO  — for request traces
```

**View backend logs (Docker):**

```bash
# View live logs
docker-compose logs -f backend

# View last 100 lines
docker-compose logs --tail=100 backend
```

**Enable verbose SQL logging** (add to `application.properties` or `.env`):

```properties
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
logging.level.org.hibernate.SQL=DEBUG
```

**Enable Spring Security debug logs:**

```properties
logging.level.org.springframework.security=DEBUG
```

**View Postman Console** to see request/response details:

1. In Postman, go to **View → Show Postman Console** (or `Ctrl+Alt+C`)
2. All `console.log()` from your scripts and request details appear here

---

## 10. Testing Flows (End-to-End)

### ✅ Flow 1: New User Registration & Login

```
1. POST /api/v1/auth/register       → Get "verify your email" response
2. (Check email for OTP code)       → Set {{OTP_CODE}} in environment
3. POST /api/v1/auth/verify-otp     → Get "email verified" response
4. POST /api/v1/auth/login          → Get accessToken → saved to {{ACCESS_TOKEN}}
5. GET  /api/v1/user/profile        → View profile (authenticated)
6. GET  /api/v1/user/dashboard      → View dashboard data
```

---

### ✅ Flow 2: Admin Login & User Management

```
1. POST /api/v1/auth/login          → Login as admin (use admin credentials)
                                    → Set {{ACCESS_TOKEN}}
2. GET  /api/v1/admin/dashboard     → View admin stats
3. GET  /api/v1/admin/users         → View all users
4. GET  /api/v1/admin/users?search=john&role=USER → Filter users
```

---

### ✅ Flow 3: Forgot Password Reset

```
1. POST /api/v1/auth/reset-password  → Send email: { "email": "{{USER_EMAIL}}" }
2. (Check email for reset link)       → Extract token from URL
3.                                    → Set {{RESET_TOKEN}} in environment
4. POST /api/v1/auth/update-password  → { "token": "{{RESET_TOKEN}}", "newPassword": "..." }
5. POST /api/v1/auth/login            → Login with new password
```

---

### ✅ Flow 4: Token Refresh Cycle

```
1. POST /api/v1/auth/login     → Get accessToken + refresh_token cookie
2. (Wait 15 minutes or manually expire)
3. POST /api/v1/auth/refresh   → Get new accessToken (cookie sent automatically)
4. GET  /api/v1/user/profile   → Access protected resource with new token
5. POST /api/v1/auth/logout    → Invalidate refresh token and clear cookie
```

---

### ✅ Flow 5: Change Password (Authenticated User)

```
1. POST /api/v1/auth/login             → Login to get {{ACCESS_TOKEN}}
2. POST /api/v1/user/change-password   → { "currentPassword": "...", "newPassword": "..." }
3. POST /api/v1/auth/logout            → Logout (old tokens invalidated)
4. POST /api/v1/auth/login             → Login with new password to verify
```

---

## 📌 Quick Reference Card

| API                          | Method | Path                               | Auth Required  |
|------------------------------|--------|------------------------------------|----------------|
| Register                     | POST   | `/api/v1/auth/register`            | ❌ None        |
| Verify OTP                   | POST   | `/api/v1/auth/verify-otp`          | ❌ None        |
| Resend OTP                   | POST   | `/api/v1/auth/resend-otp?email=..` | ❌ None        |
| Login                        | POST   | `/api/v1/auth/login`               | ❌ None        |
| Refresh Token                | POST   | `/api/v1/auth/refresh`             | ❌ (Cookie)    |
| Logout                       | POST   | `/api/v1/auth/logout`              | ❌ (Cookie)    |
| Reset Password (Forgot)      | POST   | `/api/v1/auth/reset-password`      | ❌ None        |
| Update Password (New)        | POST   | `/api/v1/auth/update-password`     | ❌ None        |
| Get User Dashboard           | GET    | `/api/v1/user/dashboard`           | ✅ USER/ADMIN  |
| Get User Profile             | GET    | `/api/v1/user/profile`             | ✅ USER/ADMIN  |
| Change Password              | POST   | `/api/v1/user/change-password`     | ✅ USER/ADMIN  |
| Get Admin Dashboard          | GET    | `/api/v1/admin/dashboard`          | 🛡️ ADMIN only  |
| Get All Users (paginated)    | GET    | `/api/v1/admin/users`              | 🛡️ ADMIN only  |

---

*Generated for Full-Stack Authentication System — Spring Boot + JWT + OAuth2*
*Backend default port: `8080` | Frontend default port: `5173`*
