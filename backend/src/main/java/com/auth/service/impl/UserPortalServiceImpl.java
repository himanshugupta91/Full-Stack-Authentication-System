package com.auth.service.impl;

import com.auth.config.CacheNames;
import com.auth.dto.response.UserDashboardDto;
import com.auth.dto.response.UserDto;
import com.auth.entity.User;
import com.auth.mapper.UserMapper;
import com.auth.service.UserPortalService;
import com.auth.service.UserService;
import com.auth.util.DateTimeUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Composes user-facing dashboard and profile payloads.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserPortalServiceImpl implements UserPortalService {

    private final UserService userService;
    private final UserMapper userMapper;
    /**
     * Returns dashboard.
     */

    @Override
    public UserDashboardDto getDashboard(String email) {
        User user = userService.getUserByEmail(email);
        UserDashboardDto dashboard = userMapper.toUserDashboardDto(user);
        String displayName = user.getName() != null && !user.getName().isBlank() ? user.getName() : "User";
        dashboard.setMessage("Welcome back, " + displayName + "!");
        dashboard.setTimestamp(DateTimeUtil.nowInIst12HourFormat());
        return dashboard;
    }

    @Override
    @Cacheable(cacheNames = CacheNames.USER_PROFILE,
            key = "#email == null ? 'unknown' : #email.toLowerCase()")
    /**
     * Returns profile.
     */
    public UserDto getProfile(String email) {
        User user = userService.getUserByEmail(email);
        return userMapper.toDto(user);
    }
}
