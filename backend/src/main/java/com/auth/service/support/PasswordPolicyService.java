package com.auth.service.support;

import org.springframework.stereotype.Service;

import java.util.Locale;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * Enforces strong password requirements for registration and password updates.
 */
@Service
public class PasswordPolicyService {

    private static final int MIN_PASSWORD_LENGTH = 6;
    private static final Pattern LETTER_PATTERN = Pattern.compile(".*[A-Za-z].*");
    private static final Pattern DIGIT_PATTERN = Pattern.compile(".*\\d.*");
    private static final Pattern WHITESPACE_PATTERN = Pattern.compile(".*\\s.*");

    private static final Set<String> COMMON_PASSWORD_BLOCKLIST = Set.of(
            "password",
            "password123",
            "password1",
            "123456",
            "12345678",
            "123456789",
            "1234567890",
            "qwerty",
            "qwerty123",
            "letmein",
            "welcome",
            "admin",
            "admin123",
            "iloveyou",
            "abc123",
            "111111",
            "123123");

    /** Validates password strength and rejects common or guessable passwords. */
    public void validate(String password, String emailHint) {
        if (password == null || password.isBlank()) {
            throw new IllegalArgumentException("Password is required.");
        }

        if (password.length() < MIN_PASSWORD_LENGTH) {
            throw new IllegalArgumentException("Password must be at least 6 characters long.");
        }

        if (WHITESPACE_PATTERN.matcher(password).matches()) {
            throw new IllegalArgumentException("Password must not contain spaces.");
        }

        if (!LETTER_PATTERN.matcher(password).matches()) {
            throw new IllegalArgumentException("Password must contain at least one letter.");
        }

        if (!DIGIT_PATTERN.matcher(password).matches()) {
            throw new IllegalArgumentException("Password must contain at least one number.");
        }

        String normalized = password.toLowerCase(Locale.ROOT);
        if (COMMON_PASSWORD_BLOCKLIST.contains(normalized)) {
            throw new IllegalArgumentException("Password is too common. Choose a less predictable password.");
        }

        if (emailHint != null && !emailHint.isBlank()) {
            String localPart = emailHint.toLowerCase(Locale.ROOT).split("@")[0];
            if (localPart.length() >= 3 && normalized.contains(localPart)) {
                throw new IllegalArgumentException("Password must not include your email username.");
            }
        }
    }
}
