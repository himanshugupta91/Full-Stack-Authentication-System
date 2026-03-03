package com.auth.dto.request;

import lombok.Data;

/**
 * Optional request body for refresh token endpoint.
 * Cookie is preferred; this exists for non-browser clients.
 */
@Data
public class TokenRefreshRequest {
    private String refreshToken;
}
