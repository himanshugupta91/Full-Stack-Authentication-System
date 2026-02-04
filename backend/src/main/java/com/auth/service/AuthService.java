package com.auth.service;

import com.auth.dto.*;
import com.auth.entity.Role;
import com.auth.entity.User;
import com.auth.mapper.UserMapper;
import com.auth.repository.RoleRepository;
import com.auth.repository.UserRepository;
import com.auth.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * Service for authentication operations.
 */
@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private EmailService emailService;

    @Autowired
    private OtpService otpService;

    @Autowired
    private UserMapper userMapper;

    @Value("${otp.expiration.minutes:5}")
    private int otpExpirationMinutes;

    /**
     * Register a new user and send OTP for email verification.
     */
    @Transactional
    public MessageResponse register(RegisterRequest request) {
        // Check if email already exists
        if (userRepository.existsByEmail(request.getEmail())) {
            return new MessageResponse("Email already registered!", false);
        }

        // Create new user
        User user = userMapper.toEntity(request);
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        // Generate and set OTP
        String otp = otpService.generateOtp();
        user.setVerificationOtp(otp);
        user.setOtpExpiry(LocalDateTime.now().plusMinutes(otpExpirationMinutes));

        // Assign default USER role
        Set<Role> roles = new HashSet<>();
        Role userRole = roleRepository.findByName(Role.RoleName.ROLE_USER)
                .orElseGet(() -> {
                    Role newRole = new Role();
                    newRole.setName(Role.RoleName.ROLE_USER);
                    return roleRepository.save(newRole);
                });
        roles.add(userRole);
        user.setRoles(roles);

        userRepository.save(user);

        // Send OTP email
        try {
            emailService.sendOtpEmail(user.getEmail(), otp);
        } catch (Exception e) {
            // Log the error but don't fail registration
            System.err.println("Failed to send OTP email: " + e.getMessage());
        }

        return new MessageResponse("Registration successful! Please check your email for OTP verification.", true);
    }

    /**
     * Verify user email with OTP.
     */
    @Transactional
    public MessageResponse verifyOtp(OtpVerifyRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElse(null);

        if (user == null) {
            return new MessageResponse("User not found!", false);
        }

        if (user.isEnabled()) {
            return new MessageResponse("Email already verified!", false);
        }

        if (user.getVerificationOtp() == null || !user.getVerificationOtp().equals(request.getOtp())) {
            return new MessageResponse("Invalid OTP!", false);
        }

        if (user.getOtpExpiry().isBefore(LocalDateTime.now())) {
            return new MessageResponse("OTP has expired! Please request a new one.", false);
        }

        // Verify user
        user.setEnabled(true);
        user.setVerificationOtp(null);
        user.setOtpExpiry(null);
        userRepository.save(user);

        return new MessageResponse("Email verified successfully! You can now login.", true);
    }

    /**
     * Authenticate user and return JWT token.
     */
    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found!"));

        if (!user.isEnabled()) {
            throw new RuntimeException("Please verify your email first!");
        }

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));

        String jwt = jwtUtil.generateToken(authentication);

        return userMapper.toAuthResponse(user, jwt);
    }

    /**
     * Request password reset - sends email with reset token.
     */
    @Transactional
    public MessageResponse resetPassword(ResetPasswordRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElse(null);

        if (user == null) {
            // Don't reveal if email exists
            return new MessageResponse("If an account exists with this email, a reset link will be sent.", true);
        }

        // Generate reset token
        String resetToken = otpService.generateResetToken();
        user.setResetToken(resetToken);
        user.setResetTokenExpiry(LocalDateTime.now().plusMinutes(30));
        userRepository.save(user);

        // Send reset email
        try {
            emailService.sendPasswordResetEmail(user.getEmail(), resetToken);
        } catch (Exception e) {
            System.err.println("Failed to send reset email: " + e.getMessage());
        }

        return new MessageResponse("If an account exists with this email, a reset link will be sent.", true);
    }

    /**
     * Update password using reset token.
     */
    @Transactional
    public MessageResponse updatePassword(UpdatePasswordRequest request) {
        User user = userRepository.findByResetToken(request.getToken())
                .orElse(null);

        if (user == null) {
            return new MessageResponse("Invalid or expired reset token!", false);
        }

        if (user.getResetTokenExpiry().isBefore(LocalDateTime.now())) {
            return new MessageResponse("Reset token has expired! Please request a new one.", false);
        }

        // Update password
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        user.setResetToken(null);
        user.setResetTokenExpiry(null);
        userRepository.save(user);

        return new MessageResponse("Password updated successfully! You can now login.", true);
    }

    /**
     * Resend OTP for email verification.
     */
    @Transactional
    public MessageResponse resendOtp(String email) {
        User user = userRepository.findByEmail(email)
                .orElse(null);

        if (user == null) {
            return new MessageResponse("User not found!", false);
        }

        if (user.isEnabled()) {
            return new MessageResponse("Email already verified!", false);
        }

        // Generate new OTP
        String otp = otpService.generateOtp();
        user.setVerificationOtp(otp);
        user.setOtpExpiry(LocalDateTime.now().plusMinutes(otpExpirationMinutes));
        userRepository.save(user);

        // Send OTP email
        try {
            emailService.sendOtpEmail(user.getEmail(), otp);
        } catch (Exception e) {
            System.err.println("Failed to send OTP email: " + e.getMessage());
        }

        return new MessageResponse("OTP sent successfully! Please check your email.", true);
    }

    /**
     * Change password for authenticated user.
     */
    @Transactional
    public MessageResponse changePassword(String email, ChangePasswordRequest request) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found!"));

        // Check if current password is correct
        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            return new MessageResponse("Incorrect current password!", false);
        }

        // Update password
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        return new MessageResponse("Password changed successfully!", true);
    }
}
