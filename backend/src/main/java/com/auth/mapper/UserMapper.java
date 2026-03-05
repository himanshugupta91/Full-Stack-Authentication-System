package com.auth.mapper;

import com.auth.dto.request.RegisterRequest;
import com.auth.dto.UserDashboardDto;
import com.auth.dto.UserDto;
import com.auth.entity.Role;
import com.auth.entity.User;
import com.auth.util.DateTimeUtil;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;

@Mapper(componentModel = "spring")
public interface UserMapper {

    /**
     * Maps registration payload to a new User entity while ignoring managed fields.
     */
    @Mapping(target = "password", ignore = true) // Encoded manually
    @Mapping(target = "roles", ignore = true) // Set manually
    @Mapping(target = "verificationOtp", ignore = true)
    @Mapping(target = "otpExpiry", ignore = true)
    @Mapping(target = "resetToken", ignore = true)
    @Mapping(target = "resetTokenExpiry", ignore = true)
    @Mapping(target = "refreshToken", ignore = true)
    @Mapping(target = "refreshTokenExpiry", ignore = true)
    @Mapping(target = "failedLoginAttempts", ignore = true)
    @Mapping(target = "accountLockedUntil", ignore = true)
    @Mapping(target = "failedOtpAttempts", ignore = true)
    @Mapping(target = "otpLockedUntil", ignore = true)
    @Mapping(target = "authProvider", ignore = true)
    @Mapping(target = "authProviderUserId", ignore = true)
    @Mapping(target = "enabled", constant = "false")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    User toEntity(RegisterRequest request);

    /** Maps User entity to API-safe user DTO. */
    @Mapping(target = "loginSource", expression = "java(resolveLoginSource(user))")
    UserDto toDto(User user);

    /** Maps a list of users to their DTO representation. */
    List<UserDto> toDtoList(List<User> users);

    /** Maps User entity to dashboard payload with custom user field mapping. */
    @Mapping(source = "name", target = "user")
    @Mapping(target = "message", ignore = true)
    @Mapping(target = "timestamp", ignore = true)
    UserDashboardDto toUserDashboardDto(User user);

    /** Converts Role entity to its role-name string for DTO serialization. */
    default String map(Role role) {
        return role.getName().name();
    }

    /** Formats LocalDateTime as IST in 12-hour form for API responses. */
    default String map(LocalDateTime value) {
        return DateTimeUtil.formatIst12Hour(value);
    }

    /** Resolves the source used by the account for login display in admin views. */
    default String resolveLoginSource(User user) {
        if (user == null) {
            return "UNKNOWN";
        }

        String provider = user.getAuthProvider();
        if (provider == null || provider.trim().isEmpty()) {
            return "EMAIL_PASSWORD";
        }

        return provider.trim().toUpperCase(Locale.ROOT);
    }
}
