package com.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO for authentication response containing JWT token and user details.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {

    private String token;
    private String type = "Bearer";
    private Long id;
    private String name;
    private String email;
    private List<String> roles;

    public AuthResponse(String token, Long id, String name, String email, List<String> roles) {
        this.token = token;
        this.id = id;
        this.name = name;
        this.email = email;
        this.roles = roles;
    }
}
