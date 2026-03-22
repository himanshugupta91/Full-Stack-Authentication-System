package com.auth.service.support;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

/**
 * Provides deterministic one-way hashing for opaque authentication tokens
 * (OTP codes, reset tokens, refresh tokens) using SHA-256 with a server-side pepper.
 */
@Service
public class TokenHashService {

    @Value("${security.token-hash-pepper:${jwt.secret}}")
    private String pepper;

    /**
     * Hashes a raw token using SHA-256 and the configured server-side pepper.
     *
     * @param rawToken the plain-text token to hash; must not be null or blank
     * @return a Base64URL-encoded SHA-256 digest
     * @throws IllegalArgumentException if {@code rawToken} is null or blank
     */
    public String hash(String rawToken) {
        if (rawToken == null || rawToken.isBlank()) {
            throw new IllegalArgumentException("Token cannot be null or blank.");
        }
        String normalized = rawToken.trim();
        byte[] digest = sha256((pepper + ":" + normalized).getBytes(StandardCharsets.UTF_8));
        return Base64.getUrlEncoder().withoutPadding().encodeToString(digest);
    }

    /**
     * Performs a constant-time comparison between a raw token and its stored hash
     * to prevent timing-based oracle attacks.
     *
     * @param rawToken  the plain-text token provided by the caller
     * @param storedHash the previously persisted hash to compare against
     * @return {@code true} if the hashes match, {@code false} otherwise
     */
    public boolean matches(String rawToken, String storedHash) {
        if (rawToken == null || rawToken.isBlank() || storedHash == null || storedHash.isBlank()) {
            return false;
        }
        byte[] computed = hash(rawToken).getBytes(StandardCharsets.UTF_8);
        byte[] stored = storedHash.getBytes(StandardCharsets.UTF_8);
        return MessageDigest.isEqual(computed, stored);
    }

    private byte[] sha256(byte[] payload) {
        try {
            return MessageDigest.getInstance("SHA-256").digest(payload);
        } catch (NoSuchAlgorithmException ex) {
            throw new IllegalStateException("SHA-256 algorithm is not available.", ex);
        }
    }
}
