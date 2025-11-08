package com.vbforge.concierge.service;

import com.vbforge.concierge.dto.UserDto;
import com.vbforge.concierge.enums.UserRole;

import java.util.List;

/**
 * Service interface for User operations
 */
public interface UserService {

    /**
     * Create a new user
     * @param userDto User data
     * @return Created user
     */
    UserDto createUser(UserDto userDto);

    /**
     * Update existing user
     * @param id User ID
     * @param userDto Updated data
     * @return Updated user
     */
    UserDto updateUser(Long id, UserDto userDto);

    /**
     * Change user password
     * @param id User ID
     * @param newPassword New password
     */
    void changePassword(Long id, String newPassword);

    /**
     * Delete user (soft delete)
     * @param id User ID
     */
    void deleteUser(Long id);

    /**
     * Get user by ID
     * @param id User ID
     * @return User DTO
     */
    UserDto getUserById(Long id);

    /**
     * Get user by username
     * @param username Username
     * @return User DTO
     */
    UserDto getUserByUsername(String username);

    /**
     * Get all users
     * @return List of users
     */
    List<UserDto> getAllUsers();

    /**
     * Get users by role
     * @param role User role
     * @return List of users
     */
    List<UserDto> getUsersByRole(UserRole role);

    /**
     * Get all enabled users
     * @return List of enabled users
     */
    List<UserDto> getEnabledUsers();

    /**
     * Enable user
     * @param id User ID
     */
    void enableUser(Long id);

    /**
     * Disable user
     * @param id User ID
     */
    void disableUser(Long id);

    /**
     * Check if username exists
     * @param username Username
     * @return true if exists
     */
    boolean existsByUsername(String username);

    /**
     * Link user to concierge
     * @param userId User ID
     * @param conciergeId Concierge ID
     */
    void linkToConcierge(Long userId, Long conciergeId);

    /**
     * Unlink user from concierge
     * @param userId User ID
     */
    void unlinkFromConcierge(Long userId);

}
