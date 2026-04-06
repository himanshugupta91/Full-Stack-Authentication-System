package com.auth.security;

import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.net.URI;
import java.util.Locale;

/**
 * Builds HTTP-only cookie headers for secure refresh-token handling.
 *
 * <p>Supports auto-detection of the {@code Secure} flag and {@code SameSite}
 * policy based on the incoming request's protocol and origin, with optional
 * static overrides via application properties.
 */
@Component
public class RefreshTokenCookieService {

    @Getter
    @Value("${auth.refresh-token.cookie-name:refreshToken}")
    private String cookieName;

    @Value("${auth.refresh-token.cookie-path:/api/v1/auth}")
    private String cookiePath;

    @Value("${auth.refresh-token.cookie-secure:auto}")
    private String cookieSecureSetting;

    @Value("${auth.refresh-token.cookie-same-site:auto}")
    private String cookieSameSiteSetting;

    @Value("${auth.refresh-token.cookie-domain:}")
    private String cookieDomain;

    @Value("${app.frontend-url:http://localhost:5173}")
    private String frontendUrl;

    @Value("${jwt.refresh.expiration}")
    private long refreshTokenExpirationMs;

    @PostConstruct
    void validateCookieConfiguration() {
        String sameSite = normalizeSetting(cookieSameSiteSetting);
        String secure = normalizeSetting(cookieSecureSetting);
        if ("none".equals(sameSite) && "false".equals(secure)) {
            throw new IllegalStateException(
                    "auth.refresh-token.cookie-secure must be true when auth.refresh-token.cookie-same-site=None.");
        }
    }

    /** Builds the HTTP-only refresh-token {@code Set-Cookie} header value. */
    public String buildRefreshTokenCookie(String refreshToken) {
        return buildRefreshTokenCookie(refreshToken, null);
    }

    /** Builds the HTTP-only refresh-token {@code Set-Cookie} header value, using request-aware defaults. */
    public String buildRefreshTokenCookie(String refreshToken, HttpServletRequest request) {
        CookiePolicy policy = resolveCookiePolicy(request);
        return cookieBuilder(refreshToken, policy)
                .maxAge(refreshTokenExpirationMs / 1000L)
                .build()
                .toString();
    }

    /** Builds an expired {@code Set-Cookie} header to clear the browser's refresh-token cookie. */
    public String clearRefreshTokenCookie() {
        return clearRefreshTokenCookie(null);
    }

    /** Builds an expired {@code Set-Cookie} header to clear the browser's refresh-token cookie. */
    public String clearRefreshTokenCookie(HttpServletRequest request) {
        CookiePolicy policy = resolveCookiePolicy(request);
        return cookieBuilder("", policy).maxAge(0).build().toString();
    }

    // ── Private helpers ───────────────────────────────────────────────────────
    /**
     * Executes cookie builder logic.
     */

    private ResponseCookie.ResponseCookieBuilder cookieBuilder(String value, CookiePolicy policy) {
        ResponseCookie.ResponseCookieBuilder builder = ResponseCookie.from(cookieName, value)
                .httpOnly(true)
                .secure(policy.secure())
                .path(cookiePath)
                .sameSite(policy.sameSite());

        if (StringUtils.hasText(cookieDomain)) {
            builder.domain(cookieDomain.trim());
        }
        return builder;
    }
    /**
     * Resolves cookie policy.
     */

    private CookiePolicy resolveCookiePolicy(HttpServletRequest request) {
        boolean secure = resolveSecureFlag(request);
        boolean autoSameSite = "auto".equals(normalizeSetting(cookieSameSiteSetting));
        String sameSite = autoSameSite ? resolveAutoSameSite(request) : normalizeSameSite(cookieSameSiteSetting);

        if ("None".equalsIgnoreCase(sameSite) && !secure) {
            if (autoSameSite) {
                // Browsers reject SameSite=None without Secure; fall back safely for local HTTP.
                sameSite = "Lax";
            } else {
                throw new IllegalStateException(
                        "auth.refresh-token.cookie-secure must be true when auth.refresh-token.cookie-same-site=None.");
            }
        }
        return new CookiePolicy(secure, sameSite);
    }
    /**
     * Resolves secure flag.
     */

    private boolean resolveSecureFlag(HttpServletRequest request) {
        String normalized = normalizeSetting(cookieSecureSetting);
        return switch (normalized) {
            case "auto" -> isSecureRequest(request);
            case "true" -> true;
            case "false" -> false;
            default -> throw new IllegalStateException(
                    "auth.refresh-token.cookie-secure must be true, false, or auto.");
        };
    }
    /**
     * Resolves auto same site.
     */

    private String resolveAutoSameSite(HttpServletRequest request) {
        return isCrossSiteRequest(request) ? "None" : "Lax";
    }
    /**
     * Checks whether cross site request.
     */

    private boolean isCrossSiteRequest(HttpServletRequest request) {
        if (request == null) return false;

        String secFetchSite = normalizeSetting(request.getHeader("Sec-Fetch-Site"));
        if ("cross-site".equals(secFetchSite)) {
            return true;
        }
        if ("same-origin".equals(secFetchSite) || "same-site".equals(secFetchSite)) {
            return false;
        }

        String requestHost = resolveRequestHost(request);
        String requestScheme = resolveRequestScheme(request);

        URI origin = parseUri(request.getHeader("Origin"));
        if (origin != null) {
            return isDifferentSite(origin.getHost(), origin.getScheme(), requestHost, requestScheme);
        }

        URI referer = parseUri(request.getHeader("Referer"));
        if (referer != null) {
            return isDifferentSite(referer.getHost(), referer.getScheme(), requestHost, requestScheme);
        }

        URI frontend = parseUri(frontendUrl);
        if (frontend != null) {
            return isDifferentSite(frontend.getHost(), frontend.getScheme(), requestHost, requestScheme);
        }

        return false;
    }
    /**
     * Resolves request host.
     */

    private String resolveRequestHost(HttpServletRequest request) {
        String host = firstHeaderToken(request.getHeader("X-Forwarded-Host"));
        if (!StringUtils.hasText(host)) {
            host = request.getServerName();
        }
        return stripPort(host);
    }
    /**
     * Resolves request scheme.
     */

    private String resolveRequestScheme(HttpServletRequest request) {
        String forwarded = firstHeaderToken(request.getHeader("X-Forwarded-Proto"));
        if (StringUtils.hasText(forwarded)) return forwarded.toLowerCase(Locale.ROOT);
        return request.isSecure() ? "https" : request.getScheme();
    }
    /**
     * Checks whether secure request.
     */

    private boolean isSecureRequest(HttpServletRequest request) {
        return request != null && "https".equalsIgnoreCase(resolveRequestScheme(request));
    }
    /**
     * Normalizes same site.
     */

    private String normalizeSameSite(String raw) {
        return switch (normalizeSetting(raw)) {
            case "strict" -> "Strict";
            case "lax" -> "Lax";
            case "none" -> "None";
            default -> throw new IllegalStateException(
                    "auth.refresh-token.cookie-same-site must be strict, lax, none, or auto.");
        };
    }
    /**
     * Normalizes setting.
     */

    private String normalizeSetting(String value) {
        return StringUtils.hasText(value) ? value.trim().toLowerCase(Locale.ROOT) : "";
    }
    /**
     * Parses uri.
     */

    private URI parseUri(String value) {
        if (!StringUtils.hasText(value)) {
            return null;
        }
        try {
            return URI.create(value.trim());
        } catch (IllegalArgumentException ex) {
            return null;
        }
    }
    /**
     * Checks whether different site.
     */

    private boolean isDifferentSite(String sourceHost, String sourceScheme, String targetHost, String targetScheme) {
        if (!StringUtils.hasText(sourceHost) || !StringUtils.hasText(targetHost)) {
            return false;
        }

        boolean sameHost = sourceHost.equalsIgnoreCase(targetHost);
        boolean sameScheme = StringUtils.hasText(sourceScheme)
                && StringUtils.hasText(targetScheme)
                && sourceScheme.equalsIgnoreCase(targetScheme);
        return !(sameHost && sameScheme);
    }
    /**
     * Executes first header token logic.
     */

    private String firstHeaderToken(String headerValue) {
        return StringUtils.hasText(headerValue) ? headerValue.split(",")[0].trim() : null;
    }
    /**
     * Executes strip port logic.
     */

    private String stripPort(String host) {
        if (!StringUtils.hasText(host)) return host;
        int colonIndex = host.indexOf(':');
        return colonIndex < 0 ? host : host.substring(0, colonIndex);
    }

    private record CookiePolicy(boolean secure, String sameSite) {}
}
