package com.auth.service.support;

import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.Base64;

/**
 * Generates cryptographically secure OTP codes and password-reset tokens.
 */
@Service
public class OtpService {

    private final SecureRandom secureRandom = new SecureRandom();

    /**
     * Generates a 6-digit numeric OTP code.
     */
    public String generateOtp() {
        return String.valueOf(100_000 + secureRandom.nextInt(900_000));
    }

    /**
     * Generates a high-entropy, URL-safe password-reset token.
     */
    public String generateResetToken() {
        byte[] randomBytes = new byte[32];
        secureRandom.nextBytes(randomBytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);
    }
}
