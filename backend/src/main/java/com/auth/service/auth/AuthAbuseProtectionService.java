package com.auth.service.auth;

import com.auth.entity.User;
import com.auth.exception.AccountLockedException;
import com.auth.exception.RateLimitExceededException;
import com.auth.service.UserService;
import com.auth.service.support.EmailService;
import com.auth.service.support.RateLimitService;
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
import java.util.Locale;

/**
 * Coordinates Redis rate limiting and user-level brute-force lockouts.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AuthAbuseProtectionService {

    private final RateLimitService rateLimitService;
    private final UserService userService;
    private final EmailService emailService;

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
        if (isProtectionDisabled()) {
            return;
        }

        String normalizedEmail = normalizeEmail(email);
        String clientIp = resolveClientIp();

        enforce(buildRateLimitKey("auth:login:ip:", clientIp), loginIpLimit, loginIpWindowSeconds,
                "Too many login attempts from this IP. Please retry later.");
        enforce(buildRateLimitKey("auth:login:email:", normalizedEmail), loginEmailLimit,
                loginEmailWindowSeconds,
                "Too many login attempts for this account. Please retry later.");

        userService.findByEmail(normalizedEmail).ifPresent(this::assertLoginNotLocked);
    }

    /** Increments failed login attempts and applies temporary lockout when threshold is crossed. */
    public void recordFailedLogin(String email) {
        if (isProtectionDisabled()) {
            return;
        }

        String normalizedEmail = normalizeEmail(email);
        userService.findByEmail(normalizedEmail).ifPresent(this::applyFailedLoginAttempt);
    }

    /** Clears login brute-force counters on successful authentication. */
    public void clearLoginFailures(User user) {
        if (isProtectionDisabled() || isMissingUser(user)) {
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
        if (isProtectionDisabled()) {
            return;
        }

        String normalizedEmail = normalizeEmail(email);
        String clientIp = resolveClientIp();

        enforce(buildRateLimitKey("auth:otp-verify:ip:", clientIp), otpVerifyIpLimit,
                otpVerifyIpWindowSeconds,
                "Too many OTP verification attempts from this IP. Please retry later.");
        enforce(buildRateLimitKey("auth:otp-verify:email:", normalizedEmail), otpVerifyEmailLimit,
                otpVerifyEmailWindowSeconds,
                "Too many OTP verification attempts for this email. Please retry later.");

        userService.findByEmail(normalizedEmail).ifPresent(this::assertOtpNotLocked);
    }

    /** Increments OTP failure counter and applies temporary OTP lockout. */
    public void recordFailedOtp(User user) {
        if (isProtectionDisabled() || isMissingUser(user)) {
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
        if (isProtectionDisabled() || isMissingUser(user)) {
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
        if (isProtectionDisabled()) {
            return;
        }

        String normalizedEmail = normalizeEmail(email);
        String clientIp = resolveClientIp();

        enforce(buildRateLimitKey("auth:resend-otp:email-cooldown:", normalizedEmail), 1,
                resendOtpCooldownSeconds,
                "Please wait before requesting another OTP.");
        enforce(buildRateLimitKey("auth:resend-otp:email:", normalizedEmail), resendOtpEmailLimit,
                resendOtpEmailWindowSeconds,
                "Too many OTP resend requests for this email. Please retry later.");
        enforce(buildRateLimitKey("auth:resend-otp:ip:", clientIp), resendOtpIpLimit,
                resendOtpIpWindowSeconds,
                "Too many OTP resend requests from this IP. Please retry later.");
    }

    /** Applies forgot-password endpoint limits per email and IP. */
    public void guardResetPassword(String email) {
        if (isProtectionDisabled()) {
            return;
        }

        String normalizedEmail = normalizeEmail(email);
        String clientIp = resolveClientIp();

        enforce(buildRateLimitKey("auth:reset-password:email:", normalizedEmail), resetPasswordEmailLimit,
                resetPasswordEmailWindowSeconds,
                "Too many password reset requests for this email. Please retry later.");
        enforce(buildRateLimitKey("auth:reset-password:ip:", clientIp), resetPasswordIpLimit,
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
            return "unknown";
        }

        HttpServletRequest request = attributes.getRequest();
        String forwardedIp = extractForwardedClientIp(request);
        if (forwardedIp != null) {
            return forwardedIp;
        }

        String realIp = trimToNull(request.getHeader("X-Real-IP"));
        if (realIp != null) {
            return realIp;
        }

        String remoteAddress = trimToNull(request.getRemoteAddr());
        return remoteAddress != null ? remoteAddress : "unknown";
    }

    private String normalizeEmail(String email) {
        if (!StringUtils.hasText(email)) {
            return "unknown-email";
        }
        String normalizedEmail = email.trim().toLowerCase(Locale.ROOT);
        return normalizedEmail;
    }

    private String buildRateLimitKey(String prefix, String keyPart) {
        String rateLimitKey = prefix + keyPart;
        return rateLimitKey;
    }

    private void applyFailedLoginAttempt(User user) {
        int nextFailedAttempts = user.getFailedLoginAttempts() + 1;
        if (nextFailedAttempts >= loginMaxAttempts) {
            user.setFailedLoginAttempts(0);
            user.setAccountLockedUntil(LocalDateTime.now().plusMinutes(loginLockMinutes));
            userService.save(user);
            sendAccountLockEmailSafely(user);
            return;
        } else {
            user.setFailedLoginAttempts(nextFailedAttempts);
        }

        userService.save(user);
    }

    private void sendAccountLockEmailSafely(User user) {
        try {
            emailService.sendAccountLockedAlertEmail(user.getEmail(), user.getName(), user.getAccountLockedUntil());
        } catch (RuntimeException exception) {
            log.warn("Failed to send account lock alert email for {}", user.getEmail(), exception);
        }
    }

    private boolean hasLoginFailureState(User user) {
        boolean hasLoginFailureState = user.getFailedLoginAttempts() != 0 || user.getAccountLockedUntil() != null;
        return hasLoginFailureState;
    }

    private boolean hasOtpFailureState(User user) {
        boolean hasOtpFailureState = user.getFailedOtpAttempts() != 0 || user.getOtpLockedUntil() != null;
        return hasOtpFailureState;
    }

    private boolean isActiveLock(LocalDateTime lockedUntil) {
        if (lockedUntil == null) {
            return false;
        }

        LocalDateTime now = LocalDateTime.now();
        boolean isActiveLock = lockedUntil.isAfter(now);
        return isActiveLock;
    }

    private long computeRetryAfterSeconds(LocalDateTime lockedUntil) {
        LocalDateTime now = LocalDateTime.now();
        long retryAfterSeconds = Duration.between(now, lockedUntil).getSeconds();
        return retryAfterSeconds;
    }

    private String extractForwardedClientIp(HttpServletRequest request) {
        String forwardedFor = trimToNull(request.getHeader("X-Forwarded-For"));
        if (forwardedFor == null) {
            return null;
        }

        String[] chain = forwardedFor.split(",");
        if (chain.length == 0) {
            return null;
        }
        String firstForwardedIp = trimToNull(chain[0]);
        return firstForwardedIp;
    }

    private String trimToNull(String value) {
        if (!StringUtils.hasText(value)) {
            return null;
        }
        String trimmedValue = value.trim();
        return trimmedValue;
    }

    private boolean isProtectionDisabled() {
        boolean protectionDisabled = !protectionEnabled;
        return protectionDisabled;
    }

    private boolean isMissingUser(User user) {
        boolean missingUser = user == null;
        return missingUser;
    }
}
