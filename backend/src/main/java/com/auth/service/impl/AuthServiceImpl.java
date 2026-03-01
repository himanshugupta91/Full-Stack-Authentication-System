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
import java.util.Optional;
import java.util.Set;

/**
 * Implementation of AuthService.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private static final String ERROR_EMAIL_ALREADY_REGISTERED = "Email already registered!";
    private static final String ERROR_EMAIL_ALREADY_VERIFIED = "Email already verified!";
    private static final String ERROR_INVALID_CREDENTIALS = "Invalid email or password!";
    private static final String ERROR_EMAIL_NOT_VERIFIED = "Please verify your email first!";
    private static final String ERROR_INVALID_OTP = "Invalid OTP!";
    private static final String ERROR_EXPIRED_OTP = "OTP has expired! Please request a new one.";
    private static final String ERROR_INVALID_RESET_TOKEN = "Invalid or expired reset token!";
    private static final String ERROR_EXPIRED_RESET_TOKEN = "Reset token has expired! Please request a new one.";
    private static final String ERROR_INCORRECT_CURRENT_PASSWORD = "Incorrect current password!";
    private static final String MESSAGE_REGISTER_SUCCESS =
            "Registration successful! Please check your email for OTP verification.";
    private static final String MESSAGE_VERIFY_OTP_SUCCESS = "Email verified successfully! You can now login.";
    private static final String MESSAGE_RESET_PASSWORD_GENERIC =
            "If an account exists with this email, a reset link will be sent.";
    private static final String MESSAGE_UPDATE_PASSWORD_SUCCESS = "Password updated successfully! You can now login.";
    private static final String MESSAGE_RESEND_OTP_SUCCESS = "OTP sent successfully! Please check your email.";
    private static final String MESSAGE_CHANGE_PASSWORD_SUCCESS = "Password changed successfully!";
    private static final String CONTEXT_REGISTRATION = "registration";
    private static final String CONTEXT_RESEND = "resend";

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
        if (userService.existsByEmail(request.getEmail())) {
            throw new UserAlreadyExistsException(ERROR_EMAIL_ALREADY_REGISTERED);
        }

        passwordPolicyService.validate(request.getPassword(), request.getEmail());

        User user = userMapper.toEntity(request);
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        String otp = assignVerificationOtp(user);
        assignDefaultUserRole(user);

        userService.save(user);
        sendOtpEmailSafely(user, otp, CONTEXT_REGISTRATION);

        return new MessageResponse(MESSAGE_REGISTER_SUCCESS, true);
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

        User user = requireUserByEmail(request.getEmail());

        if (user.isEnabled()) {
            throw new UserAlreadyExistsException(ERROR_EMAIL_ALREADY_VERIFIED);
        }

        if (!tokenHashService.matches(request.getOtp(), user.getVerificationOtp())) {
            authAbuseProtectionService.recordFailedOtp(user);
            throw new TokenValidationException(ERROR_INVALID_OTP);
        }

        if (isExpired(user.getOtpExpiry())) {
            throw new TokenValidationException(ERROR_EXPIRED_OTP);
        }

        user.setEnabled(true);
        user.setVerificationOtp(null);
        user.setOtpExpiry(null);
        authAbuseProtectionService.clearOtpFailures(user);
        userService.save(user);

        return new MessageResponse(MESSAGE_VERIFY_OTP_SUCCESS, true);
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

        Optional<User> user = userService.findByEmail(request.getEmail());
        if (user.isEmpty()) {
            authAbuseProtectionService.recordFailedLogin(request.getEmail());
            throw new BadCredentialsException(ERROR_INVALID_CREDENTIALS);
        }
        User existingUser = user.get();

        if (!existingUser.isEnabled()) {
            throw new TokenValidationException(ERROR_EMAIL_NOT_VERIFIED);
        }

        authenticateLoginCredentials(request);

        authAbuseProtectionService.clearLoginFailures(existingUser);

        return authTokenService.issueTokens(existingUser);
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

        Optional<User> userOpt = userService.findByEmail(request.getEmail());
        if (userOpt.isEmpty()) {
            return new MessageResponse(MESSAGE_RESET_PASSWORD_GENERIC, true);
        }
        User user = userOpt.get();

        String resetToken = assignResetToken(user);
        userService.save(user);

        sendResetEmailSafely(user, resetToken);

        return new MessageResponse(MESSAGE_RESET_PASSWORD_GENERIC, true);
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
                .orElseThrow(() -> new TokenValidationException(ERROR_INVALID_RESET_TOKEN));

        if (isExpired(user.getResetTokenExpiry())) {
            throw new TokenValidationException(ERROR_EXPIRED_RESET_TOKEN);
        }

        passwordPolicyService.validate(request.getNewPassword(), user.getEmail());

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        clearResetToken(user);
        userService.save(user);

        return new MessageResponse(MESSAGE_UPDATE_PASSWORD_SUCCESS, true);
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

        User user = requireUserByEmail(email);

        if (user.isEnabled()) {
            throw new UserAlreadyExistsException(ERROR_EMAIL_ALREADY_VERIFIED);
        }

        String otp = assignVerificationOtp(user);
        userService.save(user);
        sendOtpEmailSafely(user, otp, CONTEXT_RESEND);

        return new MessageResponse(MESSAGE_RESEND_OTP_SUCCESS, true);
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
        User user = requireUserByEmail(email);

        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new BadCredentialsException(ERROR_INCORRECT_CURRENT_PASSWORD);
        }

        passwordPolicyService.validate(request.getNewPassword(), user.getEmail());

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userService.save(user);

        return new MessageResponse(MESSAGE_CHANGE_PASSWORD_SUCCESS, true);
    }

    private User requireUserByEmail(String email) {
        return userService.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found!"));
    }

    private String assignVerificationOtp(User user) {
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

    private String assignResetToken(User user) {
        String resetToken = otpService.generateResetToken();
        String resetTokenHash = tokenHashService.hash(resetToken);
        LocalDateTime expiry = LocalDateTime.now().plusMinutes(resetTokenExpirationMinutes);

        user.setResetToken(resetTokenHash);
        user.setResetTokenExpiry(expiry);
        return resetToken;
    }

    private boolean isExpired(LocalDateTime expiry) {
        return expiry == null || expiry.isBefore(LocalDateTime.now());
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

    private void authenticateLoginCredentials(LoginRequest request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
        } catch (BadCredentialsException exception) {
            authAbuseProtectionService.recordFailedLogin(request.getEmail());
            throw exception;
        }
    }

    private void clearResetToken(User user) {
        user.setResetToken(null);
        user.setResetTokenExpiry(null);
    }
}
