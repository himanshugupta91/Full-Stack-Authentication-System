package com.auth.service.auth;

import com.auth.entity.Role;
import com.auth.entity.RoleName;
import com.auth.entity.User;
import com.auth.service.RoleService;
import com.auth.service.UserService;
import org.junit.jupiter.api.DisplayName;
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
@DisplayName("OAuth2UserProvisioningService")
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
    @DisplayName("loadOrCreateUser: existing user needs no update → returns without saving")
    void givenExistingUserWithNoChanges_whenLoadingOrCreating_thenReturnsWithoutSaving() {
        // Arrange
        User existing = new User();
        existing.setEmail("alice@example.com");
        existing.setName("Alice");
        existing.setEnabled(true);
        existing.setAuthProvider("google");
        existing.setAuthProviderUserId("google-sub-1");

        OAuth2AuthenticationToken token = oauthToken(
                "google",
                Map.of("sub", "google-sub-1", "email", "alice@example.com", "name", "Alice Changed"));

        when(userService.findByAuthProviderAndAuthProviderUserId("google", "google-sub-1")).thenReturn(Optional.empty());
        when(userService.findByEmail("alice@example.com")).thenReturn(Optional.of(existing));

        // Act
        User resolved = service.loadOrCreateUser(token, token.getPrincipal());

        // Assert
        assertEquals(existing, resolved);
        verify(userService, never()).save(any(User.class));
        verifyNoInteractions(roleService, passwordEncoder);
    }

    @Test
    @DisplayName("loadOrCreateUser: existing user has missing data → updates only changed fields")
    void givenExistingUserWithMissingFields_whenLoadingOrCreating_thenUpdatesOnlyChangedFields() {
        // Arrange
        User existing = new User();
        existing.setEmail("alice@example.com");
        existing.setName(" ");
        existing.setEnabled(false);
        existing.setAuthProvider(" ");

        OAuth2AuthenticationToken token = oauthToken(
                "google",
                Map.of("sub", "google-sub-2", "email", "  alice@example.com  ", "name", "  Alice  "));

        when(userService.findByAuthProviderAndAuthProviderUserId("google", "google-sub-2")).thenReturn(Optional.empty());
        when(userService.findByEmail("alice@example.com")).thenReturn(Optional.of(existing));
        when(userService.save(existing)).thenReturn(existing);

        // Act
        User resolved = service.loadOrCreateUser(token, token.getPrincipal());

        // Assert
        assertTrue(resolved.isEnabled());
        assertEquals("google", resolved.getAuthProvider());
        assertEquals("google-sub-2", resolved.getAuthProviderUserId());
        assertEquals("Alice", resolved.getName());
        verify(userService).save(existing);
        verifyNoInteractions(roleService, passwordEncoder);
    }

    @Test
    @DisplayName("loadOrCreateUser: new user → persists OAuth user with default USER role")
    void givenNewOAuthUser_whenLoadingOrCreating_thenPersistsUserWithDefaultRole() {
        // Arrange
        Role role = new Role();
        role.setName(RoleName.ROLE_USER);

        OAuth2AuthenticationToken token = oauthToken(
                "google",
                Map.of("sub", "google-sub-3", "email", "new.user@example.com", "name", "New User"));

        when(userService.findByAuthProviderAndAuthProviderUserId("google", "google-sub-3")).thenReturn(Optional.empty());
        when(userService.findByEmail("new.user@example.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode(anyString())).thenReturn("encoded-password");
        when(roleService.findOrCreateRole(RoleName.ROLE_USER)).thenReturn(role);
        when(userService.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        User resolved = service.loadOrCreateUser(token, token.getPrincipal());

        // Assert
        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userService).save(captor.capture());
        User saved = captor.getValue();

        assertEquals("new.user@example.com", saved.getEmail());
        assertEquals("New User", saved.getName());
        assertEquals("google", saved.getAuthProvider());
        assertEquals("google-sub-3", saved.getAuthProviderUserId());
        assertTrue(saved.isEnabled());
        assertEquals("encoded-password", saved.getPassword());
        assertEquals(Set.of(role), saved.getRoles());
        assertEquals(saved, resolved);
    }

    @Test
    @DisplayName("loadOrCreateUser: GitHub email missing → uses no-reply fallback email")
    void givenGithubUserWithoutEmail_whenLoadingOrCreating_thenUsesNoreplyEmailFallback() {
        // Arrange
        Role role = new Role();
        role.setName(RoleName.ROLE_USER);

        OAuth2AuthenticationToken token = oauthToken(
                "github",
                Map.of("login", "octocat", "name", "The Cat", "id", "42"));

        when(userService.findByAuthProviderAndAuthProviderUserId("github", "42")).thenReturn(Optional.empty());
        when(userService.findByEmail("octocat@users.noreply.github.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode(anyString())).thenReturn("encoded-password");
        when(roleService.findOrCreateRole(RoleName.ROLE_USER)).thenReturn(role);
        when(userService.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        User resolved = service.loadOrCreateUser(token, token.getPrincipal());

        // Assert
        assertEquals("octocat@users.noreply.github.com", resolved.getEmail());
        assertEquals("42", resolved.getAuthProviderUserId());
    }

    @Test
    @DisplayName("loadOrCreateUser: email unavailable for provider → throws IllegalArgumentException")
    void givenProviderWithoutResolvableEmail_whenLoadingOrCreating_thenThrowsIllegalArgumentException() {
        // Arrange
        OAuth2AuthenticationToken token = oauthToken("google", Map.of("name", "Missing Email"));

        // Act + Assert
        assertThrows(IllegalArgumentException.class, () -> service.loadOrCreateUser(token, token.getPrincipal()));

        // Assert
        verify(userService, never()).save(any(User.class));
        verifyNoInteractions(roleService, passwordEncoder);
    }

    @Test
    @DisplayName("loadOrCreateUser: Apple email missing → uses provider-derived email")
    void givenAppleUserWithoutEmail_whenLoadingOrCreating_thenUsesProviderDerivedFallbackEmail() {
        // Arrange
        Role role = new Role();
        role.setName(RoleName.ROLE_USER);

        OAuth2AuthenticationToken token = oauthToken(
                "apple",
                Map.of("sub", "apple-user-77", "name", "Apple User"));

        when(userService.findByAuthProviderAndAuthProviderUserId("apple", "apple-user-77")).thenReturn(Optional.empty());
        when(userService.findByEmail("apple-apple-user-77@oauth.local")).thenReturn(Optional.empty());
        when(passwordEncoder.encode(anyString())).thenReturn("encoded-password");
        when(roleService.findOrCreateRole(RoleName.ROLE_USER)).thenReturn(role);
        when(userService.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        User resolved = service.loadOrCreateUser(token, token.getPrincipal());

        // Assert
        assertEquals("apple-apple-user-77@oauth.local", resolved.getEmail());
        assertEquals("apple-user-77", resolved.getAuthProviderUserId());
        assertEquals("apple", resolved.getAuthProvider());
    }

    @Test
    @DisplayName("loadOrCreateUser: found by provider ID → skips email lookup")
    void givenUserFoundByProviderId_whenLoadingOrCreating_thenSkipsEmailLookup() {
        // Arrange
        User existing = new User();
        existing.setEmail("linked@example.com");
        existing.setName("Linked User");
        existing.setEnabled(true);
        existing.setAuthProvider("linkedin");
        existing.setAuthProviderUserId("linkedin-sub-22");

        OAuth2AuthenticationToken token = oauthToken(
                "linkedin",
                Map.of("sub", "linkedin-sub-22", "name", "Linked User Updated"));

        when(userService.findByAuthProviderAndAuthProviderUserId("linkedin", "linkedin-sub-22"))
                .thenReturn(Optional.of(existing));

        // Act
        User resolved = service.loadOrCreateUser(token, token.getPrincipal());

        // Assert
        assertEquals(existing, resolved);
        verify(userService, never()).findByEmail(anyString());
        verify(userService, never()).save(any(User.class));
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
