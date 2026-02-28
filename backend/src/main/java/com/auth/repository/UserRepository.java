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

    /** Finds a user by unique email address. */
    Optional<User> findByEmail(String email);

    /** Checks whether a user exists for the given email. */
    boolean existsByEmail(String email);

    /** Finds a user by active password-reset token hash. */
    Optional<User> findByResetToken(String resetToken);

    /** Finds a user by current refresh token hash. */
    Optional<User> findByRefreshToken(String refreshToken);

    /** Counts users with enabled=true for admin metrics. */
    long countByEnabledTrue();
}
