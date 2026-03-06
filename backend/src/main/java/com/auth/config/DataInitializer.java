package com.auth.config;

import com.auth.entity.Role;
import com.auth.entity.RoleName;
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
import java.util.Set;

/**
 * Data initializer to create default roles and admin user on startup.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    @Value("${app.seed.admin.name:}")
    private String seedAdminName;

    @Value("${app.seed.admin.email:}")
    private String seedAdminEmail;

    @Value("${app.seed.admin.password:}")
    private String seedAdminPassword;

    @Value("${app.seed.admin.enabled:false}")
    private boolean seedAdminEnabled;

    /** Seeds default roles and a local admin account if they are missing at startup. */
    @Override
    public void run(String... args) {
        Role userRole = findOrCreateRole(RoleName.ROLE_USER);
        Role adminRole = findOrCreateRole(RoleName.ROLE_ADMIN);

        if (!seedAdminEnabled) {
            return;
        }

        validateSeedAdminConfiguration();

        if (!userRepository.existsByEmailIgnoreCase(seedAdminEmail)) {
            User admin = new User();
            admin.setName(seedAdminName);
            admin.setEmail(seedAdminEmail);
            admin.setPassword(passwordEncoder.encode(seedAdminPassword));
            admin.setEnabled(true);
            admin.setRoles(new HashSet<>(Set.of(adminRole, userRole)));

            userRepository.save(admin);
            log.info("Default admin user created: {}", seedAdminEmail);
        }
    }

    private void validateSeedAdminConfiguration() {
        if (isBlank(seedAdminName) || isBlank(seedAdminEmail) || isBlank(seedAdminPassword)) {
            throw new IllegalStateException("app.seed.admin.enabled=true requires name, email, and password.");
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    private Role findOrCreateRole(RoleName roleName) {
        return roleRepository.findByName(roleName)
                .orElseGet(() -> createRole(roleName));
    }

    private Role createRole(RoleName roleName) {
        Role role = new Role();
        role.setName(roleName);
        return roleRepository.save(role);
    }
}
