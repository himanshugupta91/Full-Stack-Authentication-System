package com.auth.service.impl;

import com.auth.entity.Role;
import com.auth.entity.RoleName;
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
    public Role findOrCreateRole(RoleName roleName) {
        Optional<Role> existingRoleOpt = roleRepository.findByName(roleName);
        Role role = existingRoleOpt.orElseGet(() -> createRole(roleName));
        return role;
    }

    /** Returns a role lookup result for the provided enum role name. */
    @Override
    public Optional<Role> findByName(RoleName roleName) {
        Optional<Role> roleOpt = roleRepository.findByName(roleName);
        return roleOpt;
    }

    private Role createRole(RoleName roleName) {
        Role role = new Role();
        role.setName(roleName);
        Role savedRole = roleRepository.save(role);
        return savedRole;
    }
}
