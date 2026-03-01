package com.auth.service.support;

import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.Base64;

/**
 * Service for generating OTP codes and reset tokens.
 */
@Service
public class OtpService {

    private SecureRandom secureRandom = new SecureRandom();

    /**
     * Generate a 6-digit OTP code.
     */
    public String generateOtp() {
        int otp = 100000 + secureRandom.nextInt(900000);
        String otpValue = String.valueOf(otp);
        return otpValue;
    }

    /**
     * Generate a unique password reset token.
     */
    public String generateResetToken() {
        byte[] randomBytes = new byte[32];
        secureRandom.nextBytes(randomBytes);
        String resetToken = Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);
        return resetToken;
    }
}
