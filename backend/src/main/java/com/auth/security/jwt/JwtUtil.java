package com.auth.security.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;

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

    @PostConstruct
    void validateConfiguration() {
        if (accessTokenExpiration <= 0) {
            throw new IllegalStateException("jwt.expiration must be greater than 0.");
        }
        getSigningKey();
    }

    /**
     * Generates an access token for an authenticated principal.
     */
    public String generateToken(Authentication authentication) {
        UserDetails principal = (UserDetails) authentication.getPrincipal();
        List<String> roles = principal.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();
        return generateTokenFromEmailAndRoles(principal.getUsername(), roles);
    }

    /**
     * Generates an access token from an email address and role list.
     */
    public String generateTokenFromEmailAndRoles(String email, List<String> roles) {
        return Jwts.builder()
                .subject(email)
                .issuedAt(new Date())
                .claim("tokenType", "access")
                .claim("roles", roles)
                .expiration(new Date(System.currentTimeMillis() + accessTokenExpiration))
                .signWith(getSigningKey())
                .compact();
    }

    /**
     * Generates an access token for the given email without role claims (fallback/legacy).
     */
    public String generateTokenFromEmail(String email) {
        return generateTokenFromEmailAndRoles(email, List.of());
    }

    /**
     * Extracts the subject (email) from a signed JWT token.
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
     * Extracts the role list from the {@code roles} claim of a signed JWT token.
     */
    public List<String> getRolesFromToken(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
        @SuppressWarnings("unchecked")
        List<String> roles = (List<String>) claims.get("roles", List.class);
        return roles != null ? roles : List.of();
    }

    /**
     * Returns {@code true} if the token signature and expiry are valid.
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

    /**
     * Builds the JWT signing key from the configured secret.
     * Accepts Base64, Base64URL, or plain-text secrets.
     */
    private SecretKey getSigningKey() {
        byte[] keyBytes = decodeSecret(jwtSecret);
        if (keyBytes.length < 32) {
            throw new IllegalStateException("jwt.secret must be at least 32 bytes for HS256 signing.");
        }
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * Decodes the secret using Base64 / Base64URL and falls back to UTF-8 bytes
     * for plain-text secrets used in local/dev environments.
     */
    private byte[] decodeSecret(String secret) {
        if (secret == null || secret.isBlank()) {
            throw new IllegalStateException("jwt.secret is missing or blank.");
        }

        String normalized = secret.trim();

        try {
            return Decoders.BASE64.decode(normalized);
        } catch (RuntimeException ignored) {
            // Fallback to Base64URL or plain text for local/dev setups.
        }

        try {
            return Decoders.BASE64URL.decode(normalized);
        } catch (RuntimeException ignored) {
            return normalized.getBytes(StandardCharsets.UTF_8);
        }
    }
}
