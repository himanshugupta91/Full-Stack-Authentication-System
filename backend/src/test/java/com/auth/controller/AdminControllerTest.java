package com.auth.controller;

import com.auth.dto.response.AdminDashboardDto;
import com.auth.dto.response.ApiResponse;
import com.auth.dto.response.UserDto;
import com.auth.service.AdminService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AdminControllerTest {

    @Mock
    private AdminService adminService;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private AdminController adminController;

    @Test
    void getDashboard_whenAuthenticated_returnsDashboardPayload() {
        AdminDashboardDto dashboard = new AdminDashboardDto(
                "Welcome to Admin Dashboard!",
                "admin@example.com",
                20L,
                12L,
                "09 Mar 2026, 01:45:00 PM");
        when(authentication.getName()).thenReturn("admin@example.com");
        when(adminService.getDashboard("admin@example.com")).thenReturn(dashboard);

        ResponseEntity<ApiResponse<AdminDashboardDto>> response = adminController.getDashboard(authentication);

        verify(adminService).getDashboard("admin@example.com");
        assertTrue(response.getBody().isSuccess());
        assertEquals(dashboard, response.getBody().getData());
    }

    @Test
    void getAllUsers_whenQueryParamsProvided_returnsPagedUsers() {
        UserDto user = new UserDto();
        user.setId(3L);
        user.setName("Alice");
        user.setEmail("alice@example.com");
        user.setLoginSource("GOOGLE");
        user.setRoles(Set.of("ROLE_USER"));
        user.setEnabled(true);
        user.setCreatedAt("08 Mar 2026, 10:10:10 AM");
        Page<UserDto> usersPage = new PageImpl<>(List.of(user));

        when(adminService.getUsers(1, 20, "alice", true, "USER", "createdAt", "desc"))
                .thenReturn(usersPage);

        ResponseEntity<ApiResponse<Page<UserDto>>> response = adminController.getAllUsers(
                1,
                20,
                "alice",
                true,
                "USER",
                "createdAt",
                "desc");

        verify(adminService).getUsers(1, 20, "alice", true, "USER", "createdAt", "desc");
        assertTrue(response.getBody().isSuccess());
        assertEquals(usersPage, response.getBody().getData());
    }
}
