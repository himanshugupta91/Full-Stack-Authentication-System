package com.auth.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * Utility class for JWT token generation and validation.
 */
@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Getter
    @Value("${jwt.expiration}")
    private long accessTokenExpiration;

    /**
     * Generate JWT token from authentication object.
     */
    public String generateToken(Authentication authentication) {
        UserDetails userPrincipal = (UserDetails) authentication.getPrincipal();
        return generateTokenFromEmail(userPrincipal.getUsername());
    }

    /**
     * Generate JWT token from email.
     */
    public String generateTokenFromEmail(String email) {
        return Jwts.builder()
                .subject(email)
                .issuedAt(new Date())
                .claim("tokenType", "access")
                .expiration(new Date(System.currentTimeMillis() + accessTokenExpiration))
                .signWith(getSigningKey())
                .compact();
    }

    /**
     * Extract email from JWT token.
     */
    public String getEmailFromToken(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    /**
     * Validate JWT token.
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    /** Builds the JWT signing key from configured secret using Base64, Base64URL, or raw text fallback. */
    private SecretKey getSigningKey() {
        byte[] keyBytes = decodeSecret(jwtSecret);
        if (keyBytes.length < 32) {
            throw new IllegalStateException("jwt.secret must be at least 32 bytes for HS256 signing.");
        }
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /** Decodes secret value from Base64/Base64URL and falls back to UTF-8 bytes for plain text secrets. */
    private byte[] decodeSecret(String secret) {
        if (secret == null || secret.isBlank()) {
            throw new IllegalStateException("jwt.secret is missing or blank.");
        }

        String normalizedSecret = secret.trim();

        try {
            return Decoders.BASE64.decode(normalizedSecret);
        } catch (RuntimeException ignored) {
            // Fallback to Base64URL or plain text for local/dev setups.
        }

        try {
            return Decoders.BASE64URL.decode(normalizedSecret);
        } catch (RuntimeException ignored) {
            return normalizedSecret.getBytes(StandardCharsets.UTF_8);
        }
    }

}
