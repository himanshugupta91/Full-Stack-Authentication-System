package com.auth.security;

import com.auth.entity.User;
import com.auth.service.UserService;
import com.auth.util.EmailNormalizer;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

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
        User user = getRequiredUser(normalizedEmail);
        UserDetails userDetails = buildSecurityUser(user);
        return userDetails;
    }

    /** Normalizes email input used for user lookup. */
    protected String normalizeEmail(String email) {
        String normalizedEmail = EmailNormalizer.normalizeOrNull(email);
        if (normalizedEmail == null) {
            throw new UsernameNotFoundException("Email must not be blank.");
        }
        return normalizedEmail;
    }

    /** Finds an application user by email or throws UsernameNotFoundException. */
    protected User getRequiredUser(String email) {
        Optional<User> userOpt = userService.findByEmail(email);
        User user = userOpt
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));
        return user;
    }

    /** Converts application roles into Spring Security granted authorities. */
    protected List<SimpleGrantedAuthority> buildAuthorities(User user) {
        List<SimpleGrantedAuthority> authorities = user.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority(role.getName().name()))
                .toList();
        return authorities;
    }

    /** Converts the application user model into Spring Security UserDetails. */
    protected UserDetails buildSecurityUser(User user) {
        UserDetails userDetails = new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                user.isEnabled(),
                true,
                true,
                true,
                buildAuthorities(user));
        return userDetails;
    }
}
