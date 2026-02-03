package com.auth.service;

import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.UUID;

/**
 * Service for generating OTP codes and reset tokens.
 */
@Service
public class OtpService {

    private static final SecureRandom random = new SecureRandom();

    /**
     * Generate a 6-digit OTP code.
     */
    public String generateOtp() {
        int otp = 100000 + random.nextInt(900000);
        return String.valueOf(otp);
    }

    /**
     * Generate a unique password reset token.
     */
    public String generateResetToken() {
        return UUID.randomUUID().toString();
    }
}
