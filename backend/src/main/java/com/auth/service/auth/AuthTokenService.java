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
import lombok.RequiredArgsConstructor;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Handles issuing, rotating, and invalidating access/refresh tokens.
 */
@Service
@RequiredArgsConstructor
public class AuthTokenService {

    private static final int REFRESH_TOKEN_BYTE_LENGTH = 64;
    private static final long MILLISECONDS_PER_SECOND = 1000L;
    private static final String BEARER_TOKEN_TYPE = "Bearer";

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
        user.setRefreshTokenExpiry(LocalDateTime.now().plusSeconds(refreshTokenExpirationMs / MILLISECONDS_PER_SECOND));
        userService.save(user);

        AuthResponse response = buildAuthResponse(user, accessToken);
        return new AuthTokens(response, refreshToken);
    }

    /**
     * Rotate refresh token and issue a fresh access token.
     */
    @Transactional
    public AuthTokens refreshTokens(String refreshToken) {
        if (isBlank(refreshToken)) {
            throw new TokenValidationException("Refresh token is required.");
        }

        String refreshTokenHash = tokenHashService.hash(refreshToken);
        User user = userService.findByRefreshToken(refreshTokenHash)
                .orElseThrow(() -> new TokenValidationException("Invalid refresh token."));

        if (isRefreshTokenExpired(user.getRefreshTokenExpiry())) {
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
        if (isBlank(refreshToken)) {
            return;
        }

        String refreshTokenHash = tokenHashService.hash(refreshToken);
        Optional<User> userOpt = userService.findByRefreshToken(refreshTokenHash);
        if (userOpt.isEmpty()) {
            return;
        }

        User user = userOpt.get();
        user.setRefreshToken(null);
        user.setRefreshTokenExpiry(null);
        userService.save(user);
    }

    /** Builds API auth response payload with token metadata and current user details. */
    private AuthResponse buildAuthResponse(User user, String accessToken) {
        List<String> roles = user.getRoles().stream()
                .map(Role::getName)
                .map(Enum::name)
                .collect(Collectors.toList());

        return new AuthResponse(
                accessToken,
                BEARER_TOKEN_TYPE,
                jwtUtil.getAccessTokenExpiration(),
                refreshTokenExpirationMs,
                user.getId(),
                user.getName(),
                user.getEmail(),
                roles);
    }

    /** Generates a high-entropy URL-safe refresh token. */
    private String generateRefreshToken() {
        byte[] randomBytes = new byte[REFRESH_TOKEN_BYTE_LENGTH];
        SECURE_RANDOM.nextBytes(randomBytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);
    }

    private boolean isBlank(String value) {
        if (value == null) {
            return true;
        }
        return value.isBlank();
    }

    private boolean isRefreshTokenExpired(LocalDateTime expiry) {
        if (expiry == null) {
            return true;
        }

        LocalDateTime now = LocalDateTime.now();
        return expiry.isBefore(now);
    }

}
