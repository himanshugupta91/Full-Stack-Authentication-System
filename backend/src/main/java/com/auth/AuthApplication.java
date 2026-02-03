package com.auth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

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

    public static void main(String[] args) {
        SpringApplication.run(AuthApplication.class, args);
    }
}
