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
import java.util.Set;
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
    private static final String DEFAULT_SORT_FIELD = "createdAt";
    private static final String ROLE_PREFIX = "ROLE_";
    private static final String SORT_DIRECTION_ASC = "asc";
    private static final String ERROR_INVALID_ROLE_FILTER =
            "Invalid role filter. Use USER, ADMIN, ROLE_USER, or ROLE_ADMIN.";
    private static final String FIELD_ENABLED = "enabled";
    private static final String FIELD_ROLES = "roles";
    private static final String FIELD_ROLE_NAME = "name";
    private static final String FIELD_USER_NAME = "name";
    private static final String FIELD_EMAIL = "email";
    private static final Set<String> ALLOWED_SORT_FIELDS = Set.of(
            "id",
            FIELD_USER_NAME,
            FIELD_EMAIL,
            FIELD_ENABLED,
            DEFAULT_SORT_FIELD);

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
        int normalizedPage = normalizePage(page);
        int normalizedSize = normalizePageSize(size);

        Sort.Direction direction = resolveSortDirection(sortDir);
        String safeSortField = resolveSortField(sortBy);
        Sort sort = Sort.by(direction, safeSortField);
        Pageable pageable = PageRequest.of(normalizedPage, normalizedSize, sort);

        Specification<User> specification = buildUserSpecification(search, enabled, role);
        Page<User> usersPage = userRepository.findAll(specification, pageable);
        return usersPage.map(userMapper::toDto);
    }

    /** Builds optional user filters for search/status/role queries. */
    private Specification<User> buildUserSpecification(String search, Boolean enabled, String role) {
        Specification<User> specification = null;

        if (hasText(search)) {
            specification = andSpecification(specification, buildSearchSpecification(search));
        }

        if (enabled != null) {
            specification = andSpecification(
                    specification,
                    (root, query, cb) -> cb.equal(root.get(FIELD_ENABLED), enabled));
        }

        if (hasText(role)) {
            Role.RoleName roleName = normalizeRoleName(role);
            specification = andSpecification(specification, (root, query, cb) -> {
                query.distinct(true);
                return cb.equal(root.join(FIELD_ROLES).get(FIELD_ROLE_NAME), roleName);
            });
        }

        return specification;
    }

    /** Accepts USER/ADMIN or ROLE_USER/ROLE_ADMIN query values. */
    private Role.RoleName normalizeRoleName(String rawRole) {
        String trimmedRole = rawRole.trim().toUpperCase(Locale.ROOT);
        if (!trimmedRole.startsWith(ROLE_PREFIX)) {
            trimmedRole = ROLE_PREFIX + trimmedRole;
        }

        try {
            return Role.RoleName.valueOf(trimmedRole);
        } catch (IllegalArgumentException exception) {
            throw new IllegalArgumentException(ERROR_INVALID_ROLE_FILTER);
        }
    }

    /** Restricts client sort field to safe, indexed-ish columns. */
    private String resolveSortField(String rawSortBy) {
        if (rawSortBy == null || rawSortBy.isBlank()) {
            return DEFAULT_SORT_FIELD;
        }

        if (ALLOWED_SORT_FIELDS.contains(rawSortBy)) {
            return rawSortBy;
        }
        return DEFAULT_SORT_FIELD;
    }

    private int normalizePage(int page) {
        if (page < MIN_PAGE) {
            return MIN_PAGE;
        }
        return page;
    }

    private int normalizePageSize(int size) {
        int boundedSize = size;
        if (boundedSize < MIN_PAGE_SIZE) {
            boundedSize = MIN_PAGE_SIZE;
        }
        if (boundedSize > MAX_PAGE_SIZE) {
            boundedSize = MAX_PAGE_SIZE;
        }
        return boundedSize;
    }

    private Sort.Direction resolveSortDirection(String sortDir) {
        if (sortDir != null && sortDir.equalsIgnoreCase(SORT_DIRECTION_ASC)) {
            return Sort.Direction.ASC;
        }
        return Sort.Direction.DESC;
    }

    private Specification<User> buildSearchSpecification(String search) {
        String loweredSearch = "%" + search.trim().toLowerCase(Locale.ROOT) + "%";
        return (root, query, cb) -> cb.or(
                cb.like(cb.lower(root.get(FIELD_USER_NAME)), loweredSearch),
                cb.like(cb.lower(root.get(FIELD_EMAIL)), loweredSearch));
    }

    private boolean hasText(String value) {
        if (value == null) {
            return false;
        }
        return !value.isBlank();
    }

    private Specification<User> andSpecification(
            Specification<User> baseSpecification,
            Specification<User> clause) {
        if (baseSpecification == null) {
            return clause;
        }
        return baseSpecification.and(clause);
    }
}
