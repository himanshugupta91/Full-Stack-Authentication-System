package com.auth.security;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * Builds HTTP-only cookie headers for refresh token handling.
 */
@Component
public class RefreshTokenCookieService {

    @Value("${auth.refresh-token.cookie-name:refreshToken}")
    private String cookieName;

    @Value("${auth.refresh-token.cookie-path:/api/v1/auth}")
    private String cookiePath;

    @Value("${auth.refresh-token.cookie-secure:false}")
    private boolean cookieSecure;

    @Value("${auth.refresh-token.cookie-same-site:Lax}")
    private String cookieSameSite;

    @Value("${auth.refresh-token.cookie-domain:}")
    private String cookieDomain;

    @Value("${jwt.refresh.expiration}")
    private long refreshTokenExpirationMs;

    @PostConstruct
    void validateCookieConfiguration() {
        if ("none".equalsIgnoreCase(cookieSameSite) && !cookieSecure) {
            throw new IllegalStateException(
                    "auth.refresh-token.cookie-secure must be true when auth.refresh-token.cookie-same-site=None.");
        }
    }

    /** Builds the HTTP-only refresh-token cookie header value. */
    public String buildRefreshTokenCookie(String refreshToken) {
        ResponseCookie refreshCookie = cookieBuilder(refreshToken)
                .maxAge(refreshTokenExpirationMs / 1000L)
                .build();
        String cookieHeader = refreshCookie.toString();
        return cookieHeader;
    }

    /** Builds an expired refresh-token cookie header value to clear browser state. */
    public String clearRefreshTokenCookie() {
        ResponseCookie clearedCookie = cookieBuilder("")
                .maxAge(0)
                .build();
        String cookieHeader = clearedCookie.toString();
        return cookieHeader;
    }

    /** Returns the configured refresh-token cookie name for request lookup. */
    public String getCookieName() {
        String configuredCookieName = cookieName;
        return configuredCookieName;
    }

    private ResponseCookie.ResponseCookieBuilder cookieBuilder(String value) {
        ResponseCookie.ResponseCookieBuilder builder = ResponseCookie.from(cookieName, value)
                .httpOnly(true)
                .secure(cookieSecure)
                .path(cookiePath)
                .sameSite(cookieSameSite);

        if (StringUtils.hasText(cookieDomain)) {
            builder.domain(cookieDomain.trim());
        }

        return builder;
    }
}
