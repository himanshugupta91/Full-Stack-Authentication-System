package com.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO for authentication response containing access token metadata and user details.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {

    private String accessToken;
    private String tokenType;
    private long accessTokenExpiresInMs;
    private long refreshTokenExpiresInMs;
    private Long id;
    private String name;
    private String email;
    private List<String> roles;
}
