package com.vbforge.concierge.service;

import com.vbforge.concierge.dto.UserDto;
import com.vbforge.concierge.entity.User;
import com.vbforge.concierge.enums.UserRole;
import com.vbforge.concierge.exception.InvalidInputException;
import com.vbforge.concierge.exception.ResourceNotFoundException;
import com.vbforge.concierge.mapper.UserMapper;
import com.vbforge.concierge.repository.ConciergeRepository;
import com.vbforge.concierge.repository.UserRepository;
import com.vbforge.concierge.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Unit tests for UserService
 */
@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private ConciergeRepository conciergeRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    private User adminUser;
    private UserDto adminUserDto;

    @BeforeEach
    void setUp() {
        adminUser = User.builder()
                .id(1L)
                .username("admin")
                .password("encodedPassword")
                .role(UserRole.ADMIN)
                .enabled(true)
                .build();

        adminUserDto = UserDto.builder()
                .id(1L)
                .username("admin")
                .password("admin123")
                .role(UserRole.ADMIN)
                .enabled(true)
                .build();
    }

    @Test
    void testCreateUser() {
        // Given
        when(userRepository.existsByUsernameAndDeletedFalse(anyString())).thenReturn(false);
        when(userMapper.toEntity(adminUserDto)).thenReturn(adminUser);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userRepository.save(adminUser)).thenReturn(adminUser);
        when(userMapper.toDto(adminUser)).thenReturn(adminUserDto);

        // When
        UserDto result = userService.createUser(adminUserDto);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getUsername()).isEqualTo("admin");
        verify(passwordEncoder).encode("admin123");
        verify(userRepository).save(adminUser);
    }

    @Test
    void testShouldThrowExceptionWhenCreatingUserWithExistingUsername() {
        // Given
        when(userRepository.existsByUsernameAndDeletedFalse("admin")).thenReturn(true);

        // When/Then
        assertThatThrownBy(() -> userService.createUser(adminUserDto))
                .isInstanceOf(InvalidInputException.class)
                .hasMessageContaining("already exists");

        verify(userRepository, never()).save(any());
    }

    @Test
    void testShouldThrowExceptionWhenPasswordTooShort() {
        // Given
        adminUserDto.setPassword("12345");

        // When/Then
        assertThatThrownBy(() -> userService.createUser(adminUserDto))
                .isInstanceOf(InvalidInputException.class)
                .hasMessageContaining("at least 6 characters");
    }

    @Test
    void testGetUserById() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.of(adminUser));
        when(userMapper.toDto(adminUser)).thenReturn(adminUserDto);

        // When
        UserDto result = userService.getUserById(1L);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getUsername()).isEqualTo("admin");
    }

    @Test
    void testShouldThrowExceptionWhenUserNotFound() {
        // Given
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        // When/Then
        assertThatThrownBy(() -> userService.getUserById(999L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void testGetUserByUsername() {
        // Given
        when(userRepository.findByUsernameAndDeletedFalse("admin")).thenReturn(Optional.of(adminUser));
        when(userMapper.toDto(adminUser)).thenReturn(adminUserDto);

        // When
        UserDto result = userService.getUserByUsername("admin");

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getUsername()).isEqualTo("admin");
    }

    @Test
    void testGetUsersByRole() {
        // Given
        List<User> admins = List.of(adminUser);
        when(userRepository.findByRoleAndDeletedFalse(UserRole.ADMIN)).thenReturn(admins);
        when(userMapper.toDtoList(admins)).thenReturn(List.of(adminUserDto));

        // When
        List<UserDto> result = userService.getUsersByRole(UserRole.ADMIN);

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getRole()).isEqualTo(UserRole.ADMIN);
    }

    @Test
    void testChangePassword() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.of(adminUser));
        when(passwordEncoder.encode("newPassword123")).thenReturn("newEncodedPassword");
        when(userRepository.save(adminUser)).thenReturn(adminUser);

        // When
        userService.changePassword(1L, "newPassword123");

        // Then
        verify(passwordEncoder).encode("newPassword123");
        verify(userRepository).save(adminUser);
    }

    @Test
    void testShouldThrowExceptionWhenChangingToShortPassword() {
        // ✅ Using lenient() to suppress UnnecessaryStubbingException warning
        // This tells Mockito: "it's okay if this stub is never called"
        // Useful when we keep shared or default stubs for consistency across tests
        lenient().when(userRepository.findById(1L)).thenReturn(Optional.of(adminUser));

        // When / Then
        // This test verifies that a too-short password immediately triggers a validation error
        // The repository call is never reached because of early validation failure
        assertThatThrownBy(() -> userService.changePassword(1L, "12345"))
                .isInstanceOf(InvalidInputException.class)
                .hasMessageContaining("at least 6 characters");
    }

    @Test
    void testShouldThrowExceptionWhenChangingToShortPassword_noLenient() {
        // ✅ No stubbing needed here
        // Since the method throws an exception before interacting with the repository,
        // adding any stubbing would be unnecessary (and trigger UnnecessaryStubbingException)
        // This is the cleanest version of the test

        assertThatThrownBy(() -> userService.changePassword(1L, "12345"))
                .isInstanceOf(InvalidInputException.class)
                .hasMessageContaining("at least 6 characters");
    }

    @Test
    void testEnableUser() {
        // Given
        adminUser.setEnabled(false);
        when(userRepository.findById(1L)).thenReturn(Optional.of(adminUser));
        when(userRepository.save(adminUser)).thenReturn(adminUser);

        // When
        userService.enableUser(1L);

        // Then
        assertThat(adminUser.isEnabled()).isTrue();
        verify(userRepository).save(adminUser);
    }

    @Test
    void testDisableUser() {
        // Given
        adminUser.setEnabled(true);
        when(userRepository.findById(1L)).thenReturn(Optional.of(adminUser));
        when(userRepository.save(adminUser)).thenReturn(adminUser);

        // When
        userService.disableUser(1L);

        // Then
        assertThat(adminUser.isEnabled()).isFalse();
        verify(userRepository).save(adminUser);
    }

    @Test
    void testDeleteUser() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.of(adminUser));
        when(userRepository.save(adminUser)).thenReturn(adminUser);

        // When
        userService.deleteUser(1L);

        // Then
        verify(userRepository).save(adminUser);
        assertThat(adminUser.isDeleted()).isTrue();
    }

    @Test
    void testCheckIfUsernameExists() {
        // Given
        when(userRepository.existsByUsernameAndDeletedFalse("admin")).thenReturn(true);

        // When
        boolean exists = userService.existsByUsername("admin");

        // Then
        assertThat(exists).isTrue();
    }

}
