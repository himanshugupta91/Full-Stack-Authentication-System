package com.auth.service.impl;

import com.auth.dto.request.ChangePasswordRequest;
import com.auth.dto.request.LoginRequest;
import com.auth.dto.response.MessageResponse;
import com.auth.dto.request.RegisterRequest;
import com.auth.dto.request.ResetPasswordRequest;
import com.auth.dto.request.UpdatePasswordRequest;
import com.auth.entity.Role;
import com.auth.entity.User;
import com.auth.exception.TokenValidationException;
import com.auth.exception.UserAlreadyExistsException;
import com.auth.mapper.UserMapper;
import com.auth.service.RoleService;
import com.auth.service.UserService;
import com.auth.service.auth.AuthAbuseProtectionService;
import com.auth.service.auth.AuthTokenService;
import com.auth.service.support.EmailService;
import com.auth.service.support.OtpService;
import com.auth.service.support.PasswordPolicyService;
import com.auth.service.support.TokenHashService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock
    private UserService userService;

    @Mock
    private RoleService roleService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private AuthTokenService authTokenService;

    @Mock
    private EmailService emailService;

    @Mock
    private OtpService otpService;

    @Mock
    private UserMapper userMapper;

    @Mock
    private TokenHashService tokenHashService;

    @Mock
    private PasswordPolicyService passwordPolicyService;

    @Mock
    private AuthAbuseProtectionService authAbuseProtectionService;

    @InjectMocks
    private AuthServiceImpl authService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(authService, "otpExpirationMinutes", 5);
        ReflectionTestUtils.setField(authService, "resetTokenExpirationMinutes", 30);
    }

    @Test
    void register_whenEmailAlreadyExists_throwsUserAlreadyExistsException() {
        RegisterRequest request = new RegisterRequest();
        request.setEmail("alice@example.com");

        when(userService.existsByEmail(request.getEmail())).thenReturn(true);

        assertThrows(UserAlreadyExistsException.class, () -> authService.register(request));
    }

    @Test
    void register_whenValidRequest_savesUserAndSendsOtp() {
        RegisterRequest request = new RegisterRequest();
        request.setName("Alice");
        request.setEmail("alice@example.com");
        request.setPassword("Password1");

        User mappedUser = new User();
        mappedUser.setName(request.getName());
        mappedUser.setEmail(request.getEmail());

        Role userRole = new Role();
        userRole.setName(Role.RoleName.ROLE_USER);

        when(userService.existsByEmail(request.getEmail())).thenReturn(false);
        when(userMapper.toEntity(request)).thenReturn(mappedUser);
        when(passwordEncoder.encode(request.getPassword())).thenReturn("encoded-password");
        when(otpService.generateOtp()).thenReturn("123456");
        when(tokenHashService.hash("123456")).thenReturn("otp-hash");
        when(roleService.findOrCreateRole(Role.RoleName.ROLE_USER)).thenReturn(userRole);
        when(userService.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        MessageResponse response = authService.register(request);

        assertTrue(response.isSuccess());
        verify(passwordPolicyService).validate(request.getPassword(), request.getEmail());
        verify(emailService).sendOtpEmail(request.getEmail(), request.getName(), "123456");

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userService).save(userCaptor.capture());
        User savedUser = userCaptor.getValue();

        assertEquals("encoded-password", savedUser.getPassword());
        assertEquals("otp-hash", savedUser.getVerificationOtp());
        assertEquals(Set.of(userRole), savedUser.getRoles());
        assertEquals("local", savedUser.getAuthProvider());
        assertFalse(savedUser.isEnabled());
    }

    @Test
    void login_whenUserMissing_recordsFailedAttemptAndThrows() {
        LoginRequest request = new LoginRequest();
        request.setEmail("missing@example.com");
        request.setPassword("Password1");

        when(userService.findByEmail(request.getEmail())).thenReturn(Optional.empty());

        assertThrows(BadCredentialsException.class, () -> authService.login(request));

        verify(authAbuseProtectionService).guardLoginAttempt(request.getEmail());
        verify(authAbuseProtectionService).recordFailedLogin(request.getEmail());
        verifyNoInteractions(authenticationManager);
    }

    @Test
    void resetPassword_whenUserMissing_returnsGenericSuccessMessage() {
        ResetPasswordRequest request = new ResetPasswordRequest();
        request.setEmail("missing@example.com");

        when(userService.findByEmail(request.getEmail())).thenReturn(Optional.empty());

        MessageResponse response = authService.resetPassword(request);

        assertTrue(response.isSuccess());
        assertEquals("If an account exists with this email, a reset link will be sent.", response.getMessage());
    }

    @Test
    void updatePassword_whenTokenNotFound_throwsTokenValidationException() {
        UpdatePasswordRequest request = new UpdatePasswordRequest();
        request.setToken("reset-token");
        request.setNewPassword("Password2");

        when(tokenHashService.hash(request.getToken())).thenReturn("reset-token-hash");
        when(userService.findByResetToken("reset-token-hash")).thenReturn(Optional.empty());

        assertThrows(TokenValidationException.class, () -> authService.updatePassword(request));
    }

    @Test
    void changePassword_whenCurrentPasswordDoesNotMatch_throwsBadCredentials() {
        ChangePasswordRequest request = new ChangePasswordRequest();
        request.setCurrentPassword("wrong-current");
        request.setNewPassword("Password2");

        User user = new User();
        user.setEmail("alice@example.com");
        user.setPassword("stored-password");

        when(userService.findByEmail("alice@example.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())).thenReturn(false);

        assertThrows(BadCredentialsException.class,
                () -> authService.changePassword("alice@example.com", request));
    }
}
