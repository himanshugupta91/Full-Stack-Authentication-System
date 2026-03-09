package com.auth.controller;

import com.auth.config.ApiPaths;
import com.auth.dto.request.ChangePasswordRequest;
import com.auth.dto.response.ApiResponse;
import com.auth.dto.response.MessageResponse;
import com.auth.dto.response.UserDashboardDto;
import com.auth.dto.response.UserDto;
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
@RequestMapping(ApiPaths.USER_V1)
@RequiredArgsConstructor
public class UserController {

    private final AuthService authService;

    private final UserPortalService userPortalService;

    /**
     * Get user dashboard data.
     * GET /api/v1/user/dashboard
     */
    @GetMapping("/dashboard")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<UserDashboardDto>> getDashboard(Authentication authentication) {
        String authenticatedEmail = authentication.getName();
        UserDashboardDto dashboard = userPortalService.getDashboard(authenticatedEmail);
        return ResponseEntity.ok(ApiResponse.ok(dashboard));
    }

    /**
     * Get user profile.
     * GET /api/v1/user/profile
     */
    @GetMapping("/profile")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<UserDto>> getProfile(Authentication authentication) {
        String authenticatedEmail = authentication.getName();
        UserDto profile = userPortalService.getProfile(authenticatedEmail);
        return ResponseEntity.ok(ApiResponse.ok(profile));
    }

    /**
     * Change password.
     * POST /api/v1/user/change-password
     */
    @PostMapping("/change-password")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<MessageResponse>> changePassword(
            Authentication authentication,
            @Valid @RequestBody ChangePasswordRequest request) {
        MessageResponse response = authService.changePassword(authentication.getName(), request);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }
}
