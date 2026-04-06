package com.auth.service.impl;

import com.auth.entity.User;
import com.auth.exception.ResourceNotFoundException;
import com.auth.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserServiceImpl")
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    @DisplayName("getUserByEmail: user exists → returns User entity")
    void givenExistingUserEmail_whenGettingUserByEmail_thenReturnsUser() {
        // Arrange
        User user = new User();
        user.setEmail("alice@example.com");

        when(userRepository.findByEmailIgnoreCase("alice@example.com")).thenReturn(Optional.of(user));

        // Act
        User result = userService.getUserByEmail("alice@example.com");

        // Assert
        assertEquals(user, result);
    }

    @Test
    @DisplayName("getUserByEmail: user missing → throws ResourceNotFoundException")
    void givenMissingUserEmail_whenGettingUserByEmail_thenThrowsResourceNotFoundException() {
        // Arrange
        when(userRepository.findByEmailIgnoreCase("missing@example.com")).thenReturn(Optional.empty());

        // Act + Assert
        assertThrows(ResourceNotFoundException.class, () -> userService.getUserByEmail("missing@example.com"));
    }
}
