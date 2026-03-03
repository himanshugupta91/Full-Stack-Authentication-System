package com.auth.service.support;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Year;

/**
 * Service for sending emails (OTP verification, password reset).
 */
@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;
    private final SpringTemplateEngine templateEngine;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Value("${otp.expiration.minutes:5}")
    private int otpExpirationMinutes;

    @Value("${app.frontend-reset-password-url:http://localhost:5173/reset-password}")
    private String resetPasswordUrl;

    @Value("${auth.reset-token.expiration.minutes:30}")
    private int resetTokenExpirationMinutes;

    @Value("${app.email.brand-name:Authentication System}")
    private String brandName;

    /**
     * Send OTP verification email.
     */
    public void sendOtpEmail(String toEmail, String otp) {
        MimeMessage mimeMessage = mailSender.createMimeMessage();

        String subject = brandName + " - Verify Your Email";
        String plainTextBody = buildOtpPlainTextBody(otp);
        String htmlBody = buildOtpHtmlBodyFromTemplate(toEmail, otp);

        try {
            MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
            messageHelper.setFrom(fromEmail);
            messageHelper.setTo(toEmail);
            messageHelper.setSubject(subject);
            messageHelper.setText(plainTextBody, htmlBody);
        } catch (MessagingException exception) {
            throw new IllegalStateException("Failed to build OTP email message.", exception);
        }

        mailSender.send(mimeMessage);
    }

    /**
     * Send password reset email with token link.
     */
    public void sendPasswordResetEmail(String toEmail, String resetToken) {
        String resetLink = resetPasswordUrl + "?token=" + URLEncoder.encode(resetToken, StandardCharsets.UTF_8);
        String emailBody = "Click the link below to reset your password:\n\n" + resetLink +
                "\n\nThis link will expire in " + resetTokenExpirationMinutes + " minutes." +
                "\n\nIf you didn't request this, please ignore this email.";

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(toEmail);
        message.setSubject("Password Reset Request");
        message.setText(emailBody);

        mailSender.send(message);
    }

    private String buildOtpPlainTextBody(String otp) {
        return "Your one-time verification code is: " + otp +
                "\n\nThis OTP expires in " + otpExpirationMinutes + " minutes." +
                "\n\nIf you did not request this code, you can ignore this message.";
    }

    private String buildOtpHtmlBodyFromTemplate(String toEmail, String otp) {
        Context context = new Context();
        context.setVariable("brandName", brandName);
        context.setVariable("recipientEmail", toEmail);
        context.setVariable("otp", otp);
        context.setVariable("otpExpirationMinutes", otpExpirationMinutes);
        context.setVariable("year", Year.now().getValue());
        return templateEngine.process("emails/otp-verification", context);
    }
}
