package com.auth.mapper;

import com.auth.dto.AuthResponse;
import com.auth.dto.RegisterRequest;
import com.auth.dto.UserDashboardDto;
import com.auth.dto.UserDto;
import com.auth.entity.Role;
import com.auth.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "password", ignore = true) // Encoded manually
    @Mapping(target = "roles", ignore = true) // Set manually
    @Mapping(target = "verificationOtp", ignore = true)
    @Mapping(target = "otpExpiry", ignore = true)
    @Mapping(target = "resetToken", ignore = true)
    @Mapping(target = "resetTokenExpiry", ignore = true)
    @Mapping(target = "enabled", constant = "false")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    User toEntity(RegisterRequest request);

    UserDto toDto(User user);

    List<UserDto> toDtoList(List<User> users);

    @Mapping(source = "token", target = "token")
    @Mapping(target = "type", constant = "Bearer")
    AuthResponse toAuthResponse(User user, String token);

    @Mapping(source = "name", target = "user")
    @Mapping(target = "message", ignore = true)
    @Mapping(target = "timestamp", ignore = true)
    UserDashboardDto toUserDashboardDto(User user);

    default String map(Role role) {
        return role.getName().name();
    }
}
