package com.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminDashboardDto {
    private String message;
    private String admin;
    private long totalUsers;
    private long activeUsers;
    private String timestamp;
}
