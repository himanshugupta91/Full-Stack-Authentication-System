package com.auth.dto;

import lombok.Data;
import java.util.Set;

@Data
public class UserDto {
    private Long id;
    private String name;
    private String email;
    private Set<String> roles;
    private boolean enabled;
    private java.time.LocalDateTime createdAt;
}
