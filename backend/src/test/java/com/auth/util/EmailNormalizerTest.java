package com.auth.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@DisplayName("EmailNormalizer")
class EmailNormalizerTest {

    @Test
    @DisplayName("normalizeOrNull: trims and lower-cases valid email")
    void givenMixedCaseEmail_whenNormalizeOrNull_thenReturnsTrimmedLowercaseEmail() {
        // Act
        String normalized = EmailNormalizer.normalizeOrNull("  Alice.Example@Gmail.COM  ");

        // Assert
        assertEquals("alice.example@gmail.com", normalized);
    }

    @Test
    @DisplayName("normalizeOrNull: blank email returns null")
    void givenBlankEmail_whenNormalizeOrNull_thenReturnsNull() {
        // Act + Assert
        assertNull(EmailNormalizer.normalizeOrNull("   "));
    }

    @Test
    @DisplayName("normalizeOr: blank email returns provided fallback")
    void givenBlankEmail_whenNormalizeOr_thenReturnsFallback() {
        // Act
        String normalized = EmailNormalizer.normalizeOr(" ", "unknown-email");

        // Assert
        assertEquals("unknown-email", normalized);
    }
}
