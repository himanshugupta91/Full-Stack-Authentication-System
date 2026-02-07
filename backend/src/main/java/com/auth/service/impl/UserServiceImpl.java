package com.auth.service.impl;

import com.auth.entity.User;
import com.auth.repository.UserRepository;
import com.auth.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Implementation of UserService.
 */
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    /**
     * Retrieves a user by their email address.
     *
     * @param email The email to search for.
     * @return The User entity found.
     * @throws RuntimeException if no user is found with the given email.
     */
    @Override
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));
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
        return userRepository.findByEmail(email);
    }

    /**
     * Checks if a user exists with the given email.
     *
     * @param email The email to check.
     * @return true if a user exists, false otherwise.
     */
    @Override
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
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
        return userRepository.save(user);
    }

    /**
     * Finds a user associated with a password reset token.
     *
     * @param token The reset token.
     * @return Optional containing the User if found, or empty.
     */
    @Override
    public Optional<User> findByResetToken(String token) {
        return userRepository.findByResetToken(token);
    }
}
