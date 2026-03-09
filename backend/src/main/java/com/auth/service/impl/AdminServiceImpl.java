package com.auth.service.impl;

import com.auth.config.CacheNames;
import com.auth.dto.response.AdminDashboardDto;
import com.auth.dto.response.UserDto;
import com.auth.entity.RoleName;
import com.auth.entity.User;
import com.auth.mapper.UserMapper;
import com.auth.repository.UserRepository;
import com.auth.service.AdminService;
import com.auth.util.DateTimeUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Set;
import java.util.Locale;

/**
 * Admin-specific business logic for dashboard metrics and user listing.
 */
@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    @Cacheable(cacheNames = CacheNames.ADMIN_DASHBOARD, key = "#adminEmail == null ? 'unknown' : #adminEmail.toLowerCase()")
    public AdminDashboardDto getDashboard(String adminEmail) {
        long totalUsers = userRepository.count();
        long activeUsers = userRepository.countByEnabledTrue();

        AdminDashboardDto dashboard = new AdminDashboardDto(
                "Welcome to Admin Dashboard!",
                adminEmail,
                totalUsers,
                activeUsers,
                DateTimeUtil.nowInIst12HourFormat());
        return dashboard;
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
        Page<UserDto> userDtoPage = usersPage.map(userMapper::toDto);
        return userDtoPage;
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
                    (root, query, cb) -> cb.equal(root.get("enabled"), enabled));
        }

        if (hasText(role)) {
            RoleName roleName = normalizeRoleName(role);
            specification = andSpecification(specification, (root, query, cb) -> {
                query.distinct(true);
                return cb.equal(root.join("roles").get("name"), roleName);
            });
        }

        return specification;
    }

    /** Accepts USER/ADMIN or ROLE_USER/ROLE_ADMIN query values. */
    private RoleName normalizeRoleName(String rawRole) {
        String trimmedRole = rawRole.trim().toUpperCase(Locale.ROOT);
        if (!trimmedRole.startsWith("ROLE_")) {
            trimmedRole = "ROLE_" + trimmedRole;
        }

        try {
            RoleName roleName = RoleName.valueOf(trimmedRole);
            return roleName;
        } catch (IllegalArgumentException exception) {
            throw new IllegalArgumentException("Invalid role filter. Use USER, ADMIN, ROLE_USER, or ROLE_ADMIN.");
        }
    }

    /** Restricts client sort field to safe, indexed-ish columns. */
    private String resolveSortField(String rawSortBy) {
        if (!StringUtils.hasText(rawSortBy)) {
            return "createdAt";
        }
        String safeSortField = isAllowedSortField(rawSortBy) ? rawSortBy : "createdAt";
        return safeSortField;
    }

    private int normalizePage(int page) {
        if (page < 0) {
            return 0;
        }
        return page;
    }

    private int normalizePageSize(int size) {
        int boundedSize = size;
        if (boundedSize < 1) {
            boundedSize = 1;
        }
        if (boundedSize > 100) {
            boundedSize = 100;
        }
        return boundedSize;
    }

    private Sort.Direction resolveSortDirection(String sortDir) {
        Sort.Direction direction = "asc".equalsIgnoreCase(sortDir) ? Sort.Direction.ASC : Sort.Direction.DESC;
        return direction;
    }

    private Specification<User> buildSearchSpecification(String search) {
        String loweredSearch = "%" + search.trim().toLowerCase(Locale.ROOT) + "%";
        Specification<User> searchSpecification = (root, query, cb) -> cb.or(
                cb.like(cb.lower(root.get("name")), loweredSearch),
                cb.like(cb.lower(root.get("email")), loweredSearch));
        return searchSpecification;
    }

    private boolean hasText(String value) {
        boolean hasText = StringUtils.hasText(value);
        return hasText;
    }

    private Specification<User> andSpecification(
            Specification<User> baseSpecification,
            Specification<User> clause) {
        if (baseSpecification == null) {
            return clause;
        }
        Specification<User> combinedSpecification = baseSpecification.and(clause);
        return combinedSpecification;
    }

    private boolean isAllowedSortField(String sortField) {
        Set<String> allowedSortFields = Set.of("id", "name", "email", "enabled", "createdAt");
        boolean isAllowed = allowedSortFields.contains(sortField);
        return isAllowed;
    }
}
