package com.auth.service;

import com.auth.dto.AuthResponse;
import com.auth.dto.AuthTokens;
import com.auth.entity.User;
import com.auth.exception.TokenValidationException;
import com.auth.security.JwtUtil;
import com.auth.service.UserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

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
        user.setRefreshTokenExpiry(LocalDateTime.now().plusSeconds(refreshTokenExpirationMs / 1000));
        userService.save(user);

        AuthResponse response = buildAuthResponse(user, accessToken);
        return new AuthTokens(response, refreshToken);
    }

    /**
     * Rotate refresh token and issue a fresh access token.
     */
    @Transactional
    public AuthTokens refreshTokens(String refreshToken) {
        if (refreshToken == null || refreshToken.isBlank()) {
            throw new TokenValidationException("Refresh token is required.");
        }

        String refreshTokenHash = tokenHashService.hash(refreshToken);

        User user = userService.findByRefreshToken(refreshTokenHash)
                .orElseThrow(() -> new TokenValidationException("Invalid refresh token."));

        if (user.getRefreshTokenExpiry() == null || user.getRefreshTokenExpiry().isBefore(LocalDateTime.now())) {
            user.setRefreshToken(null);
            user.setRefreshTokenExpiry(null);
            userService.save(user);
            throw new TokenValidationException("Refresh token has expired. Please login again.");
        }

        return issueTokens(user);
    }

    /**
     * Invalidate refresh token if present.
     */
    @Transactional
    public void revokeRefreshToken(String refreshToken) {
        if (refreshToken == null || refreshToken.isBlank()) {
            return;
        }

        String refreshTokenHash = tokenHashService.hash(refreshToken);
        userService.findByRefreshToken(refreshTokenHash).ifPresent(user -> {
            user.setRefreshToken(null);
            user.setRefreshTokenExpiry(null);
            userService.save(user);
        });
    }

    /** Builds API auth response payload with token metadata and current user details. */
    private AuthResponse buildAuthResponse(User user, String accessToken) {
        List<String> roles = user.getRoles().stream()
                .map(role -> role.getName().name())
                .toList();

        return new AuthResponse(
                accessToken,
                "Bearer",
                jwtUtil.getAccessTokenExpiration(),
                refreshTokenExpirationMs,
                user.getId(),
                user.getName(),
                user.getEmail(),
                roles);
    }

    /** Generates a high-entropy URL-safe refresh token. */
    private String generateRefreshToken() {
        byte[] randomBytes = new byte[64];
        SECURE_RANDOM.nextBytes(randomBytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);
    }

}
