package com.auth.service.auth;

import com.auth.dto.response.AuthTokens;
import com.auth.entity.Role;
import com.auth.entity.RoleName;
import com.auth.entity.User;
import com.auth.exception.TokenValidationException;
import com.auth.security.jwt.JwtUtil;
import com.auth.service.UserService;
import com.auth.service.support.TokenHashService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuthTokenService")
class AuthTokenServiceTest {

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private UserService userService;

    @Mock
    private TokenHashService tokenHashService;

    @InjectMocks
    private AuthTokenService authTokenService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(authTokenService, "refreshTokenExpirationMs", 3_600_000L);
    }

    @Test
    @DisplayName("issueTokens: valid user → persists hashed refresh token and builds auth response")
    void issueTokens_whenUserIsValid_persistsHashedRefreshTokenAndBuildsResponse() {
        User user = buildUser();

        when(jwtUtil.generateTokenFromEmailAndRoles(any(String.class), anyList())).thenReturn("access-token");
        when(jwtUtil.getAccessTokenExpiration()).thenReturn(900_000L);
        when(tokenHashService.hash(any(String.class))).thenReturn("hashed-refresh-token");
        when(userService.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        AuthTokens tokens = authTokenService.issueTokens(user);

        assertEquals("access-token", tokens.response().getAccessToken());
        assertEquals("Bearer", tokens.response().getTokenType());
        assertEquals(2, tokens.response().getRoles().size());
        assertNotNull(tokens.refreshToken());
        assertEquals("hashed-refresh-token", user.getRefreshToken());
        assertNotNull(user.getRefreshTokenExpiry());

        ArgumentCaptor<String> tokenCaptor = ArgumentCaptor.forClass(String.class);
        verify(tokenHashService).hash(tokenCaptor.capture());
        assertEquals(tokens.refreshToken(), tokenCaptor.getValue());

        @SuppressWarnings("unchecked")
        ArgumentCaptor<List<String>> rolesCaptor = ArgumentCaptor.forClass(List.class);
        verify(jwtUtil).generateTokenFromEmailAndRoles(
                org.mockito.ArgumentMatchers.eq(user.getEmail()),
                rolesCaptor.capture());
        assertEquals(2, rolesCaptor.getValue().size());
        assertTrue(rolesCaptor.getValue().contains("ROLE_USER"));
        assertTrue(rolesCaptor.getValue().contains("ROLE_ADMIN"));
    }

    @Test
    @DisplayName("refreshTokens: blank token → throws TokenValidationException")
    void refreshTokens_whenRefreshTokenBlank_throwsTokenValidationException() {
        assertThrows(TokenValidationException.class, () -> authTokenService.refreshTokens("  "));
    }

    @Test
    @DisplayName("refreshTokens: stored token missing → throws TokenValidationException")
    void refreshTokens_whenStoredTokenMissing_throwsTokenValidationException() {
        when(tokenHashService.hash("raw-refresh-token")).thenReturn("hashed");
        when(userService.findByRefreshToken("hashed")).thenReturn(Optional.empty());

        assertThrows(TokenValidationException.class, () -> authTokenService.refreshTokens("raw-refresh-token"));
    }

    @Test
    @DisplayName("refreshTokens: stored token expired → clears token and throws TokenValidationException")
    void refreshTokens_whenStoredTokenExpired_clearsTokenAndThrowsTokenValidationException() {
        User user = buildUser();
        user.setRefreshToken("hashed");
        user.setRefreshTokenExpiry(LocalDateTime.now().minusMinutes(1));

        when(tokenHashService.hash("raw-refresh-token")).thenReturn("hashed");
        when(userService.findByRefreshToken("hashed")).thenReturn(Optional.of(user));

        assertThrows(TokenValidationException.class, () -> authTokenService.refreshTokens("raw-refresh-token"));

        assertEquals(null, user.getRefreshToken());
        assertEquals(null, user.getRefreshTokenExpiry());
        verify(userService).save(user);
    }

    @Test
    @DisplayName("revokeRefreshToken: blank token → does not call repository")
    void revokeRefreshToken_whenTokenBlank_doesNotCallRepository() {
        authTokenService.revokeRefreshToken(" ");

        verify(tokenHashService, never()).hash(any(String.class));
        verify(userService, never()).findByRefreshToken(any(String.class));
    }

    private User buildUser() {
        Role userRole = new Role();
        userRole.setName(RoleName.ROLE_USER);

        Role adminRole = new Role();
        adminRole.setName(RoleName.ROLE_ADMIN);

        User user = new User();
        user.setId(7L);
        user.setName("Alice");
        user.setEmail("alice@example.com");
        user.setRoles(Set.of(userRole, adminRole));
        user.setEnabled(true);
        return user;
    }
}
