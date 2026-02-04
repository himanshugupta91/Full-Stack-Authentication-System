package com.auth.controller;

import com.auth.dto.AdminDashboardDto;
import com.auth.dto.UserDto;
import com.auth.entity.User;
import com.auth.mapper.UserMapper;
import com.auth.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @Autowired
    private UserMapper userMapper;

    /**
     * Get admin dashboard data.
     * GET /api/admin/dashboard
     */
    @GetMapping("/dashboard")
    public ResponseEntity<AdminDashboardDto> getDashboard() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        long totalUsers = userRepository.count();
        long activeUsers = userRepository.findAll().stream()
                .filter(User::isEnabled)
                .count();

        AdminDashboardDto response = new AdminDashboardDto(
                "Welcome to Admin Dashboard!",
                auth.getName(),
                totalUsers,
                activeUsers,
                java.time.LocalDateTime.now().toString());

        return ResponseEntity.ok(response);
    }

    /**
     * Get all users list.
     * GET /api/admin/users
     */
    @GetMapping("/users")
    public ResponseEntity<List<UserDto>> getAllUsers() {
        List<User> users = userRepository.findAll();
        List<UserDto> userDtos = userMapper.toDtoList(users);
        return ResponseEntity.ok(userDtos);
    }
}
