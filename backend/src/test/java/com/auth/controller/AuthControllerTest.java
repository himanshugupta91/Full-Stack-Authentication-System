package com.auth.controller;

import com.auth.dto.response.ApiResponse;
import com.auth.dto.response.AuthResponse;
import com.auth.dto.response.AuthTokens;
import com.auth.dto.response.MessageResponse;
import com.auth.dto.request.TokenRefreshRequest;
import com.auth.exception.TokenValidationException;
import com.auth.security.RefreshTokenCookieService;
import com.auth.service.AuthService;
import com.auth.service.auth.AuthTokenService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuthController")
class AuthControllerTest {

    @Mock
    private AuthService authService;

    @Mock
    private AuthTokenService authTokenService;

    @Mock
    private RefreshTokenCookieService refreshTokenCookieService;

    @Mock
    private HttpServletRequest httpRequest;

    @Mock
    private HttpServletResponse httpResponse;

    @InjectMocks
    private AuthController authController;

    @Test
    @DisplayName("refreshToken: body token provided → prefers body over cookie")
    void givenRefreshTokenInBody_whenRefreshing_thenBodyTokenTakesPrecedenceOverCookie() {
        // Arrange
        TokenRefreshRequest requestBody = new TokenRefreshRequest();
        requestBody.setRefreshToken("body-refresh-token");

        AuthResponse authResponse = new AuthResponse(
                200,
                "access-token",
                "Bearer",
                900_000L,
                3_600_000L,
                1L,
                "Alice",
                "alice@example.com",
                true,
                List.of("ROLE_USER"));
        AuthTokens tokens = new AuthTokens(authResponse, "new-refresh-token");

        when(authTokenService.refreshTokens("body-refresh-token")).thenReturn(tokens);
        when(refreshTokenCookieService.buildRefreshTokenCookie("new-refresh-token", httpRequest))
                .thenReturn("set-cookie-value");

        // Act
        ResponseEntity<ApiResponse<AuthResponse>> response = authController.refreshToken(httpRequest,
                httpResponse, requestBody);

        // Assert
        verify(authTokenService).refreshTokens("body-refresh-token");
        verify(httpResponse).addHeader(HttpHeaders.SET_COOKIE, "set-cookie-value");
        assertEquals(authResponse, response.getBody().getData());
    }

    @Test
    @DisplayName("logout: body missing → uses cookie token and clears cookie")
    void givenNoRefreshTokenInBody_whenLoggingOut_thenCookieTokenIsRevokedAndCookieCleared() {
        // Arrange
        when(refreshTokenCookieService.getCookieName()).thenReturn("refreshToken");
        when(httpRequest.getCookies()).thenReturn(new Cookie[] {
                new Cookie("other", "ignored"),
                new Cookie("refreshToken", "cookie-refresh-token")
        });
        when(refreshTokenCookieService.clearRefreshTokenCookie(httpRequest)).thenReturn("expired-cookie");

        // Act
        ResponseEntity<ApiResponse<MessageResponse>> response = authController.logout(httpRequest, httpResponse,
                null);

        // Assert
        verify(authTokenService).revokeRefreshToken("cookie-refresh-token");
        verify(httpResponse).addHeader(HttpHeaders.SET_COOKIE, "expired-cookie");

        assertEquals("Logged out successfully.", response.getBody().getData().getMessage());
        assertTrue(response.getBody().getData().isSuccess());
    }

    @Test
    @DisplayName("refreshToken: multiple refresh cookies → uses first valid candidate")
    void givenMultipleRefreshTokenCookies_whenRefreshing_thenFirstValidCookieTokenIsUsed() {
        // Arrange
        when(refreshTokenCookieService.getCookieName()).thenReturn("refreshToken");
        when(httpRequest.getCookies()).thenReturn(new Cookie[] {
                new Cookie("refreshToken", "stale-refresh-token"),
                new Cookie("refreshToken", "valid-refresh-token")
        });

        AuthResponse authResponse = new AuthResponse(
                200,
                "access-token",
                "Bearer",
                900_000L,
                3_600_000L,
                2L,
                "Bob",
                "bob@example.com",
                true,
                List.of("ROLE_USER"));
        AuthTokens tokens = new AuthTokens(authResponse, "rotated-refresh-token");

        when(authTokenService.refreshTokens("stale-refresh-token"))
                .thenThrow(new TokenValidationException("Invalid refresh token."));
        when(authTokenService.refreshTokens("valid-refresh-token"))
                .thenReturn(tokens);
        when(refreshTokenCookieService.buildRefreshTokenCookie("rotated-refresh-token", httpRequest))
                .thenReturn("set-cookie-value");

        // Act
        ResponseEntity<ApiResponse<AuthResponse>> response = authController.refreshToken(httpRequest,
                httpResponse, null);

        // Assert
        verify(authTokenService).refreshTokens("stale-refresh-token");
        verify(authTokenService).refreshTokens("valid-refresh-token");
        verify(httpResponse).addHeader(HttpHeaders.SET_COOKIE, "set-cookie-value");
        assertEquals(authResponse, response.getBody().getData());
    }
}
