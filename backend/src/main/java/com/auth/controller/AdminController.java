package com.auth.controller;

import com.auth.entity.User;
import com.auth.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * REST controller for admin dashboard.
 * Protected endpoints accessible only to ADMIN users.
 */
@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = { "http://localhost:5173", "http://localhost:3000" })
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    @Autowired
    private UserRepository userRepository;

    /**
     * Get admin dashboard data.
     * GET /api/admin/dashboard
     */
    @GetMapping("/dashboard")
    public ResponseEntity<Map<String, Object>> getDashboard() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        long totalUsers = userRepository.count();
        long activeUsers = userRepository.findAll().stream()
                .filter(User::isEnabled)
                .count();

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Welcome to Admin Dashboard!");
        response.put("admin", auth.getName());
        response.put("totalUsers", totalUsers);
        response.put("activeUsers", activeUsers);
        response.put("timestamp", java.time.LocalDateTime.now().toString());

        return ResponseEntity.ok(response);
    }

    /**
     * Get all users list.
     * GET /api/admin/users
     */
    @GetMapping("/users")
    public ResponseEntity<List<Map<String, Object>>> getAllUsers() {
        List<User> users = userRepository.findAll();

        List<Map<String, Object>> userList = users.stream()
                .map(user -> {
                    Map<String, Object> userMap = new HashMap<>();
                    userMap.put("id", user.getId());
                    userMap.put("name", user.getName());
                    userMap.put("email", user.getEmail());
                    userMap.put("enabled", user.isEnabled());
                    userMap.put("roles", user.getRoles().stream()
                            .map(role -> role.getName().name())
                            .collect(Collectors.toList()));
                    userMap.put("createdAt", user.getCreatedAt());
                    return userMap;
                })
                .collect(Collectors.toList());

        return ResponseEntity.ok(userList);
    }
}
