package com.auth.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@DisplayName("EmailNormalizer")
class EmailNormalizerTest {

    @Test
    @DisplayName("normalizeOrNull: trims and lower-cases valid email")
    void normalizeOrNull_whenEmailPresent_returnsNormalizedValue() {
        String normalized = EmailNormalizer.normalizeOrNull("  Alice.Example@Gmail.COM  ");

        assertEquals("alice.example@gmail.com", normalized);
    }

    @Test
    @DisplayName("normalizeOrNull: blank email returns null")
    void normalizeOrNull_whenBlank_returnsNull() {
        assertNull(EmailNormalizer.normalizeOrNull("   "));
    }

    @Test
    @DisplayName("normalizeOr: blank email returns provided fallback")
    void normalizeOr_whenBlank_returnsFallback() {
        String normalized = EmailNormalizer.normalizeOr(" ", "unknown-email");

        assertEquals("unknown-email", normalized);
    }
}
