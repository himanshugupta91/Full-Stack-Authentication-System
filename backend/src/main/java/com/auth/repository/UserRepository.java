package com.auth.repository;

import com.auth.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository for User entity operations.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {

    /** Finds a user by email address (case-insensitive). */
    Optional<User> findByEmailIgnoreCase(String email);

    /** Checks whether a user exists for the given email (case-insensitive). */
    boolean existsByEmailIgnoreCase(String email);

    /** Finds a user by active password-reset token hash. */
    Optional<User> findByResetToken(String resetToken);

    /** Finds a user by current refresh token hash. */
    Optional<User> findByRefreshToken(String refreshToken);

    /** Finds an OAuth user by provider id + provider user id. */
    Optional<User> findByAuthProviderAndAuthProviderUserId(String authProvider, String authProviderUserId);

    /** Counts users with enabled=true for admin metrics. */
    long countByEnabledTrue();
}
