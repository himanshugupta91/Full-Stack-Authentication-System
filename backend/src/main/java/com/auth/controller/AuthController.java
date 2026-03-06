package com.auth.controller;

import com.auth.config.ApiPaths;
import com.auth.security.RefreshTokenCookieService;
import com.auth.dto.response.AuthResponse;
import com.auth.dto.response.AuthTokens;
import com.auth.exception.TokenValidationException;
import com.auth.dto.request.LoginRequest;
import com.auth.dto.request.OtpVerifyRequest;
import com.auth.dto.request.RegisterRequest;
import com.auth.dto.request.ResetPasswordRequest;
import com.auth.dto.request.TokenRefreshRequest;
import com.auth.dto.request.UpdatePasswordRequest;
import com.auth.service.auth.AuthTokenService;
import com.auth.service.AuthService;
import com.auth.dto.response.MessageResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * REST controller for authentication endpoints.
 * Handles registration, login, OTP verification, and password reset.
 */
@RestController
@RequestMapping(ApiPaths.AUTH_V1)
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    private final AuthTokenService authTokenService;

    private final RefreshTokenCookieService refreshTokenCookieService;

    /**
     * Register a new user.
     * POST /api/v1/auth/register
     */
    @PostMapping("/register")
    public ResponseEntity<MessageResponse> register(@Valid @RequestBody RegisterRequest request) {
        MessageResponse response = authService.register(request);
        return ResponseEntity.ok(response);
    }

    /**
     * Verify email with OTP.
     * POST /api/v1/auth/verify-otp
     */
    @PostMapping("/verify-otp")
    public ResponseEntity<MessageResponse> verifyOtp(@Valid @RequestBody OtpVerifyRequest request) {
        MessageResponse response = authService.verifyOtp(request);
        return ResponseEntity.ok(response);
    }

    /**
     * Login user and return JWT token.
     * POST /api/v1/auth/login
     */
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request,
            HttpServletResponse httpResponse) {
        AuthTokens authTokens = authService.login(request);
        setRefreshTokenCookie(httpResponse, authTokens.refreshToken());
        AuthResponse authResponse = authTokens.response();
        return ResponseEntity.ok(authResponse);
    }

    /**
     * Refresh access token using refresh token from secure cookie.
     * POST /api/v1/auth/refresh
     */
    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refreshToken(
            HttpServletRequest httpRequest,
            HttpServletResponse httpResponse,
            @RequestBody(required = false) TokenRefreshRequest request) {
        List<String> refreshTokenCandidates = resolveRefreshTokenCandidates(httpRequest, request);
        if (refreshTokenCandidates.isEmpty()) {
            throw new TokenValidationException("Refresh token is required.");
        }

        TokenValidationException lastValidationException = null;
        for (String refreshToken : refreshTokenCandidates) {
            try {
                AuthTokens authTokens = authTokenService.refreshTokens(refreshToken);
                setRefreshTokenCookie(httpResponse, authTokens.refreshToken());
                AuthResponse authResponse = authTokens.response();
                return ResponseEntity.ok(authResponse);
            } catch (TokenValidationException exception) {
                lastValidationException = exception;
            }
        }

        throw lastValidationException;
    }

    /**
     * Logout and invalidate refresh token.
     * POST /api/v1/auth/logout
     */
    @PostMapping("/logout")
    public ResponseEntity<MessageResponse> logout(
            HttpServletRequest httpRequest,
            HttpServletResponse httpResponse,
            @RequestBody(required = false) TokenRefreshRequest request) {
        List<String> refreshTokenCandidates = resolveRefreshTokenCandidates(httpRequest, request);
        for (String refreshToken : refreshTokenCandidates) {
            authTokenService.revokeRefreshToken(refreshToken);
        }

        httpResponse.addHeader(HttpHeaders.SET_COOKIE, refreshTokenCookieService.clearRefreshTokenCookie());
        MessageResponse response = new MessageResponse("Logged out successfully.", true);
        return ResponseEntity.ok(response);
    }

    /**
     * Request password reset email.
     * POST /api/v1/auth/reset-password
     */
    @PostMapping("/reset-password")
    public ResponseEntity<MessageResponse> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        MessageResponse response = authService.resetPassword(request);
        return ResponseEntity.ok(response);
    }

    /**
     * Update password with reset token.
     * POST /api/v1/auth/update-password
     */
    @PostMapping("/update-password")
    public ResponseEntity<MessageResponse> updatePassword(@Valid @RequestBody UpdatePasswordRequest request) {
        MessageResponse response = authService.updatePassword(request);
        return ResponseEntity.ok(response);
    }

    /**
     * Resend OTP for email verification.
     * POST /api/v1/auth/resend-otp
     */
    @PostMapping("/resend-otp")
    public ResponseEntity<MessageResponse> resendOtp(@RequestParam String email) {
        MessageResponse response = authService.resendOtp(email);
        return ResponseEntity.ok(response);
    }

    /**
     * Resolves possible refresh-token values from request body and matching cookies.
     */
    private List<String> resolveRefreshTokenCandidates(HttpServletRequest request, TokenRefreshRequest body) {
        Set<String> tokenCandidates = new LinkedHashSet<>();

        if (body != null && StringUtils.hasText(body.getRefreshToken())) {
            tokenCandidates.add(body.getRefreshToken().trim());
        }

        tokenCandidates.addAll(findRefreshTokensInCookies(request));
        List<String> resolvedTokenCandidates = new ArrayList<>(tokenCandidates);
        return resolvedTokenCandidates;
    }

    private List<String> findRefreshTokensInCookies(HttpServletRequest request) {
        List<String> refreshTokens = new ArrayList<>();
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return refreshTokens;
        }

        String refreshCookieName = refreshTokenCookieService.getCookieName();
        for (Cookie cookie : cookies) {
            if (refreshCookieName.equals(cookie.getName())) {
                String cookieValue = cookie.getValue();
                if (StringUtils.hasText(cookieValue)) {
                    refreshTokens.add(cookieValue.trim());
                }
            }
        }

        return refreshTokens;
    }

    private void setRefreshTokenCookie(HttpServletResponse response, String refreshToken) {
        response.addHeader(HttpHeaders.SET_COOKIE, refreshTokenCookieService.buildRefreshTokenCookie(refreshToken));
    }
}
