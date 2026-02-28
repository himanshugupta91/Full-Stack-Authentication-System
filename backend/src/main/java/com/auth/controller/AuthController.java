package com.auth.controller;

import com.auth.security.RefreshTokenCookieService;
import com.auth.dto.AuthResponse;
import com.auth.dto.AuthTokens;
import com.auth.dto.LoginRequest;
import com.auth.dto.OtpVerifyRequest;
import com.auth.dto.RegisterRequest;
import com.auth.dto.ResetPasswordRequest;
import com.auth.dto.TokenRefreshRequest;
import com.auth.dto.UpdatePasswordRequest;
import com.auth.service.AuthTokenService;
import com.auth.service.AuthService;
import com.auth.dto.MessageResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for authentication endpoints.
 * Handles registration, login, OTP verification, and password reset.
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    private final AuthTokenService authTokenService;

    private final RefreshTokenCookieService refreshTokenCookieService;

    /**
     * Register a new user.
     * POST /api/auth/register
     */
    @PostMapping("/register")
    public ResponseEntity<MessageResponse> register(@Valid @RequestBody RegisterRequest request) {
        MessageResponse response = authService.register(request);
        return ResponseEntity.ok(response);
    }

    /**
     * Verify email with OTP.
     * POST /api/auth/verify-otp
     */
    @PostMapping("/verify-otp")
    public ResponseEntity<MessageResponse> verifyOtp(@Valid @RequestBody OtpVerifyRequest request) {
        MessageResponse response = authService.verifyOtp(request);
        return ResponseEntity.ok(response);
    }

    /**
     * Login user and return JWT token.
     * POST /api/auth/login
     */
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request, HttpServletResponse httpResponse) {
        AuthTokens authTokens = authService.login(request);
        httpResponse.addHeader(HttpHeaders.SET_COOKIE,
                refreshTokenCookieService.buildRefreshTokenCookie(authTokens.refreshToken()));
        return ResponseEntity.ok(authTokens.response());
    }

    /**
     * Refresh access token using refresh token from secure cookie.
     * POST /api/auth/refresh
     */
    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refreshToken(
            HttpServletRequest httpRequest,
            HttpServletResponse httpResponse,
            @RequestBody(required = false) TokenRefreshRequest request) {
        String refreshToken = resolveRefreshToken(httpRequest, request);

        AuthTokens authTokens = authTokenService.refreshTokens(refreshToken);
        httpResponse.addHeader(HttpHeaders.SET_COOKIE,
                refreshTokenCookieService.buildRefreshTokenCookie(authTokens.refreshToken()));
        return ResponseEntity.ok(authTokens.response());
    }

    /**
     * Logout and invalidate refresh token.
     * POST /api/auth/logout
     */
    @PostMapping("/logout")
    public ResponseEntity<MessageResponse> logout(
            HttpServletRequest httpRequest,
            HttpServletResponse httpResponse,
            @RequestBody(required = false) TokenRefreshRequest request) {
        String refreshToken = resolveRefreshToken(httpRequest, request);
        authTokenService.revokeRefreshToken(refreshToken);

        httpResponse.addHeader(HttpHeaders.SET_COOKIE, refreshTokenCookieService.clearRefreshTokenCookie());
        return ResponseEntity.ok(new MessageResponse("Logged out successfully.", true));
    }

    /**
     * Request password reset email.
     * POST /api/auth/reset-password
     */
    @PostMapping("/reset-password")
    public ResponseEntity<MessageResponse> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        MessageResponse response = authService.resetPassword(request);
        return ResponseEntity.ok(response);
    }

    /**
     * Update password with reset token.
     * POST /api/auth/update-password
     */
    @PostMapping("/update-password")
    public ResponseEntity<MessageResponse> updatePassword(@Valid @RequestBody UpdatePasswordRequest request) {
        MessageResponse response = authService.updatePassword(request);
        return ResponseEntity.ok(response);
    }

    /**
     * Resend OTP for email verification.
     * POST /api/auth/resend-otp
     */
    @PostMapping("/resend-otp")
    public ResponseEntity<MessageResponse> resendOtp(@RequestParam String email) {
        MessageResponse response = authService.resendOtp(email);
        return ResponseEntity.ok(response);
    }

    /** Resolves refresh token from request body first, then from configured cookie. */
    private String resolveRefreshToken(HttpServletRequest request, TokenRefreshRequest body) {
        if (body != null && body.getRefreshToken() != null && !body.getRefreshToken().isBlank()) {
            return body.getRefreshToken();
        }

        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return null;
        }

        for (Cookie cookie : cookies) {
            if (refreshTokenCookieService.getCookieName().equals(cookie.getName())) {
                return cookie.getValue();
            }
        }

        return null;
    }
}
