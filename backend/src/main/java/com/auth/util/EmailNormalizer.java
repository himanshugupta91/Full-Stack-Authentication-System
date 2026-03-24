package com.auth.util;

import org.springframework.util.StringUtils;

import java.util.Locale;

/**
 * Shared email canonicalization utility used across authentication layers.
 */
public final class EmailNormalizer {

    private EmailNormalizer() {
    }

    /**
     * Trims and lower-cases an email address.
     *
     * @return normalized email, or {@code null} if input is blank
     */
    public static String normalizeOrNull(String email) {
        if (!StringUtils.hasText(email)) {
            return null;
        }
        return email.trim().toLowerCase(Locale.ROOT);
    }

    /**
     * Trims and lower-cases an email address, falling back when blank.
     */
    public static String normalizeOr(String email, String fallback) {
        String normalized = normalizeOrNull(email);
        return normalized != null ? normalized : fallback;
    }
}
