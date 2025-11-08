package com.vbforge.concierge.security;

import com.vbforge.concierge.entity.User;
import com.vbforge.concierge.enums.UserRole;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

/**
 * Custom UserDetails implementation
 * Wraps User entity for Spring Security
 */
@Getter
public class CustomUserDetails implements UserDetails{

    private final User user;
    private final Collection<? extends GrantedAuthority> authorities;

    public CustomUserDetails(User user){
        this.user = user;
        this.authorities = Collections.singletonList(
                new SimpleGrantedAuthority(user.getRole().getAuthority())
        );
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return user.isEnabled() && !user.isDeleted();
    }

    /**
     * Get user ID
     */
    public Long getUserId() {
        return user.getId();
    }

    /**
     * Get user role
     */
    public UserRole getRole() {
        return user.getRole();
    }

    /**
     * Check if user is admin
     */
    public boolean isAdmin() {
        return user.getRole() == UserRole.ADMIN;
    }

    /**
     * Check if user is concierge
     */
    public boolean isConcierge() {
        return user.getRole() == UserRole.CONCIERGE;
    }


}
