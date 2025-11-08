package com.vbforge.concierge.service;

import com.vbforge.concierge.entity.User;
import com.vbforge.concierge.exception.InvalidInputException;
import com.vbforge.concierge.repository.UserRepository;
import com.vbforge.concierge.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class UserServiceLenientTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    private User adminUser;

    @BeforeEach
    void setup() {
        adminUser = new User();
        adminUser.setId(1L);
        adminUser.setUsername("admin");
        adminUser.setPassword("encoded123");

        // ⚠️ If you stub common behavior used by most, but not all tests:
        // Mockito may throw UnnecessaryStubbingException in tests that don't reach this call.
        // So, we can mark it lenient() to avoid warnings in those cases.
        lenient().when(userRepository.findById(1L))
                .thenReturn(Optional.of(adminUser));

        lenient().when(passwordEncoder.encode(anyString()))
                .thenAnswer(inv -> "encoded_" + inv.getArgument(0));
    }

    @Test
    void testChangePassword_success() {
        userService.changePassword(1L, "newStrongPassword");
        verify(userRepository).save(any(User.class));
    }

    @Test
    void testShouldThrowExceptionWhenChangingToShortPassword() {
        // Even though we don’t use findById() in this test,
        // lenient() prevents Mockito from complaining about that unused stub.
        assertThatThrownBy(() -> userService.changePassword(1L, "12345"))
                .isInstanceOf(InvalidInputException.class)
                .hasMessageContaining("at least 6 characters");
    }

}
