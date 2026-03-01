package com.auth.service.impl;

import com.auth.entity.Role;
import com.auth.repository.RoleRepository;
import com.auth.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Implementation of RoleService.
 */
@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;

    /** Returns an existing role or persists a new one when missing. */
    @Override
    @Transactional
    public Role findOrCreateRole(Role.RoleName roleName) {
        return roleRepository.findByName(roleName)
                .orElseGet(() -> createRole(roleName));
    }

    /** Returns a role lookup result for the provided enum role name. */
    @Override
    public Optional<Role> findByName(Role.RoleName roleName) {
        return roleRepository.findByName(roleName);
    }

    private Role createRole(Role.RoleName roleName) {
        Role role = new Role();
        role.setName(roleName);
        return roleRepository.save(role);
    }
}
