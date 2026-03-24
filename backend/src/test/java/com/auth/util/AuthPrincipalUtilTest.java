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
    void requireAuthenticatedEmail_whenPrincipalPresent_returnsTrimmedName() {
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("  admin@example.com  ");

        String email = AuthPrincipalUtil.requireAuthenticatedEmail(authentication);

        assertEquals("admin@example.com", email);
    }

    @Test
    @DisplayName("requireAuthenticatedEmail: blank principal throws IllegalArgumentException")
    void requireAuthenticatedEmail_whenPrincipalBlank_throwsIllegalArgumentException() {
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn(" ");

        assertThrows(IllegalArgumentException.class,
                () -> AuthPrincipalUtil.requireAuthenticatedEmail(authentication));
    }

    @Test
    @DisplayName("requireAuthenticatedEmail: null authentication throws IllegalArgumentException")
    void requireAuthenticatedEmail_whenAuthenticationNull_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class,
                () -> AuthPrincipalUtil.requireAuthenticatedEmail(null));
    }
}
