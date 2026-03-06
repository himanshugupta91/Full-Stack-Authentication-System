package com.auth.service.auth;

import com.auth.entity.Role;
import com.auth.entity.RoleName;
import com.auth.entity.User;
import com.auth.service.RoleService;
import com.auth.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.HashSet;
import java.util.Locale;
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
    public User loadOrCreateUser(OAuth2AuthenticationToken authenticationToken, OAuth2User oauth2User) {
        String provider = normalizeProvider(authenticationToken.getAuthorizedClientRegistrationId());
        Map<String, Object> attributes = oauth2User.getAttributes();
        String providerUserId = extractProviderUserId(provider, attributes);

        Optional<User> existingUserByProviderOpt = userService.findByAuthProviderAndAuthProviderUserId(provider, providerUserId);
        if (existingUserByProviderOpt.isPresent()) {
            User existingUser = existingUserByProviderOpt.get();
            String displayName = extractDisplayName(attributes, existingUser.getEmail());
            User updatedUser = updateExistingUserIfNeeded(existingUser, provider, providerUserId, displayName);
            return updatedUser;
        }

        String email = extractEmail(provider, providerUserId, attributes);
        String name = extractDisplayName(attributes, email);

        Optional<User> existingUserOpt = userService.findByEmail(email);
        User user = existingUserOpt
                .map(existingUser -> updateExistingUserIfNeeded(existingUser, provider, providerUserId, name))
                .orElseGet(() -> createNewOAuthUser(email, name, provider, providerUserId));
        return user;
    }

    private User createNewOAuthUser(String email, String name, String provider, String providerUserId) {
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(UUID.randomUUID().toString()));
        user.setEnabled(true);
        user.setAuthProvider(provider);
        user.setAuthProviderUserId(providerUserId);

        Set<Role> roles = new HashSet<>();
        roles.add(roleService.findOrCreateRole(RoleName.ROLE_USER));
        user.setRoles(roles);
        User savedUser = userService.save(user);
        return savedUser;
    }

    /** Updates existing OAuth-linked users only when persistence changes are required. */
    private User updateExistingUserIfNeeded(User user, String provider, String providerUserId, String displayName) {
        boolean changed = false;

        if (!user.isEnabled()) {
            user.setEnabled(true);
            changed = true;
        }

        if (!StringUtils.hasText(user.getAuthProvider()) && StringUtils.hasText(provider)) {
            user.setAuthProvider(provider);
            changed = true;
        }

        if (!StringUtils.hasText(user.getAuthProviderUserId()) && StringUtils.hasText(providerUserId)) {
            user.setAuthProviderUserId(providerUserId);
            changed = true;
        }

        if (!StringUtils.hasText(user.getName()) && StringUtils.hasText(displayName)) {
            user.setName(displayName);
            changed = true;
        }

        if (!changed) {
            return user;
        }
        User updatedUser = userService.save(user);
        return updatedUser;
    }

    /** Extracts a reliable email from provider attributes with provider-specific fallback logic. */
    private String extractEmail(String provider, String providerUserId, Map<String, Object> attributes) {
        String email = trimToNull(toStringValue(attributes.get("email")));
        if (StringUtils.hasText(email)) {
            String normalizedEmail = email.toLowerCase(Locale.ROOT);
            return normalizedEmail;
        }

        if ("github".equals(provider)) {
            String login = trimToNull(toStringValue(attributes.get("login")));
            if (StringUtils.hasText(login)) {
                String fallbackGithubEmail = login.toLowerCase(Locale.ROOT) + "@users.noreply.github.com";
                return fallbackGithubEmail;
            }
        }

        String providerDerivedEmail = buildProviderDerivedEmail(provider, providerUserId);
        if (providerDerivedEmail != null) {
            return providerDerivedEmail;
        }

        throw new IllegalArgumentException("Email is not available from " + provider + " OAuth profile.");
    }

    /** Resolves a display name from known OAuth profile attributes. */
    private String extractDisplayName(Map<String, Object> attributes, String email) {
        String name = firstNonBlank(
                trimToNull(toStringValue(attributes.get("name"))),
                trimToNull(toStringValue(attributes.get("preferred_username"))),
                trimToNull(toStringValue(attributes.get("login"))));

        if (name != null) {
            return name;
        }

        String givenName = trimToNull(toStringValue(attributes.get("given_name")));
        String familyName = trimToNull(toStringValue(attributes.get("family_name")));
        String combinedName = firstNonBlank(
                joinWithSpace(givenName, familyName),
                givenName,
                familyName);

        if (combinedName != null) {
            return combinedName;
        }

        int separatorIndex = email.indexOf('@');
        if (separatorIndex <= 0) {
            return email;
        }
        String fallbackName = email.substring(0, separatorIndex);
        return fallbackName;
    }

    /** Joins two non-blank strings with a single space. */
    private String joinWithSpace(String left, String right) {
        if (!StringUtils.hasText(left)) {
            return right;
        }
        if (!StringUtils.hasText(right)) {
            return left;
        }
        return left + " " + right;
    }

    /** Returns the first non-blank string in priority order. */
    private String firstNonBlank(String... values) {
        for (String value : values) {
            if (StringUtils.hasText(value)) {
                return value;
            }
        }
        return null;
    }

    /** Safely converts an arbitrary OAuth attribute value to String. */
    private String toStringValue(Object value) {
        if (value == null) {
            return null;
        }
        String stringValue = String.valueOf(value);
        return stringValue;
    }

    private String trimToNull(String value) {
        if (!StringUtils.hasText(value)) {
            return null;
        }
        String trimmedValue = value.trim();
        return trimmedValue;
    }

    private String normalizeProvider(String rawProvider) {
        String provider = trimToNull(rawProvider);
        if (!StringUtils.hasText(provider)) {
            throw new IllegalArgumentException("OAuth provider is missing.");
        }

        String normalizedProvider = provider.toLowerCase(Locale.ROOT);
        Set<String> supportedProviders = Set.of("google", "github", "apple", "linkedin");
        if (!supportedProviders.contains(normalizedProvider)) {
            throw new IllegalArgumentException("Unsupported OAuth provider: " + normalizedProvider);
        }
        return normalizedProvider;
    }

    private String extractProviderUserId(String provider, Map<String, Object> attributes) {
        String providerUserId;
        if ("github".equals(provider)) {
            providerUserId = firstNonBlank(
                    trimToNull(toStringValue(attributes.get("id"))),
                    trimToNull(toStringValue(attributes.get("node_id"))),
                    trimToNull(toStringValue(attributes.get("login"))),
                    trimToNull(toStringValue(attributes.get("email"))),
                    trimToNull(toStringValue(attributes.get("sub"))));
        } else {
            providerUserId = firstNonBlank(
                    trimToNull(toStringValue(attributes.get("sub"))),
                    trimToNull(toStringValue(attributes.get("id"))),
                    trimToNull(toStringValue(attributes.get("email"))),
                    trimToNull(toStringValue(attributes.get("preferred_username"))),
                    trimToNull(toStringValue(attributes.get("login"))));
        }

        if (!StringUtils.hasText(providerUserId)) {
            throw new IllegalArgumentException("Provider user id is not available from " + provider + " OAuth profile.");
        }
        return providerUserId;
    }

    private String buildProviderDerivedEmail(String provider, String providerUserId) {
        if (!StringUtils.hasText(providerUserId)) {
            return null;
        }

        String sanitizedProviderUserId = providerUserId.replaceAll("[^a-zA-Z0-9._-]", "_");
        if (!StringUtils.hasText(sanitizedProviderUserId)) {
            return null;
        }

        String fallbackEmail = provider + "-" + sanitizedProviderUserId.toLowerCase(Locale.ROOT) + "@oauth.local";
        return fallbackEmail;
    }
}
