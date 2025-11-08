package com.vbforge.concierge.dto;

import com.vbforge.concierge.enums.UserRole;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO for User entity
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDto {

    private Long id;

    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    private String username;

    @NotBlank(message = "Password is required", groups = CreateValidation.class)
    @Size(min = 6, max = 100, message = "Password must be at least 6 characters")
    private String password;

    // For password confirmation in forms
    private String confirmPassword;

    @NotNull(message = "Role is required")
    @Builder.Default
    private UserRole role = UserRole.CONCIERGE;

    @Builder.Default
    private boolean enabled = true;

    // Optional link to concierge
    private Long conciergeId;
    private String conciergeName;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    /**
     * Validation group for create operations
     */
    public interface CreateValidation {}

    /**
     * Validation group for update operations
     */
    public interface UpdateValidation {}

    /**
     * Check if passwords match (for form validation)
     */
    public boolean passwordsMatch() {
        return password != null && password.equals(confirmPassword);
    }

}
