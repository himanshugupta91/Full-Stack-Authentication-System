package com.auth.service.impl;

import com.auth.dto.AdminDashboardDto;
import com.auth.dto.UserDto;
import com.auth.entity.Role;
import com.auth.entity.User;
import com.auth.mapper.UserMapper;
import com.auth.repository.UserRepository;
import com.auth.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Locale;

/**
 * Admin-specific business logic for dashboard metrics and user listing.
 */
@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    private static final int MIN_PAGE = 0;
    private static final int MIN_PAGE_SIZE = 1;
    private static final int MAX_PAGE_SIZE = 100;

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public AdminDashboardDto getDashboard(String adminEmail) {
        long totalUsers = userRepository.count();
        long activeUsers = userRepository.countByEnabledTrue();

        return new AdminDashboardDto(
                "Welcome to Admin Dashboard!",
                adminEmail,
                totalUsers,
                activeUsers,
                LocalDateTime.now().toString());
    }

    @Override
    public Page<UserDto> getUsers(
            int page,
            int size,
            String search,
            Boolean enabled,
            String role,
            String sortBy,
            String sortDir) {
        int normalizedPage = Math.max(MIN_PAGE, page);
        int normalizedSize = Math.max(MIN_PAGE_SIZE, Math.min(size, MAX_PAGE_SIZE));

        Sort.Direction direction = "asc".equalsIgnoreCase(sortDir) ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(normalizedPage, normalizedSize, Sort.by(direction, resolveSortField(sortBy)));

        Specification<User> specification = buildUserSpecification(search, enabled, role);
        return userRepository.findAll(specification, pageable).map(userMapper::toDto);
    }

    /** Builds optional user filters for search/status/role queries. */
    private Specification<User> buildUserSpecification(String search, Boolean enabled, String role) {
        Specification<User> specification = Specification.where(null);

        if (search != null && !search.isBlank()) {
            String loweredSearch = "%" + search.trim().toLowerCase(Locale.ROOT) + "%";
            specification = specification.and((root, query, cb) -> cb.or(
                    cb.like(cb.lower(root.get("name")), loweredSearch),
                    cb.like(cb.lower(root.get("email")), loweredSearch)));
        }

        if (enabled != null) {
            specification = specification.and((root, query, cb) -> cb.equal(root.get("enabled"), enabled));
        }

        if (role != null && !role.isBlank()) {
            Role.RoleName roleName = normalizeRoleName(role);
            specification = specification.and((root, query, cb) -> {
                query.distinct(true);
                return cb.equal(root.join("roles").get("name"), roleName);
            });
        }

        return specification;
    }

    /** Accepts USER/ADMIN or ROLE_USER/ROLE_ADMIN query values. */
    private Role.RoleName normalizeRoleName(String rawRole) {
        String trimmedRole = rawRole.trim().toUpperCase(Locale.ROOT);
        if (!trimmedRole.startsWith("ROLE_")) {
            trimmedRole = "ROLE_" + trimmedRole;
        }

        try {
            return Role.RoleName.valueOf(trimmedRole);
        } catch (IllegalArgumentException exception) {
            throw new IllegalArgumentException("Invalid role filter. Use USER, ADMIN, ROLE_USER, or ROLE_ADMIN.");
        }
    }

    /** Restricts client sort field to safe, indexed-ish columns. */
    private String resolveSortField(String rawSortBy) {
        if (rawSortBy == null || rawSortBy.isBlank()) {
            return "createdAt";
        }

        return switch (rawSortBy) {
            case "id", "name", "email", "enabled", "createdAt" -> rawSortBy;
            default -> "createdAt";
        };
    }
}
