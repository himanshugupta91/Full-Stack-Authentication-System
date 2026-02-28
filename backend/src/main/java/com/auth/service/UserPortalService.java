package com.auth.service;

import com.auth.dto.UserDashboardDto;
import com.auth.dto.UserDto;

/**
 * Business logic contract for authenticated user portal views.
 */
public interface UserPortalService {

    /** Builds user dashboard payload for the provided authenticated email. */
    UserDashboardDto getDashboard(String email);

    /** Returns profile payload for the provided authenticated email. */
    UserDto getProfile(String email);
}
