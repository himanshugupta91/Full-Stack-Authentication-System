package com.auth.service.impl;

import com.auth.dto.response.UserDashboardDto;
import com.auth.dto.response.UserDto;
import com.auth.entity.User;
import com.auth.mapper.UserMapper;
import com.auth.service.UserPortalService;
import com.auth.service.UserService;
import com.auth.util.DateTimeUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * User-facing dashboard/profile composition logic.
 */
@Service
@RequiredArgsConstructor
public class UserPortalServiceImpl implements UserPortalService {

    private final UserService userService;
    private final UserMapper userMapper;

    @Override
    public UserDashboardDto getDashboard(String email) {
        User user = userService.getUserByEmail(email);
        UserDashboardDto response = userMapper.toUserDashboardDto(user);
        response.setMessage("Welcome to User Dashboard!");
        response.setTimestamp(DateTimeUtil.nowInIst12HourFormat());
        return response;
    }

    @Override
    public UserDto getProfile(String email) {
        User user = userService.getUserByEmail(email);
        UserDto profile = userMapper.toDto(user);
        return profile;
    }
}
