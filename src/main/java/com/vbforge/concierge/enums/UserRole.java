package com.vbforge.concierge.enums;

/**
 * User roles for authentication and authorization
 */
public enum UserRole {

    ADMIN("ROLE_ADMIN", "Administrator - Full access"),
    CONCIERGE("ROLE_CONCIERGE", "Concierge - Read-only access");

    private final String authority;
    private final String description;

    UserRole(String authority, String description) {
        this.authority = authority;
        this.description = description;
    }

    public String getAuthority() {
        return authority;
    }

    public String getDescription() {
        return description;
    }

    /**
     * Check if role has admin privileges
     */
    public boolean isAdmin() {
        return this == ADMIN;
    }

    /**
     * Check if role is concierge
     */
    public boolean isConcierge() {
        return this == CONCIERGE;
    }
}