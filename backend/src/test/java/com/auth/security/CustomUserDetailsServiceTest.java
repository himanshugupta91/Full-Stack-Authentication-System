package com.auth.security;

import com.auth.entity.Role;
import com.auth.entity.RoleName;
import com.auth.entity.User;
import com.auth.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("CustomUserDetailsService")
class CustomUserDetailsServiceTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private CustomUserDetailsService customUserDetailsService;

    @Test
    @DisplayName("loadUserByUsername: normalizes email and builds Spring authorities")
    void loadUserByUsername_normalizesEmailAndBuildsAuthorities() {
        Role userRole = new Role();
        userRole.setName(RoleName.ROLE_USER);

        Role adminRole = new Role();
        adminRole.setName(RoleName.ROLE_ADMIN);

        User user = new User();
        user.setEmail("alice@example.com");
        user.setPassword("encoded");
        user.setEnabled(true);
        user.setRoles(Set.of(userRole, adminRole));

        when(userService.findByEmail("alice@example.com")).thenReturn(Optional.of(user));

        UserDetails userDetails = customUserDetailsService.loadUserByUsername(" Alice@Example.com ");

        verify(userService).findByEmail("alice@example.com");
        assertEquals("alice@example.com", userDetails.getUsername());
        assertEquals(2, userDetails.getAuthorities().size());
    }

    @Test
    @DisplayName("loadUserByUsername: unknown email → throws UsernameNotFoundException")
    void loadUserByUsername_whenUserMissing_throwsUsernameNotFoundException() {
        when(userService.findByEmail("missing@example.com")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class,
                () -> customUserDetailsService.loadUserByUsername("missing@example.com"));
    }

    @Test
    @DisplayName("loadUserByUsername: disabled account → UserDetails.isEnabled() returns false")
    void loadUserByUsername_whenUserDisabled_marksUserDetailsDisabled() {
        Role userRole = new Role();
        userRole.setName(RoleName.ROLE_USER);

        User user = new User();
        user.setEmail("disabled@example.com");
        user.setPassword("encoded");
        user.setEnabled(false);
        user.setRoles(Set.of(userRole));

        when(userService.findByEmail("disabled@example.com")).thenReturn(Optional.of(user));

        UserDetails userDetails = customUserDetailsService.loadUserByUsername("disabled@example.com");

        assertFalse(userDetails.isEnabled());
    }
}
