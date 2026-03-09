package com.auth.service;

import com.auth.dto.response.UserDashboardDto;
import com.auth.dto.response.UserDto;

/**
 * Business logic contract for authenticated user portal views.
 */
public interface UserPortalService {

    /** Builds user dashboard payload for the provided authenticated email. */
    UserDashboardDto getDashboard(String email);

    /** Returns profile payload for the provided authenticated email. */
    UserDto getProfile(String email);
}
