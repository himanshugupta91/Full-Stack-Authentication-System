package com.auth.service.support;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Locale;
import java.util.Set;

/**
 * Enforces strong password requirements for registration and password updates.
 * Validates length, character diversity, common-password blocklist, and prevents
 * use of the account's own email username as a password.
 */
@Service
public class PasswordPolicyService {

    /**
     * Common passwords that are trivially guessable and must be rejected.
     * This list is checked case-insensitively.
     */
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
            "123123"
    );

    /**
     * Validates password strength against the configured policy rules.
     *
     * @param password  the candidate password to validate
     * @param emailHint the owner's email address, used to reject passwords that
     *                  contain the email's local part
     * @throws IllegalArgumentException if the password violates any policy rule
     */
    public void validate(String password, String emailHint) {
        if (!StringUtils.hasText(password)) {
            throw new IllegalArgumentException("Password is required.");
        }

        if (password.length() < 6) {
            throw new IllegalArgumentException("Password must be at least 6 characters long.");
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
        if (COMMON_PASSWORD_BLOCKLIST.contains(normalized)) {
            throw new IllegalArgumentException("Password is too common. Choose a less predictable password.");
        }

        String emailLocalPart = extractEmailLocalPart(emailHint);
        if (emailLocalPart != null && normalized.contains(emailLocalPart)) {
            throw new IllegalArgumentException("Password must not include your email username.");
        }
    }

    /**
     * Extracts the local part (before {@code @}) of an email address for use in
     * the email-similarity check. Returns {@code null} if the local part is too
     * short to be meaningful (fewer than 3 characters).
     */
    private String extractEmailLocalPart(String emailHint) {
        if (!StringUtils.hasText(emailHint)) {
            return null;
        }

        String[] parts = emailHint.toLowerCase(Locale.ROOT).split("@");
        if (parts.length == 0) {
            return null;
        }

        String localPart = parts[0];
        return localPart.length() >= 3 ? localPart : null;
    }
}
