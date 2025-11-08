package com.vbforge.concierge.service.impl;

import com.vbforge.concierge.dto.UserDto;
import com.vbforge.concierge.entity.Concierge;
import com.vbforge.concierge.entity.User;
import com.vbforge.concierge.enums.UserRole;
import com.vbforge.concierge.exception.ConciergeNotFoundException;
import com.vbforge.concierge.exception.InvalidInputException;
import com.vbforge.concierge.exception.ResourceNotFoundException;
import com.vbforge.concierge.mapper.UserMapper;
import com.vbforge.concierge.repository.ConciergeRepository;
import com.vbforge.concierge.repository.UserRepository;
import com.vbforge.concierge.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Implementation of UserService
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final ConciergeRepository conciergeRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public UserDto createUser(UserDto userDto) {
        log.info("Creating user: {}", userDto.getUsername());

        // Validate username is unique
        if (userRepository.existsByUsernameAndDeletedFalse(userDto.getUsername())) {
            throw new InvalidInputException("Username already exists: " + userDto.getUsername());
        }

        // Validate password
        if (userDto.getPassword() == null || userDto.getPassword().length() < 6) {
            throw new InvalidInputException("Password must be at least 6 characters");
        }

        User user = userMapper.toEntity(userDto);

        // Encode password
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));

        // Link to concierge if specified
        if (userDto.getConciergeId() != null) {
            Concierge concierge = conciergeRepository.findById(userDto.getConciergeId())
                    .orElseThrow(() -> new ConciergeNotFoundException(userDto.getConciergeId()));
            user.setConcierge(concierge);
        }

        User saved = userRepository.save(user);

        log.info("User created with ID: {}", saved.getId());
        return userMapper.toDto(saved);
    }

    @Override
    @Transactional
    public UserDto updateUser(Long id, UserDto userDto) {
        log.info("Updating user ID: {}", id);

        User existing = findUserById(id);

        // Check if username is being changed and if new username is unique
        if (!existing.getUsername().equals(userDto.getUsername())) {
            if (userRepository.existsByUsernameAndDeletedFalse(userDto.getUsername())) {
                throw new InvalidInputException("Username already exists: " + userDto.getUsername());
            }
        }

        userMapper.updateEntityFromDto(userDto, existing);

        // Update concierge link if changed
        if (userDto.getConciergeId() != null &&
                (existing.getConcierge() == null ||
                        !existing.getConcierge().getId().equals(userDto.getConciergeId()))) {
            Concierge concierge = conciergeRepository.findById(userDto.getConciergeId())
                    .orElseThrow(() -> new ConciergeNotFoundException(userDto.getConciergeId()));
            existing.setConcierge(concierge);
        } else if (userDto.getConciergeId() == null) {
            existing.setConcierge(null);
        }

        User updated = userRepository.save(existing);

        log.info("User updated: {}", updated.getUsername());
        return userMapper.toDto(updated);
    }

    @Override
    @Transactional
    public void changePassword(Long id, String newPassword) {
        log.info("Changing password for user ID: {}", id);

        if (newPassword == null || newPassword.length() < 6) {
            throw new InvalidInputException("Password must be at least 6 characters");
        }

        User user = findUserById(id);
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        log.info("Password changed for user: {}", user.getUsername());
    }

    @Override
    @Transactional
    public void deleteUser(Long id) {
        log.info("Deleting user ID: {}", id);

        User user = findUserById(id);
        user.softDelete();
        userRepository.save(user);

        log.info("User soft deleted: {}", user.getUsername());
    }

    @Override
    public UserDto getUserById(Long id) {
        log.debug("Getting user by ID: {}", id);

        User user = findUserById(id);
        return userMapper.toDto(user);
    }

    @Override
    public UserDto getUserByUsername(String username) {
        log.debug("Getting user by username: {}", username);

        User user = userRepository.findByUsernameAndDeletedFalse(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));

        return userMapper.toDto(user);
    }

    @Override
    public List<UserDto> getAllUsers() {
        log.debug("Getting all users");

        List<User> users = userRepository.findAll();
        return userMapper.toDtoList(users);
    }

    @Override
    public List<UserDto> getUsersByRole(UserRole role) {
        log.debug("Getting users by role: {}", role);

        List<User> users = userRepository.findByRoleAndDeletedFalse(role);
        return userMapper.toDtoList(users);
    }

    @Override
    public List<UserDto> getEnabledUsers() {
        log.debug("Getting enabled users");

        List<User> users = userRepository.findByEnabledTrueAndDeletedFalse();
        return userMapper.toDtoList(users);
    }

    @Override
    @Transactional
    public void enableUser(Long id) {
        log.info("Enabling user ID: {}", id);

        User user = findUserById(id);
        user.setEnabled(true);
        userRepository.save(user);

        log.info("User enabled: {}", user.getUsername());
    }

    @Override
    @Transactional
    public void disableUser(Long id) {
        log.info("Disabling user ID: {}", id);

        User user = findUserById(id);
        user.setEnabled(false);
        userRepository.save(user);

        log.info("User disabled: {}", user.getUsername());
    }

    @Override
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsernameAndDeletedFalse(username);
    }

    @Override
    @Transactional
    public void linkToConcierge(Long userId, Long conciergeId) {
        log.info("Linking user {} to concierge {}", userId, conciergeId);

        User user = findUserById(userId);
        Concierge concierge = conciergeRepository.findById(conciergeId)
                .orElseThrow(() -> new ConciergeNotFoundException(conciergeId));

        user.setConcierge(concierge);
        userRepository.save(user);

        log.info("User linked to concierge");
    }

    @Override
    @Transactional
    public void unlinkFromConcierge(Long userId) {
        log.info("Unlinking user {} from concierge", userId);

        User user = findUserById(userId);
        user.setConcierge(null);
        userRepository.save(user);

        log.info("User unlinked from concierge");
    }

    //helper method to find user by id
    private User findUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
    }
}
