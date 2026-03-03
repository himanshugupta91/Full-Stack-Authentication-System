package com.auth.service.impl;

import com.auth.entity.User;
import com.auth.exception.ResourceNotFoundException;
import com.auth.repository.UserRepository;
import com.auth.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Locale;
import java.util.Optional;

/**
 * Implementation of UserService.
 */
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    /** Retrieves a user by email or throws a domain-level not-found exception. */
    @Override
    public User getUserByEmail(String email) {
        String normalizedEmail = normalizeEmail(email);
        Optional<User> userOpt = userRepository.findByEmailIgnoreCase(normalizedEmail);
        User user = userOpt
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
        return user;
    }

    /**
     * Finds a user by email, returning an Optional.
     * Useful for checks where the user might not exist.
     *
     * @param email The email to search for.
     * @return Optional containing the User if found, or empty.
     */
    @Override
    public Optional<User> findByEmail(String email) {
        String normalizedEmail = normalizeEmail(email);
        if (!StringUtils.hasText(normalizedEmail)) {
            return Optional.empty();
        }
        Optional<User> userOpt = userRepository.findByEmailIgnoreCase(normalizedEmail);
        return userOpt;
    }

    /**
     * Checks if a user exists with the given email.
     *
     * @param email The email to check.
     * @return true if a user exists, false otherwise.
     */
    @Override
    public boolean existsByEmail(String email) {
        String normalizedEmail = normalizeEmail(email);
        if (!StringUtils.hasText(normalizedEmail)) {
            return false;
        }
        boolean userExists = userRepository.existsByEmailIgnoreCase(normalizedEmail);
        return userExists;
    }

    /**
     * Saves a user entity to the database.
     * Handles both create and update operations.
     *
     * @param user The user entity to save.
     * @return The saved user entity.
     */
    @Override
    @Transactional
    public User save(User user) {
        if (user != null && StringUtils.hasText(user.getEmail())) {
            user.setEmail(normalizeEmail(user.getEmail()));
        }
        User savedUser = userRepository.save(user);
        return savedUser;
    }

    /**
     * Finds a user associated with a password reset token.
     *
     * @param token The reset token.
     * @return Optional containing the User if found, or empty.
     */
    @Override
    public Optional<User> findByResetToken(String token) {
        Optional<User> userOpt = userRepository.findByResetToken(token);
        return userOpt;
    }

    /** Finds a user associated with a persisted refresh token. */
    @Override
    public Optional<User> findByRefreshToken(String token) {
        Optional<User> userOpt = userRepository.findByRefreshToken(token);
        return userOpt;
    }

    /** Finds a user by OAuth provider + provider user id. */
    @Override
    public Optional<User> findByAuthProviderAndAuthProviderUserId(String authProvider, String authProviderUserId) {
        Optional<User> userOpt = userRepository.findByAuthProviderAndAuthProviderUserId(authProvider, authProviderUserId);
        return userOpt;
    }

    private String normalizeEmail(String email) {
        if (!StringUtils.hasText(email)) {
            return null;
        }
        String normalizedEmail = email.trim().toLowerCase(Locale.ROOT);
        return normalizedEmail;
    }
}
