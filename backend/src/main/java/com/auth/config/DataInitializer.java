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
import org.springframework.util.StringUtils;

import java.util.Set;

/**
 * Seeds default roles and an initial admin account at application startup.
 * Admin seeding is opt-in via {@code app.seed.admin.enabled=true}.
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

    /** Seeds default roles and, if enabled, an initial admin user at startup. */
    @Override
    public void run(String... args) {
        Role userRole = findOrCreateRole(RoleName.ROLE_USER);
        Role adminRole = findOrCreateRole(RoleName.ROLE_ADMIN);

        if (!seedAdminEnabled) {
            return;
        }

        validateSeedAdminConfig();

        if (!userRepository.existsByEmailIgnoreCase(seedAdminEmail)) {
            User admin = new User();
            admin.setName(seedAdminName);
            admin.setEmail(seedAdminEmail);
            admin.setPassword(passwordEncoder.encode(seedAdminPassword));
            admin.setEnabled(true);
            admin.setRoles(Set.of(adminRole, userRole));

            userRepository.save(admin);
            log.info("Default admin user created: {}", seedAdminEmail);
        }
    }
    /**
     * Validates seed admin config.
     */

    private void validateSeedAdminConfig() {
        if (!StringUtils.hasText(seedAdminName)
                || !StringUtils.hasText(seedAdminEmail)
                || !StringUtils.hasText(seedAdminPassword)) {
            throw new IllegalStateException(
                    "app.seed.admin.enabled=true requires name, email, and password.");
        }
    }
    /**
     * Finds or create role.
     */

    private Role findOrCreateRole(RoleName roleName) {
        return roleRepository.findByName(roleName)
                .orElseGet(() -> createRole(roleName));
    }
    /**
     * Creates role.
     */

    private Role createRole(RoleName roleName) {
        Role role = new Role();
        role.setName(roleName);
        return roleRepository.save(role);
    }
}
