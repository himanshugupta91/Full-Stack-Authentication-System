package com.auth.dto.response;

/**
 * Internal login/refresh result containing API response payload and raw refresh
 * token.
 */
public record AuthTokens(AuthResponse response, String refreshToken) {
}
