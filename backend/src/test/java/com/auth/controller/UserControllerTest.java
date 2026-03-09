package com.auth.controller;

import com.auth.dto.request.ChangePasswordRequest;
import com.auth.dto.response.ApiResponse;
import com.auth.dto.response.MessageResponse;
import com.auth.dto.response.UserDashboardDto;
import com.auth.dto.response.UserDto;
import com.auth.service.AuthService;
import com.auth.service.UserPortalService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private AuthService authService;

    @Mock
    private UserPortalService userPortalService;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private UserController userController;

    @Test
    void getDashboard_whenAuthenticated_returnsDashboardPayload() {
        UserDashboardDto dashboard = new UserDashboardDto(
                "Welcome to User Dashboard!",
                "Alice",
                "alice@example.com",
                List.of("ROLE_USER"),
                "09 Mar 2026, 01:30:00 PM");
        when(authentication.getName()).thenReturn("alice@example.com");
        when(userPortalService.getDashboard("alice@example.com")).thenReturn(dashboard);

        ResponseEntity<ApiResponse<UserDashboardDto>> response = userController.getDashboard(authentication);

        verify(userPortalService).getDashboard("alice@example.com");
        assertTrue(response.getBody().isSuccess());
        assertEquals(dashboard, response.getBody().getData());
    }

    @Test
    void getProfile_whenAuthenticated_returnsProfilePayload() {
        UserDto profile = new UserDto();
        profile.setId(7L);
        profile.setName("Alice");
        profile.setEmail("alice@example.com");
        profile.setLoginSource("EMAIL_PASSWORD");
        profile.setRoles(Set.of("ROLE_USER"));
        profile.setEnabled(true);
        profile.setCreatedAt("09 Mar 2026, 11:00:00 AM");

        when(authentication.getName()).thenReturn("alice@example.com");
        when(userPortalService.getProfile("alice@example.com")).thenReturn(profile);

        ResponseEntity<ApiResponse<UserDto>> response = userController.getProfile(authentication);

        verify(userPortalService).getProfile("alice@example.com");
        assertTrue(response.getBody().isSuccess());
        assertEquals(profile, response.getBody().getData());
    }

    @Test
    void changePassword_whenValidRequest_returnsSuccessMessage() {
        ChangePasswordRequest request = new ChangePasswordRequest();
        request.setCurrentPassword("OldPass123!");
        request.setNewPassword("NewPass123!");

        MessageResponse messageResponse = new MessageResponse("Password changed successfully!", true);

        when(authentication.getName()).thenReturn("alice@example.com");
        when(authService.changePassword("alice@example.com", request)).thenReturn(messageResponse);

        ResponseEntity<ApiResponse<MessageResponse>> response = userController.changePassword(authentication, request);

        verify(authService).changePassword("alice@example.com", request);
        assertTrue(response.getBody().isSuccess());
        assertEquals(messageResponse, response.getBody().getData());
    }
}
