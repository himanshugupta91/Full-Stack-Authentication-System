package com.auth.service.support;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.Year;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Service for sending emails (OTP verification, password reset).
 */
@Service
@RequiredArgsConstructor
public class EmailService {

    private static final DateTimeFormatter EMAIL_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a",
            Locale.ENGLISH);

    private final JavaMailSender mailSender;
    private final SpringTemplateEngine templateEngine;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Value("${otp.expiration.minutes:5}")
    private int otpExpirationMinutes;

    @Value("${app.frontend-reset-password-url:http://localhost:5173/reset-password}")
    private String resetPasswordUrl;

    @Value("${app.frontend-url:http://localhost:5173}")
    private String frontendUrl;

    @Value("${auth.reset-token.expiration.minutes:5}")
    private int resetTokenExpirationMinutes;

    @Value("${app.email.brand-name:Authentication System}")
    private String brandName;

    /**
     * Send OTP verification email.
     */
    public void sendOtpEmail(String toEmail, String otp) {
        sendOtpEmail(toEmail, null, otp);
    }

    /**
     * Send OTP verification email.
     */
    public void sendOtpEmail(String toEmail, String recipientName, String otp) {
        String resolvedRecipientName = resolveRecipientName(recipientName, toEmail);
        String subject = brandName + " - Verify Your Email";
        String plainTextBody = buildOtpPlainTextBody(resolvedRecipientName, otp);
        String htmlBody = buildOtpHtmlBodyFromTemplate(toEmail, resolvedRecipientName, otp);
        sendHtmlEmail(toEmail, subject, plainTextBody, htmlBody, "OTP email");
    }

    /**
     * Send password reset email with token link.
     */
    public void sendPasswordResetEmail(String toEmail, String recipientName, String resetToken) {
        String resetLink = resetPasswordUrl + "?token=" + URLEncoder.encode(resetToken, StandardCharsets.UTF_8);
        String resolvedRecipientName = resolveRecipientName(recipientName, toEmail);
        String subject = brandName + " - Password Reset Request";
        String plainTextBody = buildPasswordResetPlainTextBody(resolvedRecipientName, resetLink);
        String htmlBody = buildPasswordResetHtmlBodyFromTemplate(toEmail, resolvedRecipientName, resetLink);
        sendHtmlEmail(toEmail, subject, plainTextBody, htmlBody, "password reset email");
    }

    /**
     * Send welcome email after successful account verification.
     */
    public void sendWelcomeEmail(String toEmail, String recipientName) {
        String resolvedRecipientName = resolveRecipientName(recipientName, toEmail);
        String loginUrl = buildFrontendUrl("/login");
        String subject = brandName + " - Welcome";
        String plainTextBody = buildWelcomePlainTextBody(resolvedRecipientName, loginUrl);
        String htmlBody = buildWelcomeHtmlBodyFromTemplate(toEmail, resolvedRecipientName, loginUrl);
        sendHtmlEmail(toEmail, subject, plainTextBody, htmlBody, "welcome email");
    }

    /**
     * Send password change confirmation email.
     */
    public void sendPasswordChangedConfirmationEmail(String toEmail, String recipientName) {
        String resolvedRecipientName = resolveRecipientName(recipientName, toEmail);
        String loginUrl = buildFrontendUrl("/login");
        String forgotPasswordUrl = buildFrontendUrl("/forgot-password");
        String changedAtText = formatDateTime(LocalDateTime.now());
        String subject = brandName + " - Password Changed";
        String plainTextBody = buildPasswordChangedPlainTextBody(resolvedRecipientName, changedAtText, forgotPasswordUrl);
        String htmlBody = buildPasswordChangedHtmlBodyFromTemplate(
                toEmail,
                resolvedRecipientName,
                changedAtText,
                loginUrl,
                forgotPasswordUrl);
        sendHtmlEmail(toEmail, subject, plainTextBody, htmlBody, "password changed confirmation email");
    }

    /**
     * Send security alert when an account is temporarily locked.
     */
    public void sendAccountLockedAlertEmail(String toEmail, String recipientName, LocalDateTime lockedUntil) {
        String resolvedRecipientName = resolveRecipientName(recipientName, toEmail);
        long lockDurationMinutes = computeRemainingLockMinutes(lockedUntil);
        String unlockAtText = formatDateTime(lockedUntil);
        String forgotPasswordUrl = buildFrontendUrl("/forgot-password");
        String subject = brandName + " - Security Alert";
        String plainTextBody = buildAccountLockedPlainTextBody(
                resolvedRecipientName,
                lockDurationMinutes,
                unlockAtText,
                forgotPasswordUrl);
        String htmlBody = buildAccountLockedHtmlBodyFromTemplate(
                toEmail,
                resolvedRecipientName,
                lockDurationMinutes,
                unlockAtText,
                forgotPasswordUrl);
        sendHtmlEmail(toEmail, subject, plainTextBody, htmlBody, "account lock alert email");
    }
    /**
     * Builds otp plain text body.
     */

    private String buildOtpPlainTextBody(String recipientName, String otp) {
        return "Hello " + recipientName + "," +
                "\n\nYour one-time verification code is: " + otp +
                "\n\nThis OTP expires in " + otpExpirationMinutes + " minutes." +
                "\n\nIf you did not request this code, you can ignore this message.";
    }
    /**
     * Builds otp html body from template.
     */

    private String buildOtpHtmlBodyFromTemplate(String toEmail, String recipientName, String otp) {
        Context context = new Context();
        context.setVariable("brandName", brandName);
        context.setVariable("recipientEmail", toEmail);
        context.setVariable("recipientName", recipientName);
        context.setVariable("otp", otp);
        context.setVariable("otpExpirationMinutes", otpExpirationMinutes);
        context.setVariable("year", Year.now().getValue());
        return templateEngine.process("emails/otp-verification", context);
    }
    /**
     * Builds password reset plain text body.
     */

    private String buildPasswordResetPlainTextBody(String recipientName, String resetLink) {
        return "Hello " + recipientName + "," +
                "\n\nWe received a request to reset your password." +
                "\n\nReset your password using this link:\n" + resetLink +
                "\n\nThis link will expire in " + resetTokenExpirationMinutes + " minutes." +
                "\n\nIf you did not request this, you can ignore this email.";
    }
    /**
     * Builds password reset html body from template.
     */

    private String buildPasswordResetHtmlBodyFromTemplate(String toEmail, String recipientName, String resetLink) {
        Context context = new Context();
        context.setVariable("brandName", brandName);
        context.setVariable("recipientEmail", toEmail);
        context.setVariable("recipientName", recipientName);
        context.setVariable("resetLink", resetLink);
        context.setVariable("resetTokenExpirationMinutes", resetTokenExpirationMinutes);
        context.setVariable("year", Year.now().getValue());
        return templateEngine.process("emails/password-reset-request", context);
    }
    /**
     * Builds welcome plain text body.
     */

    private String buildWelcomePlainTextBody(String recipientName, String loginUrl) {
        return "Hello " + recipientName + "," +
                "\n\nWelcome to " + brandName + ". Your email has been successfully verified." +
                "\n\nYou can now sign in here:\n" + loginUrl +
                "\n\nIf you did not create this account, please contact support immediately.";
    }
    /**
     * Builds welcome html body from template.
     */

    private String buildWelcomeHtmlBodyFromTemplate(String toEmail, String recipientName, String loginUrl) {
        Context context = new Context();
        context.setVariable("brandName", brandName);
        context.setVariable("recipientEmail", toEmail);
        context.setVariable("recipientName", recipientName);
        context.setVariable("loginUrl", loginUrl);
        context.setVariable("year", Year.now().getValue());
        return templateEngine.process("emails/welcome-account", context);
    }

    private String buildPasswordChangedPlainTextBody(
            String recipientName,
            String changedAtText,
            String forgotPasswordUrl) {
        return "Hello " + recipientName + "," +
                "\n\nThis is a confirmation that your password was changed on " + changedAtText + "." +
                "\n\nIf this was not you, reset your password immediately:\n" + forgotPasswordUrl +
                "\n\nFor your security, you may also review recent account activity.";
    }

    private String buildPasswordChangedHtmlBodyFromTemplate(
            String toEmail,
            String recipientName,
            String changedAtText,
            String loginUrl,
            String forgotPasswordUrl) {
        Context context = new Context();
        context.setVariable("brandName", brandName);
        context.setVariable("recipientEmail", toEmail);
        context.setVariable("recipientName", recipientName);
        context.setVariable("changedAtText", changedAtText);
        context.setVariable("loginUrl", loginUrl);
        context.setVariable("forgotPasswordUrl", forgotPasswordUrl);
        context.setVariable("year", Year.now().getValue());
        return templateEngine.process("emails/password-changed-confirmation", context);
    }

    private String buildAccountLockedPlainTextBody(
            String recipientName,
            long lockDurationMinutes,
            String unlockAtText,
            String forgotPasswordUrl) {
        return "Hello " + recipientName + "," +
                "\n\nYour account has been temporarily locked due to repeated failed login attempts." +
                "\n\nLock duration: " + lockDurationMinutes + " minute(s)." +
                "\nUnlocks at: " + unlockAtText +
                "\n\nIf this was not you, reset your password now:\n" + forgotPasswordUrl +
                "\n\nFor security reasons, please review your account activity.";
    }

    private String buildAccountLockedHtmlBodyFromTemplate(
            String toEmail,
            String recipientName,
            long lockDurationMinutes,
            String unlockAtText,
            String forgotPasswordUrl) {
        Context context = new Context();
        context.setVariable("brandName", brandName);
        context.setVariable("recipientEmail", toEmail);
        context.setVariable("recipientName", recipientName);
        context.setVariable("lockDurationMinutes", lockDurationMinutes);
        context.setVariable("unlockAtText", unlockAtText);
        context.setVariable("forgotPasswordUrl", forgotPasswordUrl);
        context.setVariable("year", Year.now().getValue());
        return templateEngine.process("emails/account-locked-alert", context);
    }

    private void sendHtmlEmail(
            String toEmail,
            String subject,
            String plainTextBody,
            String htmlBody,
            String contextLabel) {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        try {
            MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
            messageHelper.setFrom(fromEmail);
            messageHelper.setTo(toEmail);
            messageHelper.setSubject(subject);
            messageHelper.setText(plainTextBody, htmlBody);
        } catch (MessagingException exception) {
            throw new IllegalStateException("Failed to build " + contextLabel + " message.", exception);
        }
        mailSender.send(mimeMessage);
    }
    /**
     * Resolves recipient name.
     */

    private String resolveRecipientName(String recipientName, String toEmail) {
        if (StringUtils.hasText(recipientName)) {
            return recipientName.trim();
        }

        if (!StringUtils.hasText(toEmail)) {
            return "there";
        }

        String localPart = toEmail.split("@")[0].trim();
        return StringUtils.hasText(localPart) ? localPart : "there";
    }
    /**
     * Builds frontend url.
     */

    private String buildFrontendUrl(String path) {
        String normalizedBaseUrl = normalizeBaseUrl(frontendUrl);
        String normalizedPath = path.startsWith("/") ? path : "/" + path;
        return normalizedBaseUrl + normalizedPath;
    }
    /**
     * Normalizes base url.
     */

    private String normalizeBaseUrl(String baseUrl) {
        String fallbackBaseUrl = "http://localhost:5173";
        if (!StringUtils.hasText(baseUrl)) {
            return fallbackBaseUrl;
        }

        String normalizedBaseUrl = baseUrl.trim();
        while (normalizedBaseUrl.endsWith("/")) {
            normalizedBaseUrl = normalizedBaseUrl.substring(0, normalizedBaseUrl.length() - 1);
        }
        return StringUtils.hasText(normalizedBaseUrl) ? normalizedBaseUrl : fallbackBaseUrl;
    }
    /**
     * Formats date time.
     */

    private String formatDateTime(LocalDateTime value) {
        if (value == null) {
            return "unknown";
        }
        return value.format(EMAIL_TIME_FORMATTER);
    }
    /**
     * Computes remaining lock minutes.
     */

    private long computeRemainingLockMinutes(LocalDateTime lockedUntil) {
        if (lockedUntil == null) {
            return 1;
        }
        long remainingMinutes = Duration.between(LocalDateTime.now(), lockedUntil).toMinutes();
        return Math.max(1, remainingMinutes);
    }
}
