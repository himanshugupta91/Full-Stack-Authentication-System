package com.auth.service.support;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Locale;
import java.util.Set;

/**
 * Enforces strong password requirements for registration and password updates.
 */
@Service
public class PasswordPolicyService {

    /** Validates password strength and rejects common or guessable passwords. */
    public void validate(String password, String emailHint) {
        if (!StringUtils.hasText(password)) {
            throw new IllegalArgumentException("Password is required.");
        }

        if (password.length() < 6) {
            throw new IllegalArgumentException(
                    "Password must be at least 6 characters long.");
        }

        if (password.matches(".*\\s.*")) {
            throw new IllegalArgumentException("Password must not contain spaces.");
        }

        if (!password.matches(".*[A-Za-z].*")) {
            throw new IllegalArgumentException("Password must contain at least one letter.");
        }

        if (!password.matches(".*\\d.*")) {
            throw new IllegalArgumentException("Password must contain at least one number.");
        }

        String normalized = password.toLowerCase(Locale.ROOT);
        Set<String> commonPasswordBlocklist = Set.of(
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
        if (commonPasswordBlocklist.contains(normalized)) {
            throw new IllegalArgumentException("Password is too common. Choose a less predictable password.");
        }

        String emailLocalPart = extractEmailLocalPart(emailHint);
        if (emailLocalPart != null && normalized.contains(emailLocalPart)) {
            throw new IllegalArgumentException("Password must not include your email username.");
        }
    }

    private String extractEmailLocalPart(String emailHint) {
        if (!StringUtils.hasText(emailHint)) {
            return null;
        }

        String[] emailSegments = emailHint.toLowerCase(Locale.ROOT).split("@");
        if (emailSegments.length <= 0) {
            return null;
        }

        String localPart = emailSegments[0];
        if (localPart.length() < 3) {
            return null;
        }
        String emailLocalPart = localPart;
        return emailLocalPart;
    }
}
