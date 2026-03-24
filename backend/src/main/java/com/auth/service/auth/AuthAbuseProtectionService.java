package com.auth.service.auth;

import com.auth.entity.User;
import com.auth.exception.AccountLockedException;
import com.auth.exception.RateLimitExceededException;
import com.auth.service.UserService;
import com.auth.service.support.DateTimeProvider;
import com.auth.service.support.EmailService;
import com.auth.service.support.RateLimitService;
import com.auth.util.EmailNormalizer;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.Duration;
import java.time.LocalDateTime;

/**
 * Coordinates Redis-based rate limiting and per-user brute-force lockouts for
 * all authentication endpoints (login, OTP verification, resend-OTP, and
 * password reset).
 *
 * <p>Protection can be disabled globally via {@code auth.protection.enabled=false}
 * for local development environments.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AuthAbuseProtectionService {

    private final RateLimitService rateLimitService;
    private final UserService userService;
    private final EmailService emailService;
    private final DateTimeProvider dateTimeProvider;

    @Value("${auth.protection.enabled:true}")
    private boolean protectionEnabled;

    // ── Login rate-limit config ───────────────────────────────────────────────

    @Value("${auth.rate-limit.login.ip.limit:5}")
    private long loginIpLimit;
    @Value("${auth.rate-limit.login.ip.window-seconds:60}")
    private long loginIpWindowSeconds;
    @Value("${auth.rate-limit.login.email.limit:10}")
    private long loginEmailLimit;
    @Value("${auth.rate-limit.login.email.window-seconds:900}")
    private long loginEmailWindowSeconds;

    // ── OTP verification rate-limit config ───────────────────────────────────

    @Value("${auth.rate-limit.otp-verify.ip.limit:20}")
    private long otpVerifyIpLimit;
    @Value("${auth.rate-limit.otp-verify.ip.window-seconds:600}")
    private long otpVerifyIpWindowSeconds;
    @Value("${auth.rate-limit.otp-verify.email.limit:5}")
    private long otpVerifyEmailLimit;
    @Value("${auth.rate-limit.otp-verify.email.window-seconds:600}")
    private long otpVerifyEmailWindowSeconds;

    // ── Resend-OTP rate-limit config ─────────────────────────────────────────

    @Value("${auth.rate-limit.resend-otp.email.cooldown-seconds:60}")
    private long resendOtpCooldownSeconds;
    @Value("${auth.rate-limit.resend-otp.email.limit:3}")
    private long resendOtpEmailLimit;
    @Value("${auth.rate-limit.resend-otp.email.window-seconds:900}")
    private long resendOtpEmailWindowSeconds;
    @Value("${auth.rate-limit.resend-otp.ip.limit:20}")
    private long resendOtpIpLimit;
    @Value("${auth.rate-limit.resend-otp.ip.window-seconds:900}")
    private long resendOtpIpWindowSeconds;

    // ── Password-reset rate-limit config ─────────────────────────────────────

    @Value("${auth.rate-limit.reset-password.email.limit:3}")
    private long resetPasswordEmailLimit;
    @Value("${auth.rate-limit.reset-password.email.window-seconds:1800}")
    private long resetPasswordEmailWindowSeconds;
    @Value("${auth.rate-limit.reset-password.ip.limit:10}")
    private long resetPasswordIpLimit;
    @Value("${auth.rate-limit.reset-password.ip.window-seconds:1800}")
    private long resetPasswordIpWindowSeconds;

    // ── Brute-force lockout config ────────────────────────────────────────────

    @Value("${auth.bruteforce.login.max-attempts:10}")
    private int loginMaxAttempts;
    @Value("${auth.bruteforce.login.lock-minutes:15}")
    private long loginLockMinutes;

    @Value("${auth.bruteforce.otp.max-attempts:5}")
    private int otpMaxAttempts;
    @Value("${auth.bruteforce.otp.lock-minutes:10}")
    private long otpLockMinutes;

    // ── Public guard methods ──────────────────────────────────────────────────

    /** Checks login endpoint rate limits and the account's active lockout state. */
    public void guardLoginAttempt(String email) {
        if (!protectionEnabled) return;

        String normalized = normalizeEmail(email);
        String clientIp = resolveClientIp();

        enforce("auth:login:ip:" + clientIp, loginIpLimit, loginIpWindowSeconds,
                "Too many login attempts from this IP. Please retry later.");
        enforce("auth:login:email:" + normalized, loginEmailLimit, loginEmailWindowSeconds,
                "Too many login attempts for this account. Please retry later.");

        userService.findByEmail(normalized).ifPresent(this::assertLoginNotLocked);
    }

    /** Records a failed login attempt and applies a lockout when the threshold is reached. */
    public void recordFailedLogin(String email) {
        if (!protectionEnabled) return;
        userService.findByEmail(normalizeEmail(email)).ifPresent(this::applyFailedLoginAttempt);
    }

    /** Clears login brute-force counters after a successful authentication. */
    public void clearLoginFailures(User user) {
        if (!protectionEnabled || user == null) return;
        if (user.getFailedLoginAttempts() == 0 && user.getAccountLockedUntil() == null) return;

        user.setFailedLoginAttempts(0);
        user.setAccountLockedUntil(null);
        userService.save(user);
    }

    /** Checks OTP verification rate limits and the account's OTP-specific lockout state. */
    public void guardOtpVerification(String email) {
        if (!protectionEnabled) return;

        String normalized = normalizeEmail(email);
        String clientIp = resolveClientIp();

        enforce("auth:otp-verify:ip:" + clientIp, otpVerifyIpLimit, otpVerifyIpWindowSeconds,
                "Too many OTP verification attempts from this IP. Please retry later.");
        enforce("auth:otp-verify:email:" + normalized, otpVerifyEmailLimit, otpVerifyEmailWindowSeconds,
                "Too many OTP verification attempts for this email. Please retry later.");

        userService.findByEmail(normalized).ifPresent(this::assertOtpNotLocked);
    }

    /** Increments the OTP failure counter and applies a temporary OTP lockout when reached. */
    public void recordFailedOtp(User user) {
        if (!protectionEnabled || user == null) return;

        int nextAttempts = user.getFailedOtpAttempts() + 1;
        if (nextAttempts >= otpMaxAttempts) {
            user.setFailedOtpAttempts(0);
            user.setOtpLockedUntil(dateTimeProvider.now().plusMinutes(otpLockMinutes));
        } else {
            user.setFailedOtpAttempts(nextAttempts);
        }
        userService.save(user);
    }

    /** Clears OTP brute-force counters after a successful OTP verification. */
    public void clearOtpFailures(User user) {
        if (!protectionEnabled || user == null) return;
        if (user.getFailedOtpAttempts() == 0 && user.getOtpLockedUntil() == null) return;

        user.setFailedOtpAttempts(0);
        user.setOtpLockedUntil(null);
        userService.save(user);
    }

    /** Applies resend-OTP endpoint limits: per-email cooldown, windowed limit, and IP limit. */
    public void guardResendOtp(String email) {
        if (!protectionEnabled) return;

        String normalized = normalizeEmail(email);
        String clientIp = resolveClientIp();

        enforce("auth:resend-otp:email-cooldown:" + normalized, 1, resendOtpCooldownSeconds,
                "Please wait before requesting another OTP.");
        enforce("auth:resend-otp:email:" + normalized, resendOtpEmailLimit, resendOtpEmailWindowSeconds,
                "Too many OTP resend requests for this email. Please retry later.");
        enforce("auth:resend-otp:ip:" + clientIp, resendOtpIpLimit, resendOtpIpWindowSeconds,
                "Too many OTP resend requests from this IP. Please retry later.");
    }

    /** Applies password-reset endpoint limits per email and IP address. */
    public void guardResetPassword(String email) {
        if (!protectionEnabled) return;

        String normalized = normalizeEmail(email);
        String clientIp = resolveClientIp();

        enforce("auth:reset-password:email:" + normalized, resetPasswordEmailLimit,
                resetPasswordEmailWindowSeconds,
                "Too many password reset requests for this email. Please retry later.");
        enforce("auth:reset-password:ip:" + clientIp, resetPasswordIpLimit,
                resetPasswordIpWindowSeconds,
                "Too many password reset requests from this IP. Please retry later.");
    }

    // ── Private helpers ───────────────────────────────────────────────────────

    private void enforce(String key, long limit, long windowSeconds, String message) {
        RateLimitService.RateLimitDecision decision =
                rateLimitService.consume(key, limit, Duration.ofSeconds(windowSeconds));
        if (!decision.allowed()) {
            throw new RateLimitExceededException(message, Math.max(1, decision.retryAfterSeconds()));
        }
    }

    private void assertLoginNotLocked(User user) {
        LocalDateTime lockedUntil = user.getAccountLockedUntil();
        LocalDateTime now = dateTimeProvider.now();
        if (lockedUntil != null && lockedUntil.isAfter(now)) {
            long retryAfter = Math.max(1, Duration.between(now, lockedUntil).getSeconds());
            throw new AccountLockedException(
                    "Account is temporarily locked due to repeated failed logins.", retryAfter);
        }
    }

    private void assertOtpNotLocked(User user) {
        LocalDateTime lockedUntil = user.getOtpLockedUntil();
        LocalDateTime now = dateTimeProvider.now();
        if (lockedUntil != null && lockedUntil.isAfter(now)) {
            long retryAfter = Math.max(1, Duration.between(now, lockedUntil).getSeconds());
            throw new AccountLockedException(
                    "OTP verification is temporarily locked due to repeated failed attempts.", retryAfter);
        }
    }

    private void applyFailedLoginAttempt(User user) {
        int nextAttempts = user.getFailedLoginAttempts() + 1;
        if (nextAttempts >= loginMaxAttempts) {
            user.setFailedLoginAttempts(0);
            user.setAccountLockedUntil(dateTimeProvider.now().plusMinutes(loginLockMinutes));
            userService.save(user);
            sendAccountLockEmailSafely(user);
        } else {
            user.setFailedLoginAttempts(nextAttempts);
            userService.save(user);
        }
    }

    private void sendAccountLockEmailSafely(User user) {
        try {
            emailService.sendAccountLockedAlertEmail(user.getEmail(), user.getName(), user.getAccountLockedUntil());
        } catch (RuntimeException ex) {
            log.warn("Failed to send account-lock alert email for {}", user.getEmail(), ex);
        }
    }

    private String resolveClientIp() {
        ServletRequestAttributes attributes =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            return "unknown";
        }

        HttpServletRequest request = attributes.getRequest();

        String forwarded = trimToNull(request.getHeader("X-Forwarded-For"));
        if (forwarded != null) {
            String firstIp = trimToNull(forwarded.split(",")[0]);
            if (firstIp != null) return firstIp;
        }

        String realIp = trimToNull(request.getHeader("X-Real-IP"));
        if (realIp != null) return realIp;

        String remote = trimToNull(request.getRemoteAddr());
        return remote != null ? remote : "unknown";
    }

    private String normalizeEmail(String email) {
        return EmailNormalizer.normalizeOr(email, "unknown-email");
    }

    private String trimToNull(String value) {
        if (!StringUtils.hasText(value)) return null;
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}
