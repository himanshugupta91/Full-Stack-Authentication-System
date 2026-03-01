package com.auth.security;

import com.auth.entity.User;
import com.auth.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Custom UserDetailsService implementation for Spring Security.
 * Loads user details from database for authentication.
 */
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserService userService;

    /**
     * Loads a user by email and returns Spring Security-compatible user details.
     */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        String normalizedEmail = normalizeEmail(email);
        User user = findUserOrThrow(normalizedEmail);
        return buildSecurityUser(user);
    }

    /** Normalizes email input used for user lookup. */
    protected String normalizeEmail(String email) {
        if (email == null) {
            throw new UsernameNotFoundException("Email must not be null.");
        }
        return email.trim().toLowerCase(Locale.ROOT);
    }

    /** Finds an application user by email or throws UsernameNotFoundException. */
    protected User findUserOrThrow(String email) {
        Optional<User> userOpt = userService.findByEmail(email);
        if (userOpt.isEmpty()) {
            throw new UsernameNotFoundException("User not found with email: " + email);
        }
        return userOpt.get();
    }

    /** Converts application roles into Spring Security granted authorities. */
    protected List<SimpleGrantedAuthority> buildAuthorities(User user) {
        return user.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority(role.getName().name()))
                .collect(Collectors.toList());
    }

    /** Converts the application user model into Spring Security UserDetails. */
    protected UserDetails buildSecurityUser(User user) {
        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                user.isEnabled(),
                true,
                true,
                true,
                buildAuthorities(user));
    }
}
