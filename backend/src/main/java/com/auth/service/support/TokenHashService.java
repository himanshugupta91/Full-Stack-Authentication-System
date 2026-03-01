package com.auth.service.support;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

/**
 * Provides deterministic one-way hashing for opaque authentication tokens.
 */
@Service
public class TokenHashService {

    @Value("${security.token-hash-pepper:${jwt.secret}}")
    private String tokenHashPepper;

    /** Hashes a raw token using SHA-256 + server-side pepper. */
    public String hash(String rawToken) {
        if (rawToken == null || rawToken.isBlank()) {
            throw new IllegalArgumentException("Token cannot be null or blank.");
        }

        String normalizedToken = rawToken.trim();
        byte[] digest = sha256((tokenHashPepper + ":" + normalizedToken).getBytes(StandardCharsets.UTF_8));
        String tokenHash = Base64.getUrlEncoder().withoutPadding().encodeToString(digest);
        return tokenHash;
    }

    /** Constant-time token/hash comparison helper. */
    public boolean matches(String rawToken, String tokenHash) {
        if (rawToken == null || rawToken.isBlank() || tokenHash == null || tokenHash.isBlank()) {
            return false;
        }

        byte[] computedHash = hash(rawToken).getBytes(StandardCharsets.UTF_8);
        byte[] storedHash = tokenHash.getBytes(StandardCharsets.UTF_8);
        boolean hashMatches = MessageDigest.isEqual(computedHash, storedHash);
        return hashMatches;
    }

    private byte[] sha256(byte[] payload) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(payload);
            return hash;
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException("SHA-256 algorithm is not available.", exception);
        }
    }
}
