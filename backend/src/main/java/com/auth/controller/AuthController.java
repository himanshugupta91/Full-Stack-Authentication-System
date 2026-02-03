package com.auth.controller;

import com.auth.dto.*;
import com.auth.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for authentication endpoints.
 * Handles registration, login, OTP verification, and password reset.
 */
@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = { "http://localhost:5173", "http://localhost:3000" })
public class AuthController {

    @Autowired
    private UserService userService;

    /**
     * Register a new user.
     * POST /api/auth/register
     */
    @PostMapping("/register")
    public ResponseEntity<MessageResponse> register(@Valid @RequestBody RegisterRequest request) {
        MessageResponse response = userService.register(request);
        return ResponseEntity.ok(response);
    }

    /**
     * Verify email with OTP.
     * POST /api/auth/verify-otp
     */
    @PostMapping("/verify-otp")
    public ResponseEntity<MessageResponse> verifyOtp(@Valid @RequestBody OtpVerifyRequest request) {
        MessageResponse response = userService.verifyOtp(request);
        return ResponseEntity.ok(response);
    }

    /**
     * Login user and return JWT token.
     * POST /api/auth/login
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
        try {
            AuthResponse response = userService.login(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage(), false));
        }
    }

    /**
     * Request password reset email.
     * POST /api/auth/reset-password
     */
    @PostMapping("/reset-password")
    public ResponseEntity<MessageResponse> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        MessageResponse response = userService.resetPassword(request);
        return ResponseEntity.ok(response);
    }

    /**
     * Update password with reset token.
     * POST /api/auth/update-password
     */
    @PostMapping("/update-password")
    public ResponseEntity<MessageResponse> updatePassword(@Valid @RequestBody UpdatePasswordRequest request) {
        MessageResponse response = userService.updatePassword(request);
        return ResponseEntity.ok(response);
    }

    /**
     * Resend OTP for email verification.
     * POST /api/auth/resend-otp
     */
    @PostMapping("/resend-otp")
    public ResponseEntity<MessageResponse> resendOtp(@RequestParam String email) {
        MessageResponse response = userService.resendOtp(email);
        return ResponseEntity.ok(response);
    }
}
