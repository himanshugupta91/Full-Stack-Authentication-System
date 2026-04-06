package com.auth.service.impl;

import com.auth.dto.response.UserDto;
import com.auth.entity.User;
import com.auth.mapper.UserMapper;
import com.auth.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("AdminServiceImpl")
class AdminServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private AdminServiceImpl adminService;

    @Test
    @DisplayName("getUsers: out-of-range inputs → normalizes paging params and defaults sort field")
    void givenOutOfRangePagingInputs_whenGettingUsers_thenNormalizesPagingAndSort() {
        // Arrange
        User user = new User();
        user.setEmail("alice@example.com");

        UserDto userDto = new UserDto();
        userDto.setEmail("alice@example.com");

        when(userRepository.findAll(org.mockito.ArgumentMatchers.<Specification<User>>isNull(), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(user)));
        when(userMapper.toDto(user)).thenReturn(userDto);

        // Act
        Page<UserDto> result = adminService.getUsers(-5, 500, null, null, null, "unsupportedField", "asc");

        // Assert
        assertEquals(1, result.getContent().size());

        ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
        verify(userRepository).findAll(org.mockito.ArgumentMatchers.<Specification<User>>isNull(),
                pageableCaptor.capture());

        Pageable pageable = pageableCaptor.getValue();
        assertEquals(0, pageable.getPageNumber());
        assertEquals(100, pageable.getPageSize());

        Sort.Order order = pageable.getSort().getOrderFor("createdAt");
        assertEquals(Sort.Direction.ASC, order.getDirection());
    }

    @Test
    @DisplayName("getUsers: invalid role filter → throws IllegalArgumentException")
    void givenInvalidRoleFilter_whenGettingUsers_thenThrowsIllegalArgumentException() {
        // Act + Assert
        assertThrows(IllegalArgumentException.class,
                () -> adminService.getUsers(0, 20, null, null, "manager", "createdAt", "desc"));

        // Assert
        verify(userRepository, never()).findAll(
                org.mockito.ArgumentMatchers.<Specification<User>>isNull(),
                any(Pageable.class));
    }
}
