package com.auth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.TimeZone;

/**
 * Main application class for the Authentication Backend.
 * This Spring Boot application provides:
 * - JWT-based authentication
 * - OTP email verification
 * - Password reset functionality
 * - Role-based access control
 */
@SpringBootApplication
public class AuthApplication {

    /** Starts the Spring Boot authentication application. */
    public static void main(String[] args) {
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Kolkata"));
        SpringApplication.run(AuthApplication.class, args);
    }
}
