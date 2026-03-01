package com.auth.service.auth;

import com.auth.entity.Role;
import com.auth.entity.User;
import com.auth.service.RoleService;
import com.auth.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OAuth2UserProvisioningServiceTest {

    @Mock
    private UserService userService;

    @Mock
    private RoleService roleService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private OAuth2UserProvisioningService service;

    @Test
    void loadOrCreateUser_whenExistingUserNeedsNoUpdate_returnsWithoutSaving() {
        User existing = new User();
        existing.setEmail("alice@example.com");
        existing.setName("Alice");
        existing.setEnabled(true);
        existing.setAuthProvider("google");

        OAuth2AuthenticationToken token = oauthToken(
                "google",
                Map.of("email", "alice@example.com", "name", "Alice Changed"));

        when(userService.findByEmail("alice@example.com")).thenReturn(Optional.of(existing));

        User resolved = service.loadOrCreateUser(token, token.getPrincipal());

        assertEquals(existing, resolved);
        verify(userService, never()).save(any(User.class));
        verifyNoInteractions(roleService, passwordEncoder);
    }

    @Test
    void loadOrCreateUser_whenExistingUserHasMissingData_updatesOnlyChangedFields() {
        User existing = new User();
        existing.setEmail("alice@example.com");
        existing.setName(" ");
        existing.setEnabled(false);
        existing.setAuthProvider(" ");

        OAuth2AuthenticationToken token = oauthToken(
                "google",
                Map.of("email", "  alice@example.com  ", "name", "  Alice  "));

        when(userService.findByEmail("alice@example.com")).thenReturn(Optional.of(existing));
        when(userService.save(existing)).thenReturn(existing);

        User resolved = service.loadOrCreateUser(token, token.getPrincipal());

        assertTrue(resolved.isEnabled());
        assertEquals("google", resolved.getAuthProvider());
        assertEquals("Alice", resolved.getName());
        verify(userService).save(existing);
        verifyNoInteractions(roleService, passwordEncoder);
    }

    @Test
    void loadOrCreateUser_whenNewUser_persistsOAuthUserWithDefaultRole() {
        Role role = new Role();
        role.setName(Role.RoleName.ROLE_USER);

        OAuth2AuthenticationToken token = oauthToken(
                "google",
                Map.of("email", "new.user@example.com", "name", "New User"));

        when(userService.findByEmail("new.user@example.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode(anyString())).thenReturn("encoded-password");
        when(roleService.findOrCreateRole(Role.RoleName.ROLE_USER)).thenReturn(role);
        when(userService.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User resolved = service.loadOrCreateUser(token, token.getPrincipal());

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userService).save(captor.capture());
        User saved = captor.getValue();

        assertEquals("new.user@example.com", saved.getEmail());
        assertEquals("New User", saved.getName());
        assertEquals("google", saved.getAuthProvider());
        assertTrue(saved.isEnabled());
        assertEquals("encoded-password", saved.getPassword());
        assertEquals(Set.of(role), saved.getRoles());
        assertEquals(saved, resolved);
    }

    @Test
    void loadOrCreateUser_whenGithubEmailMissing_usesNoReplyFallbackEmail() {
        Role role = new Role();
        role.setName(Role.RoleName.ROLE_USER);

        OAuth2AuthenticationToken token = oauthToken(
                "github",
                Map.of("login", "octocat", "name", "The Cat"));

        when(userService.findByEmail("octocat@users.noreply.github.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode(anyString())).thenReturn("encoded-password");
        when(roleService.findOrCreateRole(Role.RoleName.ROLE_USER)).thenReturn(role);
        when(userService.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User resolved = service.loadOrCreateUser(token, token.getPrincipal());

        assertEquals("octocat@users.noreply.github.com", resolved.getEmail());
    }

    @Test
    void loadOrCreateUser_whenEmailUnavailableForProvider_throwsIllegalArgumentException() {
        OAuth2AuthenticationToken token = oauthToken("google", Map.of("name", "Missing Email"));

        assertThrows(IllegalArgumentException.class, () -> service.loadOrCreateUser(token, token.getPrincipal()));

        verify(userService, never()).save(any(User.class));
        verifyNoInteractions(roleService, passwordEncoder);
    }

    private OAuth2AuthenticationToken oauthToken(String registrationId, Map<String, Object> attributes) {
        String nameAttributeKey = attributes.containsKey("email") ? "email" : "name";
        OAuth2User oauth2User = new DefaultOAuth2User(
                Set.of(new SimpleGrantedAuthority("ROLE_USER")),
                attributes,
                nameAttributeKey);
        return new OAuth2AuthenticationToken(oauth2User, oauth2User.getAuthorities(), registrationId);
    }
}
