package com.auth.controller;

import com.auth.dto.response.AdminDashboardDto;
import com.auth.dto.response.ApiResponse;
import com.auth.dto.response.UserDto;
import com.auth.service.AdminService;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.ValidatorFactory;
import jakarta.validation.executable.ExecutableValidator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("AdminController")
class AdminControllerTest {

    @Mock
    private AdminService adminService;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private AdminController adminController;

    @Test
    @DisplayName("getDashboard: authenticated admin → returns dashboard payload")
    void givenAuthenticatedAdmin_whenGettingDashboard_thenReturnsDashboardPayload() {
        // Arrange
        AdminDashboardDto dashboard = new AdminDashboardDto(
                "Welcome to Admin Dashboard!",
                "admin@example.com",
                20L,
                12L,
                "09 Mar 2026, 01:45:00 PM");
        when(authentication.getName()).thenReturn("admin@example.com");
        when(adminService.getDashboard("admin@example.com")).thenReturn(dashboard);

        // Act
        ResponseEntity<ApiResponse<AdminDashboardDto>> response = adminController.getDashboard(authentication);

        // Assert
        verify(adminService).getDashboard("admin@example.com");
        assertTrue(response.getBody().isSuccess());
        assertEquals(dashboard, response.getBody().getData());
    }

    @Test
    @DisplayName("getDashboard: blank principal → throws IllegalArgumentException")
    void givenBlankPrincipal_whenGettingDashboard_thenThrowsIllegalArgumentException() {
        // Arrange
        when(authentication.getName()).thenReturn(" ");

        // Act + Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> adminController.getDashboard(authentication));

        // Assert
        assertEquals("Authenticated principal is required.", exception.getMessage());
        verifyNoInteractions(adminService);
    }

    @Test
    @DisplayName("getAllUsers: query params provided → returns paged user list")
    void givenQueryParameters_whenGettingAllUsers_thenReturnsPagedUsers() {
        // Arrange
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

        // Act
        ResponseEntity<ApiResponse<Page<UserDto>>> response = adminController.getAllUsers(
                1,
                20,
                "alice",
                true,
                "USER",
                "createdAt",
                "desc");

        // Assert
        verify(adminService).getUsers(1, 20, "alice", true, "USER", "createdAt", "desc");
        assertTrue(response.getBody().isSuccess());
        assertEquals(usersPage, response.getBody().getData());
    }

    @Test
    @DisplayName("getAllUsers validation: page below zero → violates @Min")
    void givenNegativePage_whenValidatingGetAllUsers_thenReportsPageViolation() throws Exception {
        // Act
        Set<ConstraintViolation<AdminController>> violations = validateGetAllUsersParameters(
                -1, 20, null, null, "USER", "createdAt", "desc");

        // Assert
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v ->
                v.getPropertyPath().toString().contains("getAllUsers.page")));
    }

    @Test
    @DisplayName("getAllUsers validation: size above max → violates @Max")
    void givenOversizedPageSize_whenValidatingGetAllUsers_thenReportsSizeViolation() throws Exception {
        // Act
        Set<ConstraintViolation<AdminController>> violations = validateGetAllUsersParameters(
                0, 101, null, null, "USER", "createdAt", "desc");

        // Assert
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v ->
                v.getPropertyPath().toString().contains("getAllUsers.size")));
    }

    @Test
    @DisplayName("getAllUsers validation: unsupported role → violates @Pattern")
    void givenUnsupportedRole_whenValidatingGetAllUsers_thenReportsRoleViolation() throws Exception {
        // Act
        Set<ConstraintViolation<AdminController>> violations = validateGetAllUsersParameters(
                0, 20, null, null, "SUPER_ADMIN", "createdAt", "desc");

        // Assert
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v ->
                v.getPropertyPath().toString().contains("getAllUsers.role")));
    }

    @Test
    @DisplayName("getAllUsers validation: invalid sortDir → violates @Pattern")
    void givenInvalidSortDirection_whenValidatingGetAllUsers_thenReportsSortDirectionViolation() throws Exception {
        // Act
        Set<ConstraintViolation<AdminController>> violations = validateGetAllUsersParameters(
                0, 20, null, null, "USER", "createdAt", "down");

        // Assert
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v ->
                v.getPropertyPath().toString().contains("getAllUsers.sortDir")));
    }

    @Test
    @DisplayName("getAllUsers validation: lowercase role and sortDir are accepted")
    void givenLowercaseRoleAndSortDirection_whenValidatingGetAllUsers_thenReportsNoViolations() throws Exception {
        // Act
        Set<ConstraintViolation<AdminController>> violations = validateGetAllUsersParameters(
                0, 20, "alice", true, "role_user", "createdAt", "asc");

        // Assert
        assertTrue(violations.isEmpty());
    }

    private Set<ConstraintViolation<AdminController>> validateGetAllUsersParameters(
            int page,
            int size,
            String search,
            Boolean enabled,
            String role,
            String sortBy,
            String sortDir) throws Exception {
        Method method = AdminController.class.getMethod(
                "getAllUsers",
                int.class,
                int.class,
                String.class,
                Boolean.class,
                String.class,
                String.class,
                String.class);

        try (ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory()) {
            ExecutableValidator executableValidator = validatorFactory.getValidator().forExecutables();
            return executableValidator.validateParameters(adminController, method, new Object[] {
                    page,
                    size,
                    search,
                    enabled,
                    role,
                    sortBy,
                    sortDir
            });
        }
    }
}
