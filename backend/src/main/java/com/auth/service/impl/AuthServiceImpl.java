package com.auth.service.impl;

import com.auth.dto.*;
import com.auth.entity.Role;
import com.auth.entity.User;
import com.auth.exception.ResourceNotFoundException;
import com.auth.exception.TokenValidationException;
import com.auth.exception.UserAlreadyExistsException;
import com.auth.mapper.UserMapper;
import com.auth.security.JwtUtil;
import com.auth.service.*;
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
 * Implementation of AuthService.
 */
@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private UserService userService;

    @Autowired
    private RoleService roleService;

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
     * Registers a new user with the provided details.
     * Checks for existing email, encodes password, generates OTP, and sends
     * verification email.
     *
     * @param request The registration request containing user details.
     * @return MessageResponse indicating success status and message.
     * @throws UserAlreadyExistsException if the email is already registered.
     */
    @Override
    @Transactional
    public MessageResponse register(RegisterRequest request) {
        // Check if email already exists
        if (userService.existsByEmail(request.getEmail())) {
            throw new UserAlreadyExistsException("Email already registered!");
        }

        // Create new user entity from request
        User user = userMapper.toEntity(request);
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        // Generate and set OTP for email verification
        String otp = otpService.generateOtp();
        user.setVerificationOtp(otp);
        user.setOtpExpiry(LocalDateTime.now().plusMinutes(otpExpirationMinutes));

        // Assign default USER role
        Set<Role> roles = new HashSet<>();
        Role userRole = roleService.findOrCreateRole(Role.RoleName.ROLE_USER);
        roles.add(userRole);
        user.setRoles(roles);

        userService.save(user);

        // Send OTP email asynchronously (handled by EmailService)
        try {
            emailService.sendOtpEmail(user.getEmail(), otp);
        } catch (Exception e) {
            // Log error but allow registration to complete; user can resend OTP later
            System.err.println("Failed to send OTP email: " + e.getMessage());
        }

        return new MessageResponse("Registration successful! Please check your email for OTP verification.", true);
    }

    /**
     * Verifies the email address using the provided OTP.
     *
     * @param request The request containing email and OTP.
     * @return MessageResponse indicating success.
     * @throws ResourceNotFoundException  if user not found.
     * @throws UserAlreadyExistsException if email is already verified.
     * @throws TokenValidationException   if OTP is invalid or expired.
     */
    @Override
    @Transactional
    public MessageResponse verifyOtp(OtpVerifyRequest request) {
        User user = userService.findByEmail(request.getEmail())
                .orElse(null);

        if (user == null) {
            throw new ResourceNotFoundException("User not found!");
        }

        if (user.isEnabled()) {
            throw new UserAlreadyExistsException("Email already verified!");
        }

        if (user.getVerificationOtp() == null || !user.getVerificationOtp().equals(request.getOtp())) {
            throw new TokenValidationException("Invalid OTP!");
        }

        if (user.getOtpExpiry().isBefore(LocalDateTime.now())) {
            throw new TokenValidationException("OTP has expired! Please request a new one.");
        }

        // Activate user account
        user.setEnabled(true);
        user.setVerificationOtp(null);
        user.setOtpExpiry(null);
        userService.save(user);

        return new MessageResponse("Email verified successfully! You can now login.", true);
    }

    /**
     * Authenticates a user and generates a JWT token.
     *
     * @param request The login request containing email and password.
     * @return AuthResponse containing user details and JWT token.
     * @throws org.springframework.security.authentication.BadCredentialsException if
     *                                                                             credentials
     *                                                                             are
     *                                                                             invalid.
     * @throws TokenValidationException                                            if
     *                                                                             email
     *                                                                             is
     *                                                                             not
     *                                                                             verified.
     */
    @Override
    public AuthResponse login(LoginRequest request) {
        User user = userService.findByEmail(request.getEmail())
                .orElseThrow(() -> new org.springframework.security.authentication.BadCredentialsException(
                        "Invalid email or password!"));

        if (!user.isEnabled()) {
            throw new TokenValidationException("Please verify your email first!");
        }

        // Authenticate with Spring Security
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));

        // Generate JWT Token
        String jwt = jwtUtil.generateToken(authentication);

        return userMapper.toAuthResponse(user, jwt);
    }

    /**
     * Initiates the password reset process by generating a token and sending an
     * email.
     *
     * @param request The request containing the user's email.
     * @return MessageResponse indicating the email was sent (generic message for
     *         security).
     */
    @Override
    @Transactional
    public MessageResponse resetPassword(ResetPasswordRequest request) {
        User user = userService.findByEmail(request.getEmail())
                .orElse(null);

        if (user == null) {
            // Security: Don't reveal if email exists or not
            return new MessageResponse("If an account exists with this email, a reset link will be sent.", true);
        }

        // Generate secure reset token
        String resetToken = otpService.generateResetToken();
        user.setResetToken(resetToken);
        user.setResetTokenExpiry(LocalDateTime.now().plusMinutes(30)); // Token valid for 30 mins
        userService.save(user);

        // Send reset email
        try {
            emailService.sendPasswordResetEmail(user.getEmail(), resetToken);
        } catch (Exception e) {
            System.err.println("Failed to send reset email: " + e.getMessage());
        }

        return new MessageResponse("If an account exists with this email, a reset link will be sent.", true);
    }

    /**
     * Updates the user's password using a valid reset token.
     *
     * @param request The request containing the reset token and new password.
     * @return MessageResponse indicating success.
     * @throws TokenValidationException if token is invalid or expired.
     */
    @Override
    @Transactional
    public MessageResponse updatePassword(UpdatePasswordRequest request) {
        User user = userService.findByResetToken(request.getToken())
                .orElse(null);

        if (user == null) {
            throw new TokenValidationException("Invalid or expired reset token!");
        }

        if (user.getResetTokenExpiry().isBefore(LocalDateTime.now())) {
            throw new TokenValidationException("Reset token has expired! Please request a new one.");
        }

        // Update password and clear reset token
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        user.setResetToken(null);
        user.setResetTokenExpiry(null);
        userService.save(user);

        return new MessageResponse("Password updated successfully! You can now login.", true);
    }

    /**
     * Resends the OTP for email verification.
     *
     * @param email The email address to resend OTP to.
     * @return MessageResponse indicating success.
     * @throws ResourceNotFoundException  if user not found.
     * @throws UserAlreadyExistsException if email is already verified.
     */
    @Override
    @Transactional
    public MessageResponse resendOtp(String email) {
        User user = userService.findByEmail(email)
                .orElse(null);

        if (user == null) {
            throw new ResourceNotFoundException("User not found!");
        }

        if (user.isEnabled()) {
            throw new UserAlreadyExistsException("Email already verified!");
        }

        // Generate new OTP
        String otp = otpService.generateOtp();
        user.setVerificationOtp(otp);
        user.setOtpExpiry(LocalDateTime.now().plusMinutes(otpExpirationMinutes));
        userService.save(user);

        // Send OTP email
        try {
            emailService.sendOtpEmail(user.getEmail(), otp);
        } catch (Exception e) {
            System.err.println("Failed to send OTP email: " + e.getMessage());
        }

        return new MessageResponse("OTP sent successfully! Please check your email.", true);
    }

    /**
     * Changes the authenticated user's password.
     *
     * @param email   The email of the authenticated user.
     * @param request The request containing current and new password.
     * @return MessageResponse indicating success.
     * @throws ResourceNotFoundException                                           if
     *                                                                             user
     *                                                                             not
     *                                                                             found.
     * @throws org.springframework.security.authentication.BadCredentialsException if
     *                                                                             current
     *                                                                             password
     *                                                                             is
     *                                                                             incorrect.
     */
    @Override
    @Transactional
    public MessageResponse changePassword(String email, ChangePasswordRequest request) {
        User user = userService.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found!"));

        // Verify current password
        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new org.springframework.security.authentication.BadCredentialsException(
                    "Incorrect current password!");
        }

        // Update with new encoded password
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userService.save(user);

        return new MessageResponse("Password changed successfully!", true);
    }
}
