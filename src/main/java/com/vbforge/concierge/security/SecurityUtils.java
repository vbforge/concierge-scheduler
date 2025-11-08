package com.vbforge.concierge.security;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/**
 * Utility class for accessing security context information
 */
@Component
@RequiredArgsConstructor
public class SecurityUtils {

    /**
     * Get current authenticated user
     * @return CustomUserDetails or null if not authenticated
     */
    public static CustomUserDetails getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }

        Object principal = authentication.getPrincipal();
        if (principal instanceof CustomUserDetails) {
            return (CustomUserDetails) principal;
        }

        return null;
    }

    /**
     * Get current username
     * @return username or null if not authenticated
     */
    public static String getCurrentUsername() {
        CustomUserDetails user = getCurrentUser();
        return user != null ? user.getUsername() : null;
    }

    /**
     * Get current user ID
     * @return user ID or null if not authenticated
     */
    public static Long getCurrentUserId() {
        CustomUserDetails user = getCurrentUser();
        return user != null ? user.getUserId() : null;
    }

    /**
     * Check if current user is admin
     * @return true if admin, false otherwise
     */
    public static boolean isCurrentUserAdmin() {
        CustomUserDetails user = getCurrentUser();
        return user != null && user.isAdmin();
    }

    /**
     * Check if current user is concierge
     * @return true if concierge, false otherwise
     */
    public static boolean isCurrentUserConcierge() {
        CustomUserDetails user = getCurrentUser();
        return user != null && user.isConcierge();
    }

    /**
     * Check if user is authenticated
     * @return true if authenticated, false otherwise
     */
    public static boolean isAuthenticated() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null &&
                authentication.isAuthenticated() &&
                !"anonymousUser".equals(authentication.getPrincipal());
    }

}
