package com.auth.service.auth;

import com.auth.dto.AuthResponse;
import com.auth.dto.AuthTokens;
import com.auth.entity.Role;
import com.auth.entity.User;
import com.auth.exception.TokenValidationException;
import com.auth.security.JwtUtil;
import com.auth.service.UserService;
import com.auth.service.support.TokenHashService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import lombok.RequiredArgsConstructor;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;

/**
 * Handles issuing, rotating, and invalidating access/refresh tokens.
 */
@Service
@RequiredArgsConstructor
public class AuthTokenService {

    private SecureRandom secureRandom = new SecureRandom();

    private final JwtUtil jwtUtil;

    private final UserService userService;

    private final TokenHashService tokenHashService;

    @Value("${jwt.refresh.expiration}")
    private long refreshTokenExpirationMs;

    /**
     * Issue a new access token and refresh token for the user.
     * Refresh token is rotated and persisted.
     */
    @Transactional
    public AuthTokens issueTokens(User user) {
        String accessToken = jwtUtil.generateTokenFromEmail(user.getEmail());
        String refreshToken = generateRefreshToken();

        user.setRefreshToken(tokenHashService.hash(refreshToken));
        user.setRefreshTokenExpiry(LocalDateTime.now().plusSeconds(refreshTokenExpirationMs / 1000L));
        userService.save(user);

        AuthResponse response = buildAuthResponse(user, accessToken);
        AuthTokens tokens = new AuthTokens(response, refreshToken);
        return tokens;
    }

    /**
     * Rotate refresh token and issue a fresh access token.
     */
    @Transactional
    public AuthTokens refreshTokens(String refreshToken) {
        if (!StringUtils.hasText(refreshToken)) {
            throw new TokenValidationException("Refresh token is required.");
        }

        String refreshTokenHash = tokenHashService.hash(refreshToken);
        User user = userService.findByRefreshToken(refreshTokenHash)
                .orElseThrow(() -> new TokenValidationException("Invalid refresh token."));

        if (hasRefreshTokenExpired(user.getRefreshTokenExpiry())) {
            clearStoredRefreshToken(user);
            throw new TokenValidationException("Refresh token has expired. Please login again.");
        }

        AuthTokens refreshedTokens = issueTokens(user);
        return refreshedTokens;
    }

    /**
     * Invalidate refresh token if present.
     */
    @Transactional
    public void revokeRefreshToken(String refreshToken) {
        if (!StringUtils.hasText(refreshToken)) {
            return;
        }

        String refreshTokenHash = tokenHashService.hash(refreshToken);
        userService.findByRefreshToken(refreshTokenHash).ifPresent(this::clearStoredRefreshToken);
    }

    /** Builds API auth response payload with token metadata and current user details. */
    private AuthResponse buildAuthResponse(User user, String accessToken) {
        List<String> roles = user.getRoles().stream()
                .map(Role::getName)
                .map(Enum::name)
                .toList();

        AuthResponse authResponse = new AuthResponse(
                accessToken,
                "Bearer",
                jwtUtil.getAccessTokenExpiration(),
                refreshTokenExpirationMs,
                user.getId(),
                user.getName(),
                user.getEmail(),
                roles);
        return authResponse;
    }

    /** Generates a high-entropy URL-safe refresh token. */
    private String generateRefreshToken() {
        byte[] randomBytes = new byte[64];
        secureRandom.nextBytes(randomBytes);
        String refreshToken = Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);
        return refreshToken;
    }

    private boolean hasRefreshTokenExpired(LocalDateTime expiry) {
        boolean tokenExpired = expiry == null || expiry.isBefore(LocalDateTime.now());
        return tokenExpired;
    }

    private void clearStoredRefreshToken(User user) {
        user.setRefreshToken(null);
        user.setRefreshTokenExpiry(null);
        userService.save(user);
    }

}
