package com.auth.controller;

import com.auth.dto.AdminDashboardDto;
import com.auth.dto.UserDto;
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
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminController {

    private static final String DEFAULT_PAGE = "0";
    private static final String DEFAULT_PAGE_SIZE = "20";
    private static final String DEFAULT_SORT_FIELD = "createdAt";
    private static final String DEFAULT_SORT_DIRECTION = "desc";

    private final AdminService adminService;

    /**
     * Get admin dashboard data.
     * GET /api/admin/dashboard
     */
    @GetMapping("/dashboard")
    public ResponseEntity<AdminDashboardDto> getDashboard(Authentication authentication) {
        return ResponseEntity.ok(adminService.getDashboard(authentication.getName()));
    }

    /**
     * Get users list with pagination/filtering/search.
     * GET /api/admin/users
     */
    @GetMapping("/users")
    public ResponseEntity<Page<UserDto>> getAllUsers(
            @RequestParam(defaultValue = DEFAULT_PAGE) int page,
            @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int size,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Boolean enabled,
            @RequestParam(required = false) String role,
            @RequestParam(defaultValue = DEFAULT_SORT_FIELD) String sortBy,
            @RequestParam(defaultValue = DEFAULT_SORT_DIRECTION) String sortDir) {
        return ResponseEntity.ok(adminService.getUsers(page, size, search, enabled, role, sortBy, sortDir));
    }
}
