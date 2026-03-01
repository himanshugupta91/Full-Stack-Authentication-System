package com.auth.config;

import com.auth.entity.Role;
import com.auth.entity.User;
import com.auth.repository.RoleRepository;
import com.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

/**
 * Data initializer to create default roles and admin user on startup.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private static final String DEFAULT_ADMIN_NAME = "Admin";

    private final RoleRepository roleRepository;

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    @Value("${app.seed.admin.name:" + DEFAULT_ADMIN_NAME + "}")
    private String seedAdminName;

    @Value("${app.seed.admin.email:admin@admin.com}")
    private String seedAdminEmail;

    @Value("${app.seed.admin.password:admin123}")
    private String seedAdminPassword;

    /** Seeds default roles and a local admin account if they are missing at startup. */
    @Override
    public void run(String... args) {
        Role userRole = findOrCreateRole(Role.RoleName.ROLE_USER);
        Role adminRole = findOrCreateRole(Role.RoleName.ROLE_ADMIN);

        if (!userRepository.existsByEmail(seedAdminEmail)) {
            User admin = new User();
            admin.setName(seedAdminName);
            admin.setEmail(seedAdminEmail);
            admin.setPassword(passwordEncoder.encode(seedAdminPassword));
            admin.setEnabled(true);

            Set<Role> adminRoles = new HashSet<>();
            adminRoles.add(adminRole);
            adminRoles.add(userRole);
            admin.setRoles(adminRoles);

            userRepository.save(admin);
            log.info("Default admin user created: {}", seedAdminEmail);
        }
    }

    private Role findOrCreateRole(Role.RoleName roleName) {
        Optional<Role> roleOpt = roleRepository.findByName(roleName);
        if (roleOpt.isPresent()) {
            return roleOpt.get();
        }
        return createRole(roleName);
    }

    private Role createRole(Role.RoleName roleName) {
        Role role = new Role();
        role.setName(roleName);
        return roleRepository.save(role);
    }
}
