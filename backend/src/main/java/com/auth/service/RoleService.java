package com.auth.service;

import com.auth.entity.Role;
import com.auth.entity.RoleName;

import java.util.Optional;

/**
 * Service interface for Role operations.
 */
public interface RoleService {
    /**
     * Find a role by name, or create it if it doesn't exist.
     *
     * @param roleName Role name enum
     * @return Found or created Role
     */
    Role findOrCreateRole(RoleName roleName);

    /**
     * Find a role by name.
     *
     * @param roleName Role name enum
     * @return Optional Role
     */
    Optional<Role> findByName(RoleName roleName);
}
