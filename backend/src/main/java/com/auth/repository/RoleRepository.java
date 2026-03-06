package com.auth.repository;

import com.auth.entity.Role;
import com.auth.entity.RoleName;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository for Role entity operations.
 */
@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

    /** Finds a role by enum-backed role name. */
    Optional<Role> findByName(RoleName name);
}
