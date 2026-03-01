package com.auth.service.auth;

import com.auth.entity.User;
import com.auth.exception.AccountLockedException;
import com.auth.exception.RateLimitExceededException;
import com.auth.service.UserService;
import com.auth.service.support.RateLimitService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Locale;

/**
 * Coordinates Redis rate limiting and user-level brute-force lockouts.
 */
@Service
@RequiredArgsConstructor
public class AuthAbuseProtectionService {

    private static final String UNKNOWN_IP = "unknown";
    private static final String UNKNOWN_EMAIL = "unknown-email";
    private static final String HEADER_X_FORWARDED_FOR = "X-Forwarded-For";
    private static final String HEADER_X_REAL_IP = "X-Real-IP";
    private static final String FORWARDED_FOR_SEPARATOR = ",";
    private static final String RATE_LIMIT_LOGIN_IP_KEY_PREFIX = "auth:login:ip:";
    private static final String RATE_LIMIT_LOGIN_EMAIL_KEY_PREFIX = "auth:login:email:";
    private static final String RATE_LIMIT_OTP_VERIFY_IP_KEY_PREFIX = "auth:otp-verify:ip:";
    private static final String RATE_LIMIT_OTP_VERIFY_EMAIL_KEY_PREFIX = "auth:otp-verify:email:";
    private static final String RATE_LIMIT_RESEND_OTP_EMAIL_COOLDOWN_KEY_PREFIX = "auth:resend-otp:email-cooldown:";
    private static final String RATE_LIMIT_RESEND_OTP_EMAIL_KEY_PREFIX = "auth:resend-otp:email:";
    private static final String RATE_LIMIT_RESEND_OTP_IP_KEY_PREFIX = "auth:resend-otp:ip:";
    private static final String RATE_LIMIT_RESET_PASSWORD_EMAIL_KEY_PREFIX = "auth:reset-password:email:";
    private static final String RATE_LIMIT_RESET_PASSWORD_IP_KEY_PREFIX = "auth:reset-password:ip:";

    private final RateLimitService rateLimitService;
    private final UserService userService;

    @Value("${auth.protection.enabled:true}")
    private boolean protectionEnabled;

    @Value("${auth.rate-limit.login.ip.limit:5}")
    private long loginIpLimit;
    @Value("${auth.rate-limit.login.ip.window-seconds:60}")
    private long loginIpWindowSeconds;
    @Value("${auth.rate-limit.login.email.limit:10}")
    private long loginEmailLimit;
    @Value("${auth.rate-limit.login.email.window-seconds:900}")
    private long loginEmailWindowSeconds;

    @Value("${auth.rate-limit.otp-verify.ip.limit:20}")
    private long otpVerifyIpLimit;
    @Value("${auth.rate-limit.otp-verify.ip.window-seconds:600}")
    private long otpVerifyIpWindowSeconds;
    @Value("${auth.rate-limit.otp-verify.email.limit:5}")
    private long otpVerifyEmailLimit;
    @Value("${auth.rate-limit.otp-verify.email.window-seconds:600}")
    private long otpVerifyEmailWindowSeconds;

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

    @Value("${auth.rate-limit.reset-password.email.limit:3}")
    private long resetPasswordEmailLimit;
    @Value("${auth.rate-limit.reset-password.email.window-seconds:1800}")
    private long resetPasswordEmailWindowSeconds;
    @Value("${auth.rate-limit.reset-password.ip.limit:10}")
    private long resetPasswordIpLimit;
    @Value("${auth.rate-limit.reset-password.ip.window-seconds:1800}")
    private long resetPasswordIpWindowSeconds;

    @Value("${auth.bruteforce.login.max-attempts:10}")
    private int loginMaxAttempts;
    @Value("${auth.bruteforce.login.lock-minutes:15}")
    private long loginLockMinutes;

    @Value("${auth.bruteforce.otp.max-attempts:5}")
    private int otpMaxAttempts;
    @Value("${auth.bruteforce.otp.lock-minutes:10}")
    private long otpLockMinutes;

    /** Checks login endpoint rate limits and active account lock state. */
    public void guardLoginAttempt(String email) {
        if (!protectionEnabled) {
            return;
        }

        String normalizedEmail = normalizeEmail(email);
        String clientIp = resolveClientIp();

        enforce(buildRateLimitKey(RATE_LIMIT_LOGIN_IP_KEY_PREFIX, clientIp), loginIpLimit, loginIpWindowSeconds,
                "Too many login attempts from this IP. Please retry later.");
        enforce(buildRateLimitKey(RATE_LIMIT_LOGIN_EMAIL_KEY_PREFIX, normalizedEmail), loginEmailLimit,
                loginEmailWindowSeconds,
                "Too many login attempts for this account. Please retry later.");

        userService.findByEmail(normalizedEmail).ifPresent(this::assertLoginNotLocked);
    }

    /** Increments failed login attempts and applies temporary lockout when threshold is crossed. */
    public void recordFailedLogin(String email) {
        if (!protectionEnabled) {
            return;
        }

        String normalizedEmail = normalizeEmail(email);
        userService.findByEmail(normalizedEmail).ifPresent(this::applyFailedLoginAttempt);
    }

    /** Clears login brute-force counters on successful authentication. */
    public void clearLoginFailures(User user) {
        if (!protectionEnabled) {
            return;
        }

        if (user == null) {
            return;
        }

        if (!hasLoginFailureState(user)) {
            return;
        }

        user.setFailedLoginAttempts(0);
        user.setAccountLockedUntil(null);
        userService.save(user);
    }

    /** Checks OTP verification rate limits and OTP-specific lock state. */
    public void guardOtpVerification(String email) {
        if (!protectionEnabled) {
            return;
        }

        String normalizedEmail = normalizeEmail(email);
        String clientIp = resolveClientIp();

        enforce(buildRateLimitKey(RATE_LIMIT_OTP_VERIFY_IP_KEY_PREFIX, clientIp), otpVerifyIpLimit,
                otpVerifyIpWindowSeconds,
                "Too many OTP verification attempts from this IP. Please retry later.");
        enforce(buildRateLimitKey(RATE_LIMIT_OTP_VERIFY_EMAIL_KEY_PREFIX, normalizedEmail), otpVerifyEmailLimit,
                otpVerifyEmailWindowSeconds,
                "Too many OTP verification attempts for this email. Please retry later.");

        userService.findByEmail(normalizedEmail).ifPresent(this::assertOtpNotLocked);
    }

    /** Increments OTP failure counter and applies temporary OTP lockout. */
    public void recordFailedOtp(User user) {
        if (!protectionEnabled) {
            return;
        }

        if (user == null) {
            return;
        }

        int nextFailedAttempts = user.getFailedOtpAttempts() + 1;
        if (nextFailedAttempts >= otpMaxAttempts) {
            user.setFailedOtpAttempts(0);
            user.setOtpLockedUntil(LocalDateTime.now().plusMinutes(otpLockMinutes));
        } else {
            user.setFailedOtpAttempts(nextFailedAttempts);
        }
        userService.save(user);
    }

    /** Clears OTP brute-force counters after successful OTP verification. */
    public void clearOtpFailures(User user) {
        if (!protectionEnabled) {
            return;
        }

        if (user == null) {
            return;
        }

        if (!hasOtpFailureState(user)) {
            return;
        }

        user.setFailedOtpAttempts(0);
        user.setOtpLockedUntil(null);
        userService.save(user);
    }

    /** Applies resend-OTP endpoint limits (cooldown + window limits per email and IP). */
    public void guardResendOtp(String email) {
        if (!protectionEnabled) {
            return;
        }

        String normalizedEmail = normalizeEmail(email);
        String clientIp = resolveClientIp();

        enforce(buildRateLimitKey(RATE_LIMIT_RESEND_OTP_EMAIL_COOLDOWN_KEY_PREFIX, normalizedEmail), 1,
                resendOtpCooldownSeconds,
                "Please wait before requesting another OTP.");
        enforce(buildRateLimitKey(RATE_LIMIT_RESEND_OTP_EMAIL_KEY_PREFIX, normalizedEmail), resendOtpEmailLimit,
                resendOtpEmailWindowSeconds,
                "Too many OTP resend requests for this email. Please retry later.");
        enforce(buildRateLimitKey(RATE_LIMIT_RESEND_OTP_IP_KEY_PREFIX, clientIp), resendOtpIpLimit,
                resendOtpIpWindowSeconds,
                "Too many OTP resend requests from this IP. Please retry later.");
    }

    /** Applies forgot-password endpoint limits per email and IP. */
    public void guardResetPassword(String email) {
        if (!protectionEnabled) {
            return;
        }

        String normalizedEmail = normalizeEmail(email);
        String clientIp = resolveClientIp();

        enforce(buildRateLimitKey(RATE_LIMIT_RESET_PASSWORD_EMAIL_KEY_PREFIX, normalizedEmail), resetPasswordEmailLimit,
                resetPasswordEmailWindowSeconds,
                "Too many password reset requests for this email. Please retry later.");
        enforce(buildRateLimitKey(RATE_LIMIT_RESET_PASSWORD_IP_KEY_PREFIX, clientIp), resetPasswordIpLimit,
                resetPasswordIpWindowSeconds,
                "Too many password reset requests from this IP. Please retry later.");
    }

    private void enforce(String key, long limit, long windowSeconds, String message) {
        RateLimitService.RateLimitDecision decision = rateLimitService.consume(
                key,
                limit,
                Duration.ofSeconds(windowSeconds));
        if (decision.allowed()) {
            return;
        }

        long retryAfterSeconds = Math.max(1, decision.retryAfterSeconds());
        throw new RateLimitExceededException(message, retryAfterSeconds);
    }

    private void assertLoginNotLocked(User user) {
        LocalDateTime lockedUntil = user.getAccountLockedUntil();
        if (!isActiveLock(lockedUntil)) {
            return;
        }

        long retryAfterSeconds = computeRetryAfterSeconds(lockedUntil);
        throw new AccountLockedException("Account is temporarily locked due to repeated failed logins.",
                Math.max(1, retryAfterSeconds));
    }

    private void assertOtpNotLocked(User user) {
        LocalDateTime lockedUntil = user.getOtpLockedUntil();
        if (!isActiveLock(lockedUntil)) {
            return;
        }

        long retryAfterSeconds = computeRetryAfterSeconds(lockedUntil);
        throw new AccountLockedException("OTP verification is temporarily locked due to repeated failed attempts.",
                Math.max(1, retryAfterSeconds));
    }

    private String resolveClientIp() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            return UNKNOWN_IP;
        }

        HttpServletRequest request = attributes.getRequest();
        String forwardedFor = request.getHeader(HEADER_X_FORWARDED_FOR);
        if (forwardedFor != null && !forwardedFor.isBlank()) {
            String[] chain = forwardedFor.split(FORWARDED_FOR_SEPARATOR);
            if (chain.length > 0 && !chain[0].isBlank()) {
                return chain[0].trim();
            }
        }

        String realIp = request.getHeader(HEADER_X_REAL_IP);
        if (realIp != null && !realIp.isBlank()) {
            return realIp.trim();
        }

        String remoteAddress = request.getRemoteAddr();
        if (remoteAddress == null) {
            return UNKNOWN_IP;
        }

        if (remoteAddress.isBlank()) {
            return UNKNOWN_IP;
        }

        return remoteAddress.trim();
    }

    private String normalizeEmail(String email) {
        if (email == null || email.isBlank()) {
            return UNKNOWN_EMAIL;
        }
        return email.trim().toLowerCase(Locale.ROOT);
    }

    private String buildRateLimitKey(String prefix, String keyPart) {
        return prefix + keyPart;
    }

    private void applyFailedLoginAttempt(User user) {
        int nextFailedAttempts = user.getFailedLoginAttempts() + 1;
        if (nextFailedAttempts >= loginMaxAttempts) {
            user.setFailedLoginAttempts(0);
            user.setAccountLockedUntil(LocalDateTime.now().plusMinutes(loginLockMinutes));
        } else {
            user.setFailedLoginAttempts(nextFailedAttempts);
        }

        userService.save(user);
    }

    private boolean hasLoginFailureState(User user) {
        return user.getFailedLoginAttempts() != 0 || user.getAccountLockedUntil() != null;
    }

    private boolean hasOtpFailureState(User user) {
        return user.getFailedOtpAttempts() != 0 || user.getOtpLockedUntil() != null;
    }

    private boolean isActiveLock(LocalDateTime lockedUntil) {
        if (lockedUntil == null) {
            return false;
        }

        LocalDateTime now = LocalDateTime.now();
        return lockedUntil.isAfter(now);
    }

    private long computeRetryAfterSeconds(LocalDateTime lockedUntil) {
        LocalDateTime now = LocalDateTime.now();
        return Duration.between(now, lockedUntil).getSeconds();
    }
}
