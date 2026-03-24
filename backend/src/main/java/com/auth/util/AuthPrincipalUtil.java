package com.auth.util;

import org.springframework.security.core.Authentication;
import org.springframework.util.StringUtils;

/**
 * Utility helpers for extracting a validated authenticated principal.
 */
public final class AuthPrincipalUtil {

    private AuthPrincipalUtil() {
    }

    /**
     * Returns the authenticated email/username or throws if missing.
     */
    public static String requireAuthenticatedEmail(Authentication authentication) {
        if (authentication == null || !StringUtils.hasText(authentication.getName())) {
            throw new IllegalArgumentException("Authenticated principal is required.");
        }
        return authentication.getName().trim();
    }
}
