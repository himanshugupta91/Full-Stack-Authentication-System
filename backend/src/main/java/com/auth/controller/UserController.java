package com.auth.controller;

import com.auth.dto.request.ChangePasswordRequest;
import com.auth.dto.response.MessageResponse;
import com.auth.dto.UserDashboardDto;
import com.auth.dto.UserDto;
import com.auth.service.AuthService;
import com.auth.service.UserPortalService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for user dashboard.
 * Protected endpoints accessible only to authenticated users.
 */
@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final AuthService authService;

    private final UserPortalService userPortalService;

    /**
     * Get user dashboard data.
     * GET /api/user/dashboard
     */
    @GetMapping("/dashboard")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<UserDashboardDto> getDashboard(Authentication authentication) {
        String authenticatedEmail = authentication.getName();
        UserDashboardDto dashboard = userPortalService.getDashboard(authenticatedEmail);
        return ResponseEntity.ok(dashboard);
    }

    /**
     * Get user profile.
     * GET /api/user/profile
     */
    @GetMapping("/profile")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<UserDto> getProfile(Authentication authentication) {
        String authenticatedEmail = authentication.getName();
        UserDto profile = userPortalService.getProfile(authenticatedEmail);
        return ResponseEntity.ok(profile);
    }

    /**
     * Change password.
     * POST /api/user/change-password
     */
    @PostMapping("/change-password")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<MessageResponse> changePassword(
            Authentication authentication,
            @Valid @RequestBody ChangePasswordRequest request) {
        MessageResponse response = authService.changePassword(authentication.getName(), request);
        return ResponseEntity.ok(response);
    }
}
