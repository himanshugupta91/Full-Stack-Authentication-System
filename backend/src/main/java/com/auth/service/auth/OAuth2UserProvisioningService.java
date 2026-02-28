package com.auth.service.auth;

import com.auth.entity.Role;
import com.auth.entity.User;
import com.auth.service.RoleService;
import com.auth.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

/**
 * Resolves OAuth2 user data and creates/updates local users.
 */
@Service
@RequiredArgsConstructor
public class OAuth2UserProvisioningService {

    private final UserService userService;

    private final RoleService roleService;

    private final PasswordEncoder passwordEncoder;

    /** Loads an existing OAuth user or creates a local enabled user profile when first seen. */
    @Transactional
    public User loadOrCreateUser(OAuth2AuthenticationToken authenticationToken, OAuth2User oauth2User) {
        String provider = authenticationToken.getAuthorizedClientRegistrationId();
        Map<String, Object> attributes = oauth2User.getAttributes();

        String email = extractEmail(provider, attributes);
        String name = extractDisplayName(attributes, email);

        Optional<User> existingUser = userService.findByEmail(email);
        if (existingUser.isPresent()) {
            User user = existingUser.get();
            boolean changed = false;

            if (!user.isEnabled()) {
                user.setEnabled(true);
                changed = true;
            }

            if (user.getAuthProvider() == null || user.getAuthProvider().isBlank()) {
                user.setAuthProvider(provider);
                changed = true;
            }

            if (changed) {
                user = userService.save(user);
            }
            return user;
        }

        User newUser = new User();
        newUser.setName(name);
        newUser.setEmail(email);
        newUser.setPassword(passwordEncoder.encode(UUID.randomUUID().toString()));
        newUser.setEnabled(true);
        newUser.setAuthProvider(provider);

        Set<Role> roles = new HashSet<>();
        roles.add(roleService.findOrCreateRole(Role.RoleName.ROLE_USER));
        newUser.setRoles(roles);

        return userService.save(newUser);
    }

    /** Extracts a reliable email from provider attributes with provider-specific fallback logic. */
    private String extractEmail(String provider, Map<String, Object> attributes) {
        String email = toString(attributes.get("email"));
        if (email != null && !email.isBlank()) {
            return email;
        }

        if ("github".equals(provider)) {
            String login = toString(attributes.get("login"));
            if (login != null && !login.isBlank()) {
                return login + "@users.noreply.github.com";
            }
        }

        throw new IllegalArgumentException("Email is not available from " + provider + " OAuth profile.");
    }

    /** Resolves a display name from known OAuth profile attributes. */
    private String extractDisplayName(Map<String, Object> attributes, String email) {
        String name = firstNonBlank(
                toString(attributes.get("name")),
                toString(attributes.get("preferred_username")),
                toString(attributes.get("login")));

        if (name != null) {
            return name;
        }

        String givenName = toString(attributes.get("given_name"));
        String familyName = toString(attributes.get("family_name"));
        String combinedName = firstNonBlank(
                joinWithSpace(givenName, familyName),
                givenName,
                familyName);

        if (combinedName != null) {
            return combinedName;
        }

        return email.substring(0, email.indexOf('@'));
    }

    /** Joins two non-blank strings with a single space. */
    private String joinWithSpace(String left, String right) {
        if (left == null || left.isBlank()) {
            return right;
        }
        if (right == null || right.isBlank()) {
            return left;
        }
        return left + " " + right;
    }

    /** Returns the first non-blank string in priority order. */
    private String firstNonBlank(String... values) {
        for (String value : values) {
            if (value != null && !value.isBlank()) {
                return value;
            }
        }
        return null;
    }

    /** Safely converts an arbitrary OAuth attribute value to String. */
    private String toString(Object value) {
        return value == null ? null : String.valueOf(value);
    }
}
