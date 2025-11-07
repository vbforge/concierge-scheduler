package com.vbforge.concierge.repository;

import com.vbforge.concierge.entity.User;
import com.vbforge.concierge.enums.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for User entity
 * Will be fully used in Phase 4 (Security)
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Find user by username
     */
    Optional<User> findByUsernameAndDeletedFalse(String username);

    /**
     * Find all users by role
     */
    List<User> findByRoleAndDeletedFalse(UserRole role);

    /**
     * Find all enabled users
     */
    List<User> findByEnabledTrueAndDeletedFalse();

    /**
     * Check if username exists
     */
    boolean existsByUsernameAndDeletedFalse(String username);

    /**
     * Find user by linked concierge
     */
    Optional<User> findByConciergeIdAndDeletedFalse(Long conciergeId);

}