package com.auth.service.impl;

import com.auth.dto.request.ChangePasswordRequest;
import com.auth.dto.request.LoginRequest;
import com.auth.dto.request.OtpVerifyRequest;
import com.auth.dto.request.RegisterRequest;
import com.auth.dto.request.ResetPasswordRequest;
import com.auth.dto.request.UpdatePasswordRequest;
import com.auth.dto.response.AuthTokens;
import com.auth.dto.response.MessageResponse;
import com.auth.entity.RoleName;
import com.auth.entity.User;
import com.auth.exception.ResourceNotFoundException;
import com.auth.exception.TokenValidationException;
import com.auth.exception.UserAlreadyExistsException;
import com.auth.mapper.UserMapper;
import com.auth.service.AuthService;
import com.auth.service.RoleService;
import com.auth.service.UserService;
import com.auth.service.auth.AuthAbuseProtectionService;
import com.auth.service.auth.AuthTokenService;
import com.auth.service.support.DateTimeProvider;
import com.auth.service.support.EmailService;
import com.auth.service.support.OtpService;
import com.auth.service.support.PasswordPolicyService;
import com.auth.service.support.TokenHashService;
import com.auth.util.EmailNormalizer;
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
import java.util.Set;

/**
 * Core authentication service — handles registration, OTP verification, login,
 * password reset, and password change flows.
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
    private final DateTimeProvider dateTimeProvider;

    @Value("${otp.expiration.minutes:5}")
    private int otpExpirationMinutes;

    @Value("${auth.reset-token.expiration.minutes:5}")
    private int resetTokenExpirationMinutes;

    /**
     * Registers a new user account.
     *
     * <p>Validates the email uniqueness, enforces the password policy, persists the
     * user with a hashed OTP, and sends a verification email.
     *
     * @throws UserAlreadyExistsException if the email is already registered
     */
    @Override
    @Transactional
    public MessageResponse register(RegisterRequest request) {
        String email = normalizeEmail(request.getEmail());
        if (userService.existsByEmail(email)) {
            throw new UserAlreadyExistsException("Email already registered!");
        }

        passwordPolicyService.validate(request.getPassword(), email);

        User user = userMapper.toEntity(request);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setAuthProvider("local");
        user.setAuthProviderUserId(null);

        String otp = storeVerificationOtp(user);
        user.setRoles(Set.of(roleService.findOrCreateRole(RoleName.ROLE_USER)));

        userService.save(user);
        sendOtpEmailSafely(user, otp, "registration");

        return new MessageResponse("Registration successful! Please check your email for OTP verification.", true);
    }

    /**
     * Verifies a user's email address using the OTP they received.
     *
     * @throws ResourceNotFoundException  if no user exists for the given email
     * @throws UserAlreadyExistsException if the email is already verified
     * @throws TokenValidationException   if the OTP is incorrect or expired
     */
    @Override
    @Transactional
    public MessageResponse verifyOtp(OtpVerifyRequest request) {
        String email = normalizeEmail(request.getEmail());
        authAbuseProtectionService.guardOtpVerification(email);

        User user = requireUserByEmail(email);
        requireEmailNotVerified(user);
        verifyOtpMatch(user, request.getOtp());
        requireTokenNotExpired(user.getOtpExpiry(), "OTP has expired! Please request a new one.");

        user.setEnabled(true);
        user.setVerificationOtp(null);
        user.setOtpExpiry(null);
        authAbuseProtectionService.clearOtpFailures(user);
        userService.save(user);

        sendWelcomeEmailSafely(user);
        return new MessageResponse("Email verified successfully! You can now login.", true);
    }

    /**
     * Authenticates a user with email and password, returning a fresh token pair.
     *
     * <p>Users with pending email verification are still allowed to authenticate
     * so they can reach verification UX after sign-in.
     *
     * @throws BadCredentialsException if the credentials are invalid
     */
    @Override
    public AuthTokens login(LoginRequest request) {
        String email = normalizeEmail(request.getEmail());
        authAbuseProtectionService.guardLoginAttempt(email);

        User user = userService.findByEmail(email).orElseGet(() -> {
            authAbuseProtectionService.recordFailedLogin(email);
            throw new BadCredentialsException("Invalid email or password!");
        });

        authenticateCredentials(email, request.getPassword());
        authAbuseProtectionService.clearLoginFailures(user);

        return authTokenService.issueTokens(user);
    }

    /**
     * Initiates password reset by generating a token and sending a reset link.
     * Always returns the same generic message to prevent email enumeration.
     */
    @Override
    @Transactional
    public MessageResponse resetPassword(ResetPasswordRequest request) {
        String email = normalizeEmail(request.getEmail());
        authAbuseProtectionService.guardResetPassword(email);

        MessageResponse genericResponse = new MessageResponse(
                "If an account exists with this email, a reset link will be sent.", true);

        userService.findByEmail(email).ifPresent(user -> {
            String resetToken = storeResetToken(user);
            userService.save(user);
            sendResetEmailSafely(user, resetToken);
        });

        return genericResponse;
    }

    /**
     * Completes a password reset using the token sent to the user's email.
     *
     * @throws TokenValidationException if the token is invalid or expired
     */
    @Override
    @Transactional
    public MessageResponse updatePassword(UpdatePasswordRequest request) {
        String tokenHash = tokenHashService.hash(request.getToken());
        User user = userService.findByResetToken(tokenHash)
                .orElseThrow(() -> new TokenValidationException("Invalid or expired reset token!"));

        requireTokenNotExpired(user.getResetTokenExpiry(), "Reset token has expired! Please request a new one.");

        passwordPolicyService.validate(request.getNewPassword(), user.getEmail());
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        clearResetToken(user);
        userService.save(user);

        sendPasswordChangedEmailSafely(user, "reset-token update");
        return new MessageResponse("Password updated successfully! You can now login.", true);
    }

    /**
     * Resends an OTP to the given email address for users who missed the original.
     *
     * @throws ResourceNotFoundException  if no user exists for the given email
     * @throws UserAlreadyExistsException if the email is already verified
     */
    @Override
    @Transactional
    public MessageResponse resendOtp(String email) {
        String normalized = normalizeEmail(email);
        authAbuseProtectionService.guardResendOtp(normalized);

        User user = requireUserByEmail(normalized);
        requireEmailNotVerified(user);

        String otp = storeVerificationOtp(user);
        userService.save(user);
        sendOtpEmailSafely(user, otp, "resend");

        return new MessageResponse("OTP sent successfully! Please check your email.", true);
    }

    /**
     * Changes the password for an already-authenticated user.
     *
     * @throws ResourceNotFoundException if the authenticated user cannot be found
     * @throws BadCredentialsException   if the current password is incorrect
     */
    @Override
    @Transactional
    public MessageResponse changePassword(String email, ChangePasswordRequest request) {
        User user = requireUserByEmail(email);

        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new BadCredentialsException("Incorrect current password!");
        }

        passwordPolicyService.validate(request.getNewPassword(), user.getEmail());
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userService.save(user);

        sendPasswordChangedEmailSafely(user, "authenticated password change");
        return new MessageResponse("Password changed successfully!", true);
    }

    // ── Private helpers ──────────────────────────────────────────────────────

    private User requireUserByEmail(String email) {
        return userService.findByEmail(normalizeEmail(email))
                .orElseThrow(() -> new ResourceNotFoundException("User not found!"));
    }

    private void requireEmailNotVerified(User user) {
        if (user.isEnabled()) {
            throw new UserAlreadyExistsException("Email already verified!");
        }
    }

    private void verifyOtpMatch(User user, String rawOtp) {
        if (!tokenHashService.matches(rawOtp, user.getVerificationOtp())) {
            authAbuseProtectionService.recordFailedOtp(user);
            throw new TokenValidationException("Invalid OTP!");
        }
    }

    private void requireTokenNotExpired(LocalDateTime expiry, String errorMessage) {
        LocalDateTime now = dateTimeProvider.now();
        if (expiry == null || expiry.isBefore(now)) {
            throw new TokenValidationException(errorMessage);
        }
    }

    private String storeVerificationOtp(User user) {
        String otp = otpService.generateOtp();
        user.setVerificationOtp(tokenHashService.hash(otp));
        user.setOtpExpiry(dateTimeProvider.now().plusMinutes(otpExpirationMinutes));
        return otp;
    }

    private String storeResetToken(User user) {
        String resetToken = otpService.generateResetToken();
        user.setResetToken(tokenHashService.hash(resetToken));
        user.setResetTokenExpiry(dateTimeProvider.now().plusMinutes(resetTokenExpirationMinutes));
        return resetToken;
    }

    private void clearResetToken(User user) {
        user.setResetToken(null);
        user.setResetTokenExpiry(null);
    }

    private void authenticateCredentials(String email, String password) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(email, password));
        } catch (BadCredentialsException ex) {
            authAbuseProtectionService.recordFailedLogin(email);
            throw ex;
        }
    }

    private String normalizeEmail(String email) {
        return EmailNormalizer.normalizeOrNull(email);
    }

    private void sendOtpEmailSafely(User user, String otp, String context) {
        try {
            emailService.sendOtpEmail(user.getEmail(), user.getName(), otp);
        } catch (RuntimeException ex) {
            log.warn("Failed to send OTP email during {} for {}", context, user.getEmail(), ex);
        }
    }

    private void sendResetEmailSafely(User user, String resetToken) {
        try {
            emailService.sendPasswordResetEmail(user.getEmail(), user.getName(), resetToken);
        } catch (RuntimeException ex) {
            log.warn("Failed to send password reset email for {}", user.getEmail(), ex);
        }
    }

    private void sendWelcomeEmailSafely(User user) {
        try {
            emailService.sendWelcomeEmail(user.getEmail(), user.getName());
        } catch (RuntimeException ex) {
            log.warn("Failed to send welcome email for {}", user.getEmail(), ex);
        }
    }

    private void sendPasswordChangedEmailSafely(User user, String context) {
        try {
            emailService.sendPasswordChangedConfirmationEmail(user.getEmail(), user.getName());
        } catch (RuntimeException ex) {
            log.warn("Failed to send password-changed confirmation email during {} for {}", context, user.getEmail(), ex);
        }
    }
}
