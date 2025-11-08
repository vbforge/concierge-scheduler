package com.vbforge.concierge.security;

import com.vbforge.concierge.entity.User;
import com.vbforge.concierge.enums.UserRole;
import com.vbforge.concierge.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.when;

/**
 * Unit tests for CustomUserDetailsService
 */
@ExtendWith(MockitoExtension.class)
public class CustomUserDetailsServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CustomUserDetailsService userDetailsService;

    private User adminUser;

    @BeforeEach
    void setUp() {
        adminUser = User.builder()
                .id(1L)
                .username("admin")
                .password("encodedPassword")
                .role(UserRole.ADMIN)
                .enabled(true)
                .build();
    }

    @Test
    void testLoadUserByUsername() {
        // Given
        when(userRepository.findByUsernameAndDeletedFalse("admin"))
                .thenReturn(Optional.of(adminUser));

        // When
        UserDetails userDetails = userDetailsService.loadUserByUsername("admin");

        // Then
        assertThat(userDetails).isNotNull();
        assertThat(userDetails.getUsername()).isEqualTo("admin");
        assertThat(userDetails.getPassword()).isEqualTo("encodedPassword");
        assertThat(userDetails.isEnabled()).isTrue();
        assertThat(userDetails.getAuthorities()).hasSize(1);
    }

    @Test
    void testShouldThrowExceptionWhenUserNotFound() {
        // Given
        when(userRepository.findByUsernameAndDeletedFalse("unknown"))
                .thenReturn(Optional.empty());

        // When/Then
        assertThatThrownBy(() -> userDetailsService.loadUserByUsername("unknown"))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessageContaining("User not found");
    }

    @Test
    void testShouldThrowExceptionWhenUserIsDisabled() {
        // Given
        adminUser.setEnabled(false);
        when(userRepository.findByUsernameAndDeletedFalse("admin"))
                .thenReturn(Optional.of(adminUser));

        // When/Then
        assertThatThrownBy(() -> userDetailsService.loadUserByUsername("admin"))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessageContaining("disabled");
    }

    @Test
    void testLoadUserWithCorrectAuthority() {
        // Given
        when(userRepository.findByUsernameAndDeletedFalse("admin"))
                .thenReturn(Optional.of(adminUser));

        // When
        UserDetails userDetails = userDetailsService.loadUserByUsername("admin");
        CustomUserDetails customUserDetails = (CustomUserDetails) userDetails;

        // Then
        assertThat(customUserDetails.getRole()).isEqualTo(UserRole.ADMIN);
        assertThat(customUserDetails.isAdmin()).isTrue();
        assertThat(customUserDetails.isConcierge()).isFalse();
        assertThat(customUserDetails.getUserId()).isEqualTo(1L);
    }

}
