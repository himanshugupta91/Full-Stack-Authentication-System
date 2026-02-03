package com.auth.controller;

import com.auth.dto.ChangePasswordRequest;
import com.auth.dto.MessageResponse;
import com.auth.service.AuthService;
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
public class UserController {

    @Autowired
    private AuthService authService;

    @Autowired
    private com.auth.service.UserService userService;

    /**
     * Get user dashboard data.
     * GET /api/user/dashboard
     */
    @GetMapping("/dashboard")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> getDashboard() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        com.auth.entity.User user = userService.getUserByEmail(auth.getName());

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Welcome to User Dashboard!");
        response.put("user", user.getName());
        response.put("email", user.getEmail());
        response.put("roles", user.getRoles().stream()
                .map(role -> role.getName().name())
                .collect(java.util.stream.Collectors.toList()));
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
        com.auth.entity.User user = userService.getUserByEmail(auth.getName());

        Map<String, Object> response = new HashMap<>();
        response.put("id", user.getId());
        response.put("name", user.getName());
        response.put("email", user.getEmail());
        response.put("roles", user.getRoles().stream()
                .map(role -> role.getName().name())
                .collect(java.util.stream.Collectors.toList()));
        response.put("createdAt", user.getCreatedAt());
        response.put("enabled", user.isEnabled());

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

        MessageResponse response = authService.changePassword(email, request);
        return ResponseEntity.ok(response);
    }
}
