# Frontend Guide

This frontend is a React single-page application that sits on top of the Spring Boot auth backend. Its job is not only to render forms and dashboards, but to coordinate session bootstrap, token refresh, protected routing, OAuth callback completion, and consistent error handling across the browser experience.

## Responsibilities

- render public auth flows such as login, register, OTP verification, and password reset
- render protected user and admin screens
- maintain client-side auth state through `AuthContext`
- attach access tokens to protected API requests
- refresh expired sessions through the backend refresh endpoint
- complete OAuth sign-in after the backend issues the refresh cookie

## Key Design Decisions

### Session model

The frontend uses a split-token approach:

- access token: stored in memory
- refresh token: stored in an HttpOnly cookie by the backend

This avoids putting long-lived credentials in `localStorage` while still allowing the frontend to recover sessions cleanly.

### API handling

`src/services/api.js` owns:

- Axios instance creation
- response unwrapping for the backend `ApiResponse<T>` envelope
- request authorization headers
- automatic refresh retry on `401`
- auth storage cleanup on terminal auth failures

### Auth state

`src/context/AuthContext.jsx` is the source of truth for:

- current user
- loading state during session bootstrap
- login, logout, register, verify OTP, password reset, and OAuth completion helpers

## Environment Variables

Copy `.env.example` to `.env` and keep it limited to public configuration:

```bash
cp .env.example .env
```

Supported variables:

- `VITE_API_URL`: backend API base URL, for example `http://localhost:8080/api/v1`
- `VITE_OAUTH_BASE_URL`: backend origin used to start OAuth2 authorization redirects

Do not place secrets in the frontend `.env`. Vite exposes `VITE_*` values to browser code.

## Development Commands

Install dependencies:

```bash
npm install
```

Start development server:

```bash
npm run dev
```

Lint the codebase:

```bash
npm run lint
```

Create a production build:

```bash
npm run build
```

Preview the production build locally:

```bash
npm run preview
```

## Application Structure

```text
src/
|-- components/   shared UI and routing helpers
|-- context/      auth and theme providers
|-- pages/        route-level screens
|-- services/     API client and backend integration
|-- utils/        password policy, role helpers, API error helpers
|-- App.jsx       route definitions
`-- main.jsx      application bootstrap
```

## Route Overview

Public routes:

- `/`
- `/login`
- `/register`
- `/verify-otp`
- `/forgot-password`
- `/reset-password`
- `/oauth2/callback`

Protected routes:

- `/dashboard`
- `/change-password`
- `/admin`

`ProtectedRoute` blocks access for anonymous users and can enforce admin-only access where needed.

## Backend Integration Contract

The frontend expects the backend to provide:

- JSON responses wrapped in a consistent success/data/message shape
- access token in the auth response body
- refresh token through an HttpOnly cookie
- `401` for expired or invalid access tokens
- `/api/v1/auth/refresh` support for session continuation

If you change the auth contract on the backend, review these files first:

- `src/services/api.js`
- `src/context/AuthContext.jsx`
- `src/pages/OAuthCallback.jsx`

## Common Troubleshooting

### Login works but protected requests fail

Check:

- `VITE_API_URL`
- browser cookie acceptance
- backend CORS configuration
- cookie `Secure` and `SameSite` settings

### OAuth button redirects but callback fails

Check:

- `VITE_OAUTH_BASE_URL`
- backend OAuth client credentials
- provider redirect URI setup
- frontend and backend origins

### Session disappears after refresh

Check:

- whether the backend actually sets the refresh cookie
- whether the browser rejects the cookie
- whether `/api/v1/auth/refresh` is reachable from the frontend origin

## Related Docs

- Project overview: [../README.md](../README.md)
- Architecture handoff: [../docs/architecture.md](../docs/architecture.md)
- Backend property reference: [../backend/src/main/resources/application.properties.example](../backend/src/main/resources/application.properties.example)
