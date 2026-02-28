# API Documentation - Authentication System

This document provides a complete reference for the API endpoints available in the application.

Base URL: `http://localhost:8080/api`

## Authentication (`/auth`)

These endpoints are public and do not require a JWT token (except for update-password which requires a reset token).
Sensitive authentication endpoints include abuse protection and may return:
- `429 Too Many Requests` (rate limit exceeded, with `Retry-After` header)
- `423 Locked` (temporary account/OTP lock after repeated failures)

### 1. Register User
**POST** `/auth/register`

Creates a new user account and sends an OTP for verification.

**Request Body:**
```json
{
  "name": "John Doe",
  "email": "john@example.com",
  "password": "StrongPass#2026"
}
```

**Response (200 OK):**
```json
{
  "message": "Registration successful! Please check your email for OTP verification.",
  "success": true
}
```

### 2. Verify OTP
**POST** `/auth/verify-otp`

Verifies the user's email address using the OTP sent to their email.

**Request Body:**
```json
{
  "email": "john@example.com",
  "otp": "123456"
}
```

**Response (200 OK):**
```json
{
  "message": "Email verified successfully! You can now login.",
  "success": true
}
```

### 3. Login
**POST** `/auth/login`

Authenticates a user and returns a short-lived access token.
Also sets a long-lived refresh token in a secure HTTP-only cookie.

**Request Body:**
```json
{
  "email": "john@example.com",
  "password": "StrongPass#2026"
}
```

**Response (200 OK):**
```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiJ9...",
  "tokenType": "Bearer",
  "accessTokenExpiresInMs": 900000,
  "refreshTokenExpiresInMs": 604800000,
  "id": 2,
  "name": "John Doe",
  "email": "john@example.com",
  "roles": ["ROLE_USER"]
}
```

### 4. Refresh Access Token
**POST** `/auth/refresh`

Uses refresh token cookie to rotate refresh token and issue a new access token.

**Response (200 OK):**
```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiJ9...",
  "tokenType": "Bearer",
  "accessTokenExpiresInMs": 900000,
  "refreshTokenExpiresInMs": 604800000,
  "id": 2,
  "name": "John Doe",
  "email": "john@example.com",
  "roles": ["ROLE_USER"]
}
```

### 5. Logout
**POST** `/auth/logout`

Invalidates refresh token and clears refresh cookie.

**Response (200 OK):**
```json
{
  "message": "Logged out successfully.",
  "success": true
}
```

### 6. Forgot Password (Reset Request)
**POST** `/auth/reset-password`

Initiates the password reset process by sending a reset link (token) to the user's email.

**Request Body:**
```json
{
  "email": "john@example.com"
}
```

**Response (200 OK):**
```json
{
  "message": "If an account exists with this email, a reset link will be sent.",
  "success": true
}
```

### 7. Update Password
**POST** `/auth/update-password`

Completes the password recovery process using a valid reset token.

**Request Body:**
```json
{
  "token": "valid-reset-token-uuid",
  "newPassword": "NewStrongPass#2026"
}
```

**Response (200 OK):**
```json
{
  "message": "Password updated successfully! You can now login.",
  "success": true
}
```

### 8. Resend OTP
**POST** `/auth/resend-otp?email=john@example.com`

Resends the verification OTP if the previous one expired or was lost.

**Query Parameters:** `email` (string)

**Response (200 OK):**
```json
{
  "message": "OTP sent successfully! Please check your email.",
  "success": true
}
```

---

## User (`/user`)

Requires Authentication header: `Authorization: Bearer <token>`

### 9. Get User Dashboard
**GET** `/user/dashboard`

Returns dashboard data for the authenticated user.

**Response (200 OK):**
```json
{
  "welcomeMessage": "Welcome to your dashboard, John Doe!",
  "email": "john@example.com",
  "serverTime": "2024-02-07T14:30:00"
}
```

### 10. Get User Profile
**GET** `/user/profile`

Returns profile details of the authenticated user.

**Response (200 OK):**
```json
{
  "id": 1,
  "name": "John Doe",
  "email": "john@example.com",
  "enabled": true,
  "roles": ["ROLE_USER"]
}
```

### 11. Change Password
**POST** `/user/change-password`

Allows a logged-in user to change their password.

**Request Body:**
```json
{
  "currentPassword": "oldpassword123",
  "newPassword": "NewStrongPass#2026"
}
```

**Response (200 OK):**
```json
{
  "message": "Password changed successfully!",
  "success": true
}
```

---

## Admin (`/admin`)

Requires Authentication header: `Authorization: Bearer <token>` (User must have `ROLE_ADMIN`)

### 12. Get Admin Dashboard
**GET** `/admin/dashboard`

Returns specific data for the admin dashboard.

**Response (200 OK):**
```json
{
  "message": "Welcome to Admin Dashboard",
  "activeUsers": 15,
  "systemStatus": "Operational"
}
```

### 13. Get Users (Paginated + Search/Filter)
**GET** `/admin/users`

Returns paginated users with optional search and filters.

**Query Parameters (optional):**
- `page` (default `0`)
- `size` (default `20`, max `100`)
- `search` (matches name/email)
- `enabled` (`true` or `false`)
- `role` (`USER`, `ADMIN`, `ROLE_USER`, `ROLE_ADMIN`)
- `sortBy` (`id`, `name`, `email`, `enabled`, `createdAt`)
- `sortDir` (`asc` or `desc`)

**Response (200 OK):**
```json
{
  "content": [
    {
      "id": 1,
      "name": "Admin User",
      "email": "admin@admin.com",
      "roles": ["ROLE_USER", "ROLE_ADMIN"],
      "enabled": true
    }
  ],
  "number": 0,
  "size": 10,
  "totalElements": 1,
  "totalPages": 1
}
```

---

## OAuth2 Login Entry Points

Browser-based OAuth2 login endpoints:
- `GET /oauth2/authorization/google`
- `GET /oauth2/authorization/github`
- `GET /oauth2/authorization/apple`
- `GET /oauth2/authorization/linkedin`

On success, backend redirects to frontend `/oauth2/callback` and sets refresh token cookie.
Frontend then calls `POST /api/auth/refresh` to obtain a fresh access token.
