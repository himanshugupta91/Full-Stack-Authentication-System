package com.auth.service.auth;

import com.auth.dto.response.AuthResponse;
import com.auth.dto.response.AuthTokens;
import com.auth.entity.Role;
import com.auth.entity.User;
import com.auth.exception.TokenValidationException;
import com.auth.security.jwt.JwtUtil;
import com.auth.service.UserService;
import com.auth.service.support.TokenHashService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;

/**
 * Handles issuing, rotating, and revoking access/refresh token pairs.
 *
 * <p>Refresh tokens are stored as peppered SHA-256 hashes to prevent
 * database-level token theft. Access tokens are short-lived, stateless JWTs.
 */
@Service
@RequiredArgsConstructor
public class AuthTokenService {

    private final SecureRandom secureRandom = new SecureRandom();

    private final JwtUtil jwtUtil;
    private final UserService userService;
    private final TokenHashService tokenHashService;

    @Value("${jwt.refresh.expiration}")
    private long refreshTokenExpirationMs;

    /**
     * Issues a new access token and a rotated refresh token for the given user.
     * The refresh token hash is persisted to the user record.
     */
    @Transactional
    public AuthTokens issueTokens(User user) {
        List<String> roles = resolveRoleNames(user);
        String accessToken = jwtUtil.generateTokenFromEmailAndRoles(user.getEmail(), roles);
        String refreshToken = generateRefreshToken();

        user.setRefreshToken(tokenHashService.hash(refreshToken));
        user.setRefreshTokenExpiry(LocalDateTime.now().plusSeconds(refreshTokenExpirationMs / 1000L));
        userService.save(user);

        return new AuthTokens(buildAuthResponse(user, accessToken), refreshToken);
    }

    /**
     * Validates a raw refresh token, rotates it, and returns a fresh token pair.
     *
     * @param refreshToken the raw (unhashed) refresh token provided by the client
     * @throws TokenValidationException if the token is missing, invalid, or expired
     */
    @Transactional
    public AuthTokens refreshTokens(String refreshToken) {
        if (!StringUtils.hasText(refreshToken)) {
            throw new TokenValidationException("Refresh token is required.");
        }

        String tokenHash = tokenHashService.hash(refreshToken);
        User user = userService.findByRefreshToken(tokenHash)
                .orElseThrow(() -> new TokenValidationException("Invalid refresh token."));

        if (isRefreshTokenExpired(user.getRefreshTokenExpiry())) {
            clearStoredRefreshToken(user);
            throw new TokenValidationException("Refresh token has expired. Please login again.");
        }

        return issueTokens(user);
    }

    /**
     * Invalidates the refresh token associated with the given raw token value,
     * if one exists. Silently succeeds if the token is blank or unknown.
     */
    @Transactional
    public void revokeRefreshToken(String refreshToken) {
        if (!StringUtils.hasText(refreshToken)) {
            return;
        }
        String tokenHash = tokenHashService.hash(refreshToken);
        userService.findByRefreshToken(tokenHash).ifPresent(this::clearStoredRefreshToken);
    }

    /**
     * Builds the API auth response payload with token metadata and the current
     * user's profile fields.
     */
    private AuthResponse buildAuthResponse(User user, String accessToken) {
        return new AuthResponse(
                HttpStatus.OK.value(),
                accessToken,
                "Bearer",
                jwtUtil.getAccessTokenExpiration(),
                refreshTokenExpirationMs,
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.isEnabled(),
                resolveRoleNames(user));
    }

    private List<String> resolveRoleNames(User user) {
        return user.getRoles().stream()
                .map(Role::getName)
                .map(Enum::name)
                .toList();
    }

    /** Generates a 64-byte, Base64URL-encoded cryptographically random refresh token. */
    private String generateRefreshToken() {
        byte[] bytes = new byte[64];
        secureRandom.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    private boolean isRefreshTokenExpired(LocalDateTime expiry) {
        return expiry == null || expiry.isBefore(LocalDateTime.now());
    }

    private void clearStoredRefreshToken(User user) {
        user.setRefreshToken(null);
        user.setRefreshTokenExpiry(null);
        userService.save(user);
    }
}
