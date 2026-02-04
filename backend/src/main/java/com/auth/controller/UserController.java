package com.auth.controller;

import com.auth.dto.ChangePasswordRequest;
import com.auth.dto.MessageResponse;
import com.auth.dto.UserDashboardDto;
import com.auth.dto.UserDto;
import com.auth.mapper.UserMapper;
import com.auth.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

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

    @Autowired
    private UserMapper userMapper;

    /**
     * Get user dashboard data.
     * GET /api/user/dashboard
     */
    @GetMapping("/dashboard")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<UserDashboardDto> getDashboard() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        com.auth.entity.User user = userService.getUserByEmail(auth.getName());

        UserDashboardDto response = userMapper.toUserDashboardDto(user);
        response.setMessage("Welcome to User Dashboard!");
        response.setTimestamp(java.time.LocalDateTime.now().toString());

        return ResponseEntity.ok(response);
    }

    /**
     * Get user profile.
     * GET /api/user/profile
     */
    @GetMapping("/profile")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<UserDto> getProfile() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        com.auth.entity.User user = userService.getUserByEmail(auth.getName());
        UserDto userDto = userMapper.toDto(user);
        return ResponseEntity.ok(userDto);
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
