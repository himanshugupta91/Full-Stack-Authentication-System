package com.auth.controller;

import com.auth.config.ApiPaths;
import com.auth.dto.request.LoginRequest;
import com.auth.dto.request.OtpVerifyRequest;
import com.auth.dto.request.RegisterRequest;
import com.auth.dto.request.ResetPasswordRequest;
import com.auth.dto.request.TokenRefreshRequest;
import com.auth.dto.request.UpdatePasswordRequest;
import com.auth.dto.response.ApiResponse;
import com.auth.dto.response.AuthResponse;
import com.auth.dto.response.AuthTokens;
import com.auth.dto.response.MessageResponse;
import com.auth.exception.TokenValidationException;
import com.auth.security.RefreshTokenCookieService;
import com.auth.service.AuthService;
import com.auth.service.auth.AuthTokenService;
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
 * Handles registration, login, OTP verification, token refresh, logout,
 * and password reset/change flows.
 */
@RestController
@RequestMapping(ApiPaths.AUTH_V1)
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final AuthTokenService authTokenService;
    private final RefreshTokenCookieService refreshTokenCookieService;

    /**
     * Registers a new user account.
     * POST /api/v1/auth/register
     */
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<MessageResponse>> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(authService.register(request)));
    }

    /**
     * Verifies a user's email with the OTP code sent during registration.
     * POST /api/v1/auth/verify-otp
     */
    @PostMapping("/verify-otp")
    public ResponseEntity<ApiResponse<MessageResponse>> verifyOtp(@Valid @RequestBody OtpVerifyRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(authService.verifyOtp(request)));
    }

    /**
     * Authenticates a user and issues a token pair.
     * The refresh token is returned as an HTTP-only cookie; the access token is in the body.
     * POST /api/v1/auth/login
     */
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(
            @Valid @RequestBody LoginRequest request,
            HttpServletRequest httpRequest,
            HttpServletResponse httpResponse) {
        AuthTokens tokens = authService.login(request);
        setRefreshTokenCookie(httpRequest, httpResponse, tokens.refreshToken());
        return ResponseEntity.ok(ApiResponse.ok(tokens.response()));
    }

    /**
     * Issues a new access token using a valid refresh token.
     * Accepts the refresh token from either the HTTP-only cookie or the request body.
     * POST /api/v1/auth/refresh
     */
    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<AuthResponse>> refreshToken(
            HttpServletRequest request,
            HttpServletResponse response,
            @RequestBody(required = false) TokenRefreshRequest body) {

        List<String> candidates = resolveRefreshTokenCandidates(request, body);
        if (candidates.isEmpty()) {
            throw new TokenValidationException("Refresh token is required.");
        }

        TokenValidationException lastException = null;
        for (String refreshToken : candidates) {
            try {
                AuthTokens tokens = authTokenService.refreshTokens(refreshToken);
                setRefreshTokenCookie(request, response, tokens.refreshToken());
                return ResponseEntity.ok(ApiResponse.ok(tokens.response()));
            } catch (TokenValidationException ex) {
                lastException = ex;
            }
        }

        throw lastException;
    }

    /**
     * Invalidates the refresh token and clears the cookie.
     * POST /api/v1/auth/logout
     */
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<MessageResponse>> logout(
            HttpServletRequest request,
            HttpServletResponse response,
            @RequestBody(required = false) TokenRefreshRequest body) {

        resolveRefreshTokenCandidates(request, body)
                .forEach(authTokenService::revokeRefreshToken);

        response.addHeader(HttpHeaders.SET_COOKIE,
                refreshTokenCookieService.clearRefreshTokenCookie(request));

        return ResponseEntity.ok(ApiResponse.ok(new MessageResponse("Logged out successfully.", true)));
    }

    /**
     * Sends a password-reset link to the user's email address.
     * POST /api/v1/auth/reset-password
     */
    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponse<MessageResponse>> resetPassword(
            @Valid @RequestBody ResetPasswordRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(authService.resetPassword(request)));
    }

    /**
     * Updates the user's password using a valid reset token.
     * POST /api/v1/auth/update-password
     */
    @PostMapping("/update-password")
    public ResponseEntity<ApiResponse<MessageResponse>> updatePassword(
            @Valid @RequestBody UpdatePasswordRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(authService.updatePassword(request)));
    }

    /**
     * Resends an OTP verification code to the given email address.
     * POST /api/v1/auth/resend-otp
     */
    @PostMapping("/resend-otp")
    public ResponseEntity<ApiResponse<MessageResponse>> resendOtp(@RequestParam String email) {
        return ResponseEntity.ok(ApiResponse.ok(authService.resendOtp(email)));
    }

    // ── Private helpers ───────────────────────────────────────────────────────

    /**
     * Collects distinct refresh-token candidates from the request body (preferred)
     * and any matching cookies, preserving insertion order so the body token is
     * tried first.
     */
    private List<String> resolveRefreshTokenCandidates(HttpServletRequest request, TokenRefreshRequest body) {
        Set<String> candidates = new LinkedHashSet<>();

        if (body != null && StringUtils.hasText(body.getRefreshToken())) {
            candidates.add(body.getRefreshToken().trim());
        }
        candidates.addAll(extractRefreshTokensFromCookies(request));

        return new ArrayList<>(candidates);
    }

    private List<String> extractRefreshTokensFromCookies(HttpServletRequest request) {
        List<String> tokens = new ArrayList<>();
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return tokens;
        }

        String cookieName = refreshTokenCookieService.getCookieName();
        for (Cookie cookie : cookies) {
            if (cookieName.equals(cookie.getName()) && StringUtils.hasText(cookie.getValue())) {
                tokens.add(cookie.getValue().trim());
            }
        }
        return tokens;
    }

    private void setRefreshTokenCookie(HttpServletRequest request, HttpServletResponse response,
            String refreshToken) {
        response.addHeader(HttpHeaders.SET_COOKIE,
                refreshTokenCookieService.buildRefreshTokenCookie(refreshToken, request));
    }
}
