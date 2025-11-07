package com.vbforge.concierge.dto;

import com.vbforge.concierge.enums.ShiftType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * DTO for ShiftAssignment entity
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ShiftAssignmentDto {

    private Long id;

    @NotNull(message = "Shift date is required")
    private LocalDate shiftDate;

    @NotNull(message = "Concierge is required")
    private Long conciergeId;

    // For display purposes
    private String conciergeName;
    private String conciergeColor;

    @NotNull(message = "Shift type is required")
    @Builder.Default
    private ShiftType shiftType = ShiftType.FULL_DAY;

    @Size(max = 500, message = "Notes cannot exceed 500 characters")
    private String notes;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}