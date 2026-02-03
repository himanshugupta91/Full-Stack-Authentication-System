package com.auth.controller;

import com.auth.dto.ChangePasswordRequest;
import com.auth.dto.MessageResponse;
import com.auth.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * REST controller for user dashboard.
 * Protected endpoints accessible only to authenticated users.
 */
@RestController
@RequestMapping("/api/user")
@CrossOrigin(origins = { "http://localhost:5173", "http://localhost:3000" })
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * Get user dashboard data.
     * GET /api/user/dashboard
     */
    @GetMapping("/dashboard")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> getDashboard() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Welcome to User Dashboard!");
        response.put("user", auth.getName());
        response.put("roles", auth.getAuthorities());
        response.put("timestamp", java.time.LocalDateTime.now().toString());

        return ResponseEntity.ok(response);
    }

    /**
     * Get user profile.
     * GET /api/user/profile
     */
    @GetMapping("/profile")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> getProfile() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        Map<String, Object> response = new HashMap<>();
        response.put("email", auth.getName());
        response.put("roles", auth.getAuthorities());

        return ResponseEntity.ok(response);
    }

    /**
     * Change password.
     * POST /api/user/change-password
     */
    @PostMapping("/change-password")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<MessageResponse> changePassword(@Valid @RequestBody ChangePasswordRequest request) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();

        MessageResponse response = userService.changePassword(email, request);
        return ResponseEntity.ok(response);
    }
}
