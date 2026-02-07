package com.auth.service;

import com.auth.dto.*;

/**
 * Service interface for authentication operations.
 */
public interface AuthService {
    /**
     * Register a new user.
     */
    MessageResponse register(RegisterRequest request);

    /**
     * Verify email with OTP.
     */
    MessageResponse verifyOtp(OtpVerifyRequest request);

    /**
     * Login user and return JWT token.
     */
    AuthResponse login(LoginRequest request);

    /**
     * Request password reset email.
     */
    MessageResponse resetPassword(ResetPasswordRequest request);

    /**
     * Update password with reset token.
     */
    MessageResponse updatePassword(UpdatePasswordRequest request);

    /**
     * Resend OTP for email verification.
     */
    MessageResponse resendOtp(String email);

    /**
     * Change password for authenticated user.
     */
    MessageResponse changePassword(String email, ChangePasswordRequest request);
}
