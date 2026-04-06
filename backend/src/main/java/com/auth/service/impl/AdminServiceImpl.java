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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Locale;
import java.util.Set;

/**
 * Admin-specific business logic for dashboard metrics and filtered user listings.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminServiceImpl implements AdminService {

    /** Columns that callers are permitted to sort by. */
    private static final Set<String> ALLOWED_SORT_FIELDS = Set.of("id", "name", "email", "enabled", "createdAt");

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    @Cacheable(cacheNames = CacheNames.ADMIN_DASHBOARD,
            key = "#adminEmail == null ? 'unknown' : #adminEmail.toLowerCase()")
    /**
     * Returns dashboard.
     */
    public AdminDashboardDto getDashboard(String adminEmail) {
        return new AdminDashboardDto(
                "Welcome to Admin Dashboard!",
                adminEmail,
                userRepository.count(),
                userRepository.countByEnabledTrue(),
                DateTimeUtil.nowInIst12HourFormat());
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

        int normalizedPage = Math.max(page, 0);
        int normalizedSize = Math.min(Math.max(size, 1), 100);

        Sort.Direction direction = "asc".equalsIgnoreCase(sortDir) ? Sort.Direction.ASC : Sort.Direction.DESC;
        String safeField = ALLOWED_SORT_FIELDS.contains(sortBy) ? sortBy : "createdAt";
        Pageable pageable = PageRequest.of(normalizedPage, normalizedSize, Sort.by(direction, safeField));

        return userRepository.findAll(buildSpecification(search, enabled, role), pageable)
                .map(userMapper::toDto);
    }

    // ── Specification builders ────────────────────────────────────────────────

    /** Composes optional filter predicates for search text, account status, and role. */
    private Specification<User> buildSpecification(String search, Boolean enabled, String role) {
        Specification<User> spec = null;

        if (StringUtils.hasText(search)) {
            spec = and(spec, searchSpecification(search));
        }
        if (enabled != null) {
            spec = and(spec, (root, query, cb) -> cb.equal(root.get("enabled"), enabled));
        }
        if (StringUtils.hasText(role)) {
            RoleName roleName = parseRoleName(role);
            spec = and(spec, (root, query, cb) -> {
                query.distinct(true);
                return cb.equal(root.join("roles").get("name"), roleName);
            });
        }

        return spec;
    }
    /**
     * Executes search specification logic.
     */

    private Specification<User> searchSpecification(String search) {
        String pattern = "%" + search.trim().toLowerCase(Locale.ROOT) + "%";
        return (root, query, cb) -> cb.or(
                cb.like(cb.lower(root.get("name")), pattern),
                cb.like(cb.lower(root.get("email")), pattern));
    }
    /**
     * Executes and logic.
     */

    private Specification<User> and(Specification<User> base, Specification<User> clause) {
        return base == null ? clause : base.and(clause);
    }

    /**
     * Normalises a caller-supplied role string. Accepts both {@code USER} and
     * {@code ROLE_USER} forms.
     */
    private RoleName parseRoleName(String raw) {
        String normalized = raw.trim().toUpperCase(Locale.ROOT);
        if (!normalized.startsWith("ROLE_")) {
            normalized = "ROLE_" + normalized;
        }
        try {
            return RoleName.valueOf(normalized);
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException("Invalid role filter. Use USER, ADMIN, ROLE_USER, or ROLE_ADMIN.");
        }
    }
}
