package com.auth.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.Authentication;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@DisplayName("AuthPrincipalUtil")
class AuthPrincipalUtilTest {

    @Test
    @DisplayName("requireAuthenticatedEmail: returns trimmed principal name")
    void givenTrimmedPrincipalName_whenRequiringAuthenticatedEmail_thenReturnsNormalizedEmail() {
        // Arrange
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("  admin@example.com  ");

        // Act
        String email = AuthPrincipalUtil.requireAuthenticatedEmail(authentication);

        // Assert
        assertEquals("admin@example.com", email);
    }

    @Test
    @DisplayName("requireAuthenticatedEmail: blank principal throws IllegalArgumentException")
    void givenBlankPrincipalName_whenRequiringAuthenticatedEmail_thenThrowsIllegalArgumentException() {
        // Arrange
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn(" ");

        // Act + Assert
        assertThrows(IllegalArgumentException.class,
                () -> AuthPrincipalUtil.requireAuthenticatedEmail(authentication));
    }

    @Test
    @DisplayName("requireAuthenticatedEmail: null authentication throws IllegalArgumentException")
    void givenNullAuthentication_whenRequiringAuthenticatedEmail_thenThrowsIllegalArgumentException() {
        // Act + Assert
        assertThrows(IllegalArgumentException.class,
                () -> AuthPrincipalUtil.requireAuthenticatedEmail(null));
    }
}
