package com.auth.service;

import com.auth.dto.response.AdminDashboardDto;
import com.auth.dto.response.UserDto;
import org.springframework.data.domain.Page;

/**
 * Business logic contract for admin dashboard and user management views.
 */
public interface AdminService {

    /** Builds admin dashboard metrics payload for the authenticated admin email. */
    AdminDashboardDto getDashboard(String adminEmail);

    /** Returns paginated/filterable/sortable users for admin listing screens. */
    Page<UserDto> getUsers(
            int page,
            int size,
            String search,
            Boolean enabled,
            String role,
            String sortBy,
            String sortDir);
}
