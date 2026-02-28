package com.auth.service.impl;

import com.auth.entity.Role;
import com.auth.entity.User;
import com.auth.exception.ResourceNotFoundException;
import com.auth.exception.TokenValidationException;
import com.auth.exception.UserAlreadyExistsException;
import com.auth.dto.AuthTokens;
import com.auth.dto.LoginRequest;
import com.auth.dto.OtpVerifyRequest;
import com.auth.dto.RegisterRequest;
import com.auth.dto.ResetPasswordRequest;
import com.auth.dto.UpdatePasswordRequest;
import com.auth.service.auth.AuthAbuseProtectionService;
import com.auth.service.AuthService;
import com.auth.service.auth.AuthTokenService;
import com.auth.service.support.EmailService;
import com.auth.service.support.OtpService;
import com.auth.service.support.PasswordPolicyService;
import com.auth.service.support.TokenHashService;
import com.auth.dto.MessageResponse;
import com.auth.mapper.UserMapper;
import com.auth.dto.ChangePasswordRequest;
import com.auth.service.RoleService;
import com.auth.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
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
@Slf4j
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserService userService;

    private final RoleService roleService;

    private final PasswordEncoder passwordEncoder;

    private final AuthenticationManager authenticationManager;

    private final AuthTokenService authTokenService;

    private final EmailService emailService;

    private final OtpService otpService;

    private final UserMapper userMapper;

    private final TokenHashService tokenHashService;

    private final PasswordPolicyService passwordPolicyService;

    private final AuthAbuseProtectionService authAbuseProtectionService;

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

        passwordPolicyService.validate(request.getPassword(), request.getEmail());

        // Create new user entity from request
        User user = userMapper.toEntity(request);
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        // Generate and set OTP for email verification
        String otp = otpService.generateOtp();
        user.setVerificationOtp(tokenHashService.hash(otp));
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
            log.warn("Failed to send OTP email during registration for {}", user.getEmail(), e);
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
        authAbuseProtectionService.guardOtpVerification(request.getEmail());

        User user = userService.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User not found!"));

        if (user.isEnabled()) {
            throw new UserAlreadyExistsException("Email already verified!");
        }

        if (!tokenHashService.matches(request.getOtp(), user.getVerificationOtp())) {
            authAbuseProtectionService.recordFailedOtp(user);
            throw new TokenValidationException("Invalid OTP!");
        }

        if (user.getOtpExpiry() == null || user.getOtpExpiry().isBefore(LocalDateTime.now())) {
            throw new TokenValidationException("OTP has expired! Please request a new one.");
        }

        // Activate user account
        user.setEnabled(true);
        user.setVerificationOtp(null);
        user.setOtpExpiry(null);
        authAbuseProtectionService.clearOtpFailures(user);
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
    public AuthTokens login(LoginRequest request) {
        authAbuseProtectionService.guardLoginAttempt(request.getEmail());

        User user = userService.findByEmail(request.getEmail())
                .orElseThrow(() -> {
                    authAbuseProtectionService.recordFailedLogin(request.getEmail());
                    return new BadCredentialsException("Invalid email or password!");
                });

        if (!user.isEnabled()) {
            throw new TokenValidationException("Please verify your email first!");
        }

        // Authenticate with Spring Security
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
        } catch (BadCredentialsException exception) {
            authAbuseProtectionService.recordFailedLogin(request.getEmail());
            throw exception;
        }

        authAbuseProtectionService.clearLoginFailures(user);

        // Successful authentication - issue access + refresh tokens
        return authTokenService.issueTokens(user);
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
        authAbuseProtectionService.guardResetPassword(request.getEmail());

        User user = userService.findByEmail(request.getEmail())
                .orElse(null);

        if (user == null) {
            // Security: Don't reveal if email exists or not
            return new MessageResponse("If an account exists with this email, a reset link will be sent.", true);
        }

        // Generate secure reset token
        String resetToken = otpService.generateResetToken();
        user.setResetToken(tokenHashService.hash(resetToken));
        user.setResetTokenExpiry(LocalDateTime.now().plusMinutes(30)); // Token valid for 30 mins
        userService.save(user);

        // Send reset email
        try {
            emailService.sendPasswordResetEmail(user.getEmail(), resetToken);
        } catch (Exception e) {
            log.warn("Failed to send password reset email for {}", user.getEmail(), e);
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
        String tokenHash = tokenHashService.hash(request.getToken());
        User user = userService.findByResetToken(tokenHash)
                .orElse(null);

        if (user == null) {
            throw new TokenValidationException("Invalid or expired reset token!");
        }

        if (user.getResetTokenExpiry() == null || user.getResetTokenExpiry().isBefore(LocalDateTime.now())) {
            throw new TokenValidationException("Reset token has expired! Please request a new one.");
        }

        passwordPolicyService.validate(request.getNewPassword(), user.getEmail());

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
        authAbuseProtectionService.guardResendOtp(email);

        User user = userService.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found!"));

        if (user.isEnabled()) {
            throw new UserAlreadyExistsException("Email already verified!");
        }

        // Generate new OTP
        String otp = otpService.generateOtp();
        user.setVerificationOtp(tokenHashService.hash(otp));
        user.setOtpExpiry(LocalDateTime.now().plusMinutes(otpExpirationMinutes));
        userService.save(user);

        // Send OTP email
        try {
            emailService.sendOtpEmail(user.getEmail(), otp);
        } catch (Exception e) {
            log.warn("Failed to send OTP email during resend for {}", user.getEmail(), e);
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
            throw new BadCredentialsException("Incorrect current password!");
        }

        passwordPolicyService.validate(request.getNewPassword(), user.getEmail());

        // Update with new encoded password
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userService.save(user);

        return new MessageResponse("Password changed successfully!", true);
    }
}
