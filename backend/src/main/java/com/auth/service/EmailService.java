package com.auth.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * Service for sending emails (OTP verification, password reset).
 */
@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Value("${otp.expiration.minutes:5}")
    private int otpExpirationMinutes;

    @Value("${app.frontend-reset-password-url:http://localhost:5173/reset-password}")
    private String resetPasswordUrl;

    /**
     * Send OTP verification email.
     */
    public void sendOtpEmail(String toEmail, String otp) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(toEmail);
        message.setSubject("Email Verification OTP");
        message.setText("Your OTP for email verification is: " + otp +
                "\n\nThis OTP will expire in " + otpExpirationMinutes + " minutes." +
                "\n\nIf you didn't request this, please ignore this email.");

        mailSender.send(message);
    }

    /**
     * Send password reset email with token link.
     */
    public void sendPasswordResetEmail(String toEmail, String resetToken) {
        String resetLink = resetPasswordUrl + "?token=" + URLEncoder.encode(resetToken, StandardCharsets.UTF_8);

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(toEmail);
        message.setSubject("Password Reset Request");
        message.setText("Click the link below to reset your password:\n\n" + resetLink +
                "\n\nThis link will expire in 30 minutes." +
                "\n\nIf you didn't request this, please ignore this email.");

        mailSender.send(message);
    }
}
