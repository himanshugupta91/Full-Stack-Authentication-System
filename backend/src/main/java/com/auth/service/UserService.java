package com.auth.service;

import com.auth.entity.User;

import java.util.Optional;

/**
 * Service interface for User operations.
 */
public interface UserService {
    /**
     * Get user by email.
     *
     * @param email User email
     * @return User object
     */
    User getUserByEmail(String email);

    /**
     * Find user by email (returns Optional).
     *
     * @param email User email
     * @return Optional User
     */
    Optional<User> findByEmail(String email);

    /**
     * Check if user exists by email.
     *
     * @param email User email
     * @return true if exists
     */
    boolean existsByEmail(String email);

    /**
     * Save user entity.
     *
     * @param user User to save
     * @return Saved user
     */
    User save(User user);

    /**
     * Find user by reset token.
     *
     * @param token Reset token
     * @return Optional User
     */
    Optional<User> findByResetToken(String token);

    /**
     * Find user by refresh token.
     *
     * @param token Refresh token
     * @return Optional User
     */
    Optional<User> findByRefreshToken(String token);

    /**
     * Finds a user by OAuth provider + provider user id.
     *
     * @param authProvider       OAuth provider id (e.g., google/github/apple/linkedin)
     * @param authProviderUserId Stable user id returned by the provider
     * @return Optional User
     */
    Optional<User> findByAuthProviderAndAuthProviderUserId(String authProvider, String authProviderUserId);
}
