package com.auth.controller;

import com.auth.config.ApiPaths;
import com.auth.dto.response.AdminDashboardDto;
import com.auth.dto.response.ApiResponse;
import com.auth.dto.response.UserDto;
import com.auth.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for admin dashboard.
 * Protected endpoints accessible only to ADMIN users.
 */
@RestController
@RequestMapping(ApiPaths.ADMIN_V1)
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    /**
     * Get admin dashboard data.
     * GET /api/v1/admin/dashboard
     */
    @GetMapping("/dashboard")
    public ResponseEntity<ApiResponse<AdminDashboardDto>> getDashboard(Authentication authentication) {
        String authenticatedEmail = authentication.getName();
        AdminDashboardDto dashboard = adminService.getDashboard(authenticatedEmail);
        return ResponseEntity.ok(ApiResponse.ok(dashboard));
    }

    /**
     * Get users list with pagination/filtering/search.
     * GET /api/v1/admin/users
     */
    @GetMapping("/users")
    public ResponseEntity<ApiResponse<Page<UserDto>>> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Boolean enabled,
            @RequestParam(required = false) String role,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        Page<UserDto> users = adminService.getUsers(page, size, search, enabled, role, sortBy, sortDir);
        return ResponseEntity.ok(ApiResponse.ok(users));
    }
}
