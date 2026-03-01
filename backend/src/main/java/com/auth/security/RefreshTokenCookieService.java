package com.auth.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

/**
 * Builds HTTP-only cookie headers for refresh token handling.
 */
@Component
public class RefreshTokenCookieService {

    private static final long MILLISECONDS_PER_SECOND = 1000L;

    @Value("${auth.refresh-token.cookie-name:refreshToken}")
    private String cookieName;

    @Value("${auth.refresh-token.cookie-path:/api/auth}")
    private String cookiePath;

    @Value("${auth.refresh-token.cookie-secure:false}")
    private boolean cookieSecure;

    @Value("${auth.refresh-token.cookie-same-site:Lax}")
    private String cookieSameSite;

    @Value("${jwt.refresh.expiration}")
    private long refreshTokenExpirationMs;

    /** Builds the HTTP-only refresh-token cookie header value. */
    public String buildRefreshTokenCookie(String refreshToken) {
        return ResponseCookie.from(cookieName, refreshToken)
                .httpOnly(true)
                .secure(cookieSecure)
                .path(cookiePath)
                .sameSite(cookieSameSite)
                .maxAge(refreshTokenExpirationMs / MILLISECONDS_PER_SECOND)
                .build()
                .toString();
    }

    /** Builds an expired refresh-token cookie header value to clear browser state. */
    public String clearRefreshTokenCookie() {
        return ResponseCookie.from(cookieName, "")
                .httpOnly(true)
                .secure(cookieSecure)
                .path(cookiePath)
                .sameSite(cookieSameSite)
                .maxAge(0)
                .build()
                .toString();
    }

    /** Returns the configured refresh-token cookie name for request lookup. */
    public String getCookieName() {
        return cookieName;
    }
}
