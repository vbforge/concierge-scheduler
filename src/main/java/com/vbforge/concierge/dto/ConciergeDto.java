package com.vbforge.concierge.dto;

import com.vbforge.concierge.enums.ColorType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO for Concierge entity
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConciergeDto {

    private Long id;

    @NotBlank(message = "Concierge name is required")
    @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
    private String name;

    @NotNull(message = "Color is required")
    private ColorType color;

    @Builder.Default
    private boolean active = true;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // For display purposes
    private Integer currentMonthShiftCount;
    private Integer totalShiftCount;
}