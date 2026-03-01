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

    private static final String EMAIL_ATTRIBUTE = "email";
    private static final String NAME_ATTRIBUTE = "name";
    private static final String PREFERRED_USERNAME_ATTRIBUTE = "preferred_username";
    private static final String LOGIN_ATTRIBUTE = "login";
    private static final String GIVEN_NAME_ATTRIBUTE = "given_name";
    private static final String FAMILY_NAME_ATTRIBUTE = "family_name";
    private static final String GITHUB_PROVIDER = "github";
    private static final String GITHUB_NO_REPLY_SUFFIX = "@users.noreply.github.com";
    private static final char EMAIL_SEPARATOR = '@';

    private final UserService userService;

    private final RoleService roleService;

    private final PasswordEncoder passwordEncoder;

    /** Loads an existing OAuth user or creates a local enabled user profile when first seen. */
    public User loadOrCreateUser(OAuth2AuthenticationToken authenticationToken, OAuth2User oauth2User) {
        String provider = normalize(authenticationToken.getAuthorizedClientRegistrationId());
        Map<String, Object> attributes = oauth2User.getAttributes();

        String email = normalize(extractEmail(provider, attributes));
        String name = extractDisplayName(attributes, email);

        Optional<User> existingUser = userService.findByEmail(email);
        if (existingUser.isPresent()) {
            return updateExistingUserIfNeeded(existingUser.get(), provider, name);
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

    /** Updates existing OAuth-linked users only when persistence changes are required. */
    private User updateExistingUserIfNeeded(User user, String provider, String displayName) {
        boolean changed = false;

        if (!user.isEnabled()) {
            user.setEnabled(true);
            changed = true;
        }

        if (!hasText(user.getAuthProvider()) && hasText(provider)) {
            user.setAuthProvider(provider);
            changed = true;
        }

        if (!hasText(user.getName()) && hasText(displayName)) {
            user.setName(displayName);
            changed = true;
        }

        if (!changed) {
            return user;
        }
        return userService.save(user);
    }

    /** Extracts a reliable email from provider attributes with provider-specific fallback logic. */
    private String extractEmail(String provider, Map<String, Object> attributes) {
        String email = normalize(toString(attributes.get(EMAIL_ATTRIBUTE)));
        if (hasText(email)) {
            return email;
        }

        if (GITHUB_PROVIDER.equals(provider)) {
            String login = normalize(toString(attributes.get(LOGIN_ATTRIBUTE)));
            if (hasText(login)) {
                return login + GITHUB_NO_REPLY_SUFFIX;
            }
        }

        throw new IllegalArgumentException("Email is not available from " + provider + " OAuth profile.");
    }

    /** Resolves a display name from known OAuth profile attributes. */
    private String extractDisplayName(Map<String, Object> attributes, String email) {
        String name = firstNonBlank(
                normalize(toString(attributes.get(NAME_ATTRIBUTE))),
                normalize(toString(attributes.get(PREFERRED_USERNAME_ATTRIBUTE))),
                normalize(toString(attributes.get(LOGIN_ATTRIBUTE))));

        if (name != null) {
            return name;
        }

        String givenName = normalize(toString(attributes.get(GIVEN_NAME_ATTRIBUTE)));
        String familyName = normalize(toString(attributes.get(FAMILY_NAME_ATTRIBUTE)));
        String combinedName = firstNonBlank(
                joinWithSpace(givenName, familyName),
                givenName,
                familyName);

        if (combinedName != null) {
            return combinedName;
        }

        int separatorIndex = email.indexOf(EMAIL_SEPARATOR);
        if (separatorIndex <= 0) {
            return email;
        }
        return email.substring(0, separatorIndex);
    }

    /** Joins two non-blank strings with a single space. */
    private String joinWithSpace(String left, String right) {
        if (!hasText(left)) {
            return right;
        }
        if (!hasText(right)) {
            return left;
        }
        return left + " " + right;
    }

    /** Returns the first non-blank string in priority order. */
    private String firstNonBlank(String... values) {
        for (String value : values) {
            if (hasText(value)) {
                return value;
            }
        }
        return null;
    }

    /** Safely converts an arbitrary OAuth attribute value to String. */
    private String toString(Object value) {
        if (value == null) {
            return null;
        }
        return String.valueOf(value);
    }

    private String normalize(String value) {
        if (value == null) {
            return null;
        }
        return value.trim();
    }

    private boolean hasText(String value) {
        if (value == null) {
            return false;
        }
        return !value.isBlank();
    }
}
