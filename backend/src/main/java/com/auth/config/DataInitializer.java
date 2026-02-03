package com.auth.config;

import com.auth.entity.Role;
import com.auth.entity.User;
import com.auth.repository.RoleRepository;
import com.auth.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

/**
 * Data initializer to create default roles and admin user on startup.
 */
@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // Create roles if they don't exist
        Role userRole = roleRepository.findByName(Role.RoleName.ROLE_USER)
                .orElseGet(() -> {
                    Role role = new Role();
                    role.setName(Role.RoleName.ROLE_USER);
                    return roleRepository.save(role);
                });

        Role adminRole = roleRepository.findByName(Role.RoleName.ROLE_ADMIN)
                .orElseGet(() -> {
                    Role role = new Role();
                    role.setName(Role.RoleName.ROLE_ADMIN);
                    return roleRepository.save(role);
                });

        // Create default admin user if doesn't exist
        if (!userRepository.existsByEmail("admin@admin.com")) {
            User admin = new User();
            admin.setName("Admin");
            admin.setEmail("admin@admin.com");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setEnabled(true);

            Set<Role> adminRoles = new HashSet<>();
            adminRoles.add(adminRole);
            adminRoles.add(userRole);
            admin.setRoles(adminRoles);

            userRepository.save(admin);
            System.out.println("Default admin user created: admin@admin.com / admin123");
        }
    }
}
