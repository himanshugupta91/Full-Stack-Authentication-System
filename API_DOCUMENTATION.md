# API Documentation - Authentication System

This document provides a complete reference for the API endpoints available in the application.

Base URL: `http://localhost:8080/api`

## Authentication (`/auth`)

These endpoints are public and do not require a JWT token (except for update-password which requires a reset token).

### 1. Register User
**POST** `/auth/register`

Creates a new user account and sends an OTP for verification.

**Request Body:**
```json
{
  "name": "John Doe",
  "email": "john@example.com",
  "password": "securepassword123"
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

Authenticates a user and returns a JWT token.

**Request Body:**
```json
{
  "email": "john@example.com",
  "password": "securepassword123"
}
```

**Response (200 OK):**
```json
{
  "name": "John Doe",
  "email": "john@example.com",
  "accessToken": "eyJhbGciOiJIUzI1NiJ9...",
  "roles": ["ROLE_USER"]
}
```

### 4. Forgot Password (Reset Request)
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

### 5. Update Password
**POST** `/auth/update-password`

Completes the password recovery process using a valid reset token.

**Request Body:**
```json
{
  "token": "valid-reset-token-uuid",
  "newPassword": "newsecurepassword123"
}
```

**Response (200 OK):**
```json
{
  "message": "Password updated successfully! You can now login.",
  "success": true
}
```

### 6. Resend OTP
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

### 7. Get User Dashboard
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

### 8. Get User Profile
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

### 9. Change Password
**POST** `/user/change-password`

Allows a logged-in user to change their password.

**Request Body:**
```json
{
  "currentPassword": "oldpassword123",
  "newPassword": "newpassword123"
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

### 10. Get Admin Dashboard
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

### 11. Get All Users
**GET** `/admin/users`

Returns a list of all registered users in the system.

**Response (200 OK):**
```json
[
  {
    "id": 1,
    "name": "Admin User",
    "email": "admin@admin.com",
    "roles": ["ROLE_USER", "ROLE_ADMIN"]
  },
  {
    "id": 2,
    "name": "John Doe",
    "email": "john@example.com",
    "roles": ["ROLE_USER"]
  }
]
```
