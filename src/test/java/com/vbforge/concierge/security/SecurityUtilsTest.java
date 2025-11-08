package com.vbforge.concierge.security;

import com.vbforge.concierge.entity.User;
import com.vbforge.concierge.enums.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for SecurityUtils
 */
public class SecurityUtilsTest {

    private CustomUserDetails adminUserDetails;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.clearContext();

        User adminUser = User.builder()
                .id(1L)
                .username("admin")
                .password("encodedPassword")
                .role(UserRole.ADMIN)
                .enabled(true)
                .build();

        adminUserDetails = new CustomUserDetails(adminUser);
    }

    @Test
    void testGetCurrentUser() {
        // Given
        Authentication auth = new UsernamePasswordAuthenticationToken(
                adminUserDetails, null, adminUserDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);

        // When
        CustomUserDetails current = SecurityUtils.getCurrentUser();

        // Then
        assertThat(current).isNotNull();
        assertThat(current.getUsername()).isEqualTo("admin");
    }

    @Test
    void testReturnNullWhenNotAuthenticated() {
        // When
        CustomUserDetails current = SecurityUtils.getCurrentUser();

        // Then
        assertThat(current).isNull();
    }

    @Test
    void testGetCurrentUsername() {
        // Given
        Authentication auth = new UsernamePasswordAuthenticationToken(
                adminUserDetails, null, adminUserDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);

        // When
        String username = SecurityUtils.getCurrentUsername();

        // Then
        assertThat(username).isEqualTo("admin");
    }

    @Test
    void testGetCurrentUserId() {
        // Given
        Authentication auth = new UsernamePasswordAuthenticationToken(
                adminUserDetails, null, adminUserDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);

        // When
        Long userId = SecurityUtils.getCurrentUserId();

        // Then
        assertThat(userId).isEqualTo(1L);
    }

    @Test
    void testCheckIfCurrentUserIsAdmin() {
        // Given
        Authentication auth = new UsernamePasswordAuthenticationToken(
                adminUserDetails, null, adminUserDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);

        // When
        boolean isAdmin = SecurityUtils.isCurrentUserAdmin();

        // Then
        assertThat(isAdmin).isTrue();
    }

    @Test
    void testCheckIfCurrentUserIsConcierge() {
        // Given
        Authentication auth = new UsernamePasswordAuthenticationToken(
                adminUserDetails, null, adminUserDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);

        // When
        boolean isConcierge = SecurityUtils.isCurrentUserConcierge();

        // Then
        assertThat(isConcierge).isFalse();
    }

    @Test
    void testCheckIfAuthenticated() {
        // Given
        Authentication auth = new UsernamePasswordAuthenticationToken(
                adminUserDetails, null, adminUserDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);

        // When
        boolean isAuthenticated = SecurityUtils.isAuthenticated();

        // Then
        assertThat(isAuthenticated).isTrue();
    }

    @Test
    void testReturnFalseWhenNotAuthenticated() {
        // When
        boolean isAuthenticated = SecurityUtils.isAuthenticated();

        // Then
        assertThat(isAuthenticated).isFalse();
    }

}
