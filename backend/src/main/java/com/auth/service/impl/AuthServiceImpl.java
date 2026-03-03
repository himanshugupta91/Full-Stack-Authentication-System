package com.auth.service.impl;

import com.auth.entity.Role;
import com.auth.entity.User;
import com.auth.exception.ResourceNotFoundException;
import com.auth.exception.TokenValidationException;
import com.auth.exception.UserAlreadyExistsException;
import com.auth.dto.response.AuthTokens;
import com.auth.dto.request.LoginRequest;
import com.auth.dto.request.OtpVerifyRequest;
import com.auth.dto.request.RegisterRequest;
import com.auth.dto.request.ResetPasswordRequest;
import com.auth.dto.request.UpdatePasswordRequest;
import com.auth.service.auth.AuthAbuseProtectionService;
import com.auth.service.AuthService;
import com.auth.service.auth.AuthTokenService;
import com.auth.service.support.EmailService;
import com.auth.service.support.OtpService;
import com.auth.service.support.PasswordPolicyService;
import com.auth.service.support.TokenHashService;
import com.auth.dto.response.MessageResponse;
import com.auth.mapper.UserMapper;
import com.auth.dto.request.ChangePasswordRequest;
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
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.Locale;
import java.util.Optional;
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

    @Value("${auth.reset-token.expiration.minutes:30}")
    private int resetTokenExpirationMinutes;

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
        String normalizedEmail = normalizeEmail(request.getEmail());
        if (userService.existsByEmail(normalizedEmail)) {
            throw new UserAlreadyExistsException("Email already registered!");
        }

        passwordPolicyService.validate(request.getPassword(), normalizedEmail);

        User user = userMapper.toEntity(request);
        user.setEmail(normalizedEmail);
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        String otp = generateAndStoreVerificationOtp(user);
        assignDefaultUserRole(user);

        userService.save(user);
        sendOtpEmailSafely(user, otp, "registration");

        MessageResponse response = new MessageResponse(
                "Registration successful! Please check your email for OTP verification.", true);
        return response;
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
        String normalizedEmail = normalizeEmail(request.getEmail());
        authAbuseProtectionService.guardOtpVerification(normalizedEmail);

        User user = getExistingUserByEmail(normalizedEmail);
        ensureEmailNotYetVerified(user);
        ensureOtpMatches(user, request.getOtp());
        ensureTokenNotExpired(user.getOtpExpiry(), "OTP has expired! Please request a new one.");

        user.setEnabled(true);
        user.setVerificationOtp(null);
        user.setOtpExpiry(null);
        authAbuseProtectionService.clearOtpFailures(user);
        userService.save(user);

        MessageResponse response = new MessageResponse("Email verified successfully! You can now login.", true);
        return response;
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
        String normalizedEmail = normalizeEmail(request.getEmail());
        authAbuseProtectionService.guardLoginAttempt(normalizedEmail);

        User user = userService.findByEmail(normalizedEmail)
                .orElseThrow(() -> {
                    authAbuseProtectionService.recordFailedLogin(normalizedEmail);
                    return new BadCredentialsException("Invalid email or password!");
                });

        authenticateLoginCredentials(normalizedEmail, request.getPassword());
        authAbuseProtectionService.clearLoginFailures(user);

        AuthTokens tokens = authTokenService.issueTokens(user);
        return tokens;
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
        String normalizedEmail = normalizeEmail(request.getEmail());
        authAbuseProtectionService.guardResetPassword(normalizedEmail);
        MessageResponse response = new MessageResponse(
                "If an account exists with this email, a reset link will be sent.",
                true);

        User user = userService.findByEmail(normalizedEmail).orElse(null);
        if (user == null) {
            return response;
        }

        String resetToken = generateAndStoreResetToken(user);
        userService.save(user);
        sendResetEmailSafely(user, resetToken);

        return response;
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
        String resetTokenHash = tokenHashService.hash(request.getToken());
        User user = userService.findByResetToken(resetTokenHash)
                .orElseThrow(() -> new TokenValidationException("Invalid or expired reset token!"));

        ensureTokenNotExpired(user.getResetTokenExpiry(), "Reset token has expired! Please request a new one.");

        passwordPolicyService.validate(request.getNewPassword(), user.getEmail());

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        clearStoredResetToken(user);
        userService.save(user);

        MessageResponse response = new MessageResponse("Password updated successfully! You can now login.", true);
        return response;
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
        String normalizedEmail = normalizeEmail(email);
        authAbuseProtectionService.guardResendOtp(normalizedEmail);

        User user = getExistingUserByEmail(normalizedEmail);
        ensureEmailNotYetVerified(user);

        String otp = generateAndStoreVerificationOtp(user);
        userService.save(user);
        sendOtpEmailSafely(user, otp, "resend");

        MessageResponse response = new MessageResponse("OTP sent successfully! Please check your email.", true);
        return response;
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
        User user = getExistingUserByEmail(email);

        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new BadCredentialsException("Incorrect current password!");
        }

        passwordPolicyService.validate(request.getNewPassword(), user.getEmail());

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userService.save(user);

        MessageResponse response = new MessageResponse("Password changed successfully!", true);
        return response;
    }

    private User getExistingUserByEmail(String email) {
        String normalizedEmail = normalizeEmail(email);
        Optional<User> userOpt = userService.findByEmail(normalizedEmail);
        User user = userOpt.orElseThrow(() -> new ResourceNotFoundException("User not found!"));
        return user;
    }

    private void ensureEmailNotYetVerified(User user) {
        if (user.isEnabled()) {
            throw new UserAlreadyExistsException("Email already verified!");
        }
    }

    private void ensureOtpMatches(User user, String rawOtp) {
        if (tokenHashService.matches(rawOtp, user.getVerificationOtp())) {
            return;
        }
        authAbuseProtectionService.recordFailedOtp(user);
        throw new TokenValidationException("Invalid OTP!");
    }

    private void ensureTokenNotExpired(LocalDateTime expiry, String errorMessage) {
        if (isTokenExpired(expiry)) {
            throw new TokenValidationException(errorMessage);
        }
    }

    private String generateAndStoreVerificationOtp(User user) {
        String otp = otpService.generateOtp();
        String otpHash = tokenHashService.hash(otp);
        LocalDateTime expiry = LocalDateTime.now().plusMinutes(otpExpirationMinutes);

        user.setVerificationOtp(otpHash);
        user.setOtpExpiry(expiry);
        return otp;
    }

    private void assignDefaultUserRole(User user) {
        Role userRole = roleService.findOrCreateRole(Role.RoleName.ROLE_USER);
        user.setRoles(Set.of(userRole));
    }

    private String generateAndStoreResetToken(User user) {
        String resetToken = otpService.generateResetToken();
        String resetTokenHash = tokenHashService.hash(resetToken);
        LocalDateTime expiry = LocalDateTime.now().plusMinutes(resetTokenExpirationMinutes);

        user.setResetToken(resetTokenHash);
        user.setResetTokenExpiry(expiry);
        return resetToken;
    }

    private boolean isTokenExpired(LocalDateTime expiry) {
        boolean tokenExpired = expiry == null || expiry.isBefore(LocalDateTime.now());
        return tokenExpired;
    }

    private void sendOtpEmailSafely(User user, String otp, String context) {
        try {
            emailService.sendOtpEmail(user.getEmail(), otp);
        } catch (RuntimeException exception) {
            log.warn("Failed to send OTP email during {} for {}", context, user.getEmail(), exception);
        }
    }

    private void sendResetEmailSafely(User user, String resetToken) {
        try {
            emailService.sendPasswordResetEmail(user.getEmail(), resetToken);
        } catch (RuntimeException exception) {
            log.warn("Failed to send password reset email for {}", user.getEmail(), exception);
        }
    }

    private void authenticateLoginCredentials(String normalizedEmail, String password) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(normalizedEmail, password));
        } catch (BadCredentialsException exception) {
            authAbuseProtectionService.recordFailedLogin(normalizedEmail);
            throw exception;
        }
    }

    private void clearStoredResetToken(User user) {
        user.setResetToken(null);
        user.setResetTokenExpiry(null);
    }

    private String normalizeEmail(String email) {
        if (!StringUtils.hasText(email)) {
            return email;
        }
        return email.trim().toLowerCase(Locale.ROOT);
    }
}
