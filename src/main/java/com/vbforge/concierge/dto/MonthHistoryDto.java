package com.vbforge.concierge.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO for MonthHistory entity
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MonthHistoryDto {

    private Long id;

    @NotNull(message = "Year is required")
    @Min(value = 2020, message = "Year must be 2020 or later")
    @Max(value = 2100, message = "Year must be 2100 or earlier")
    private Integer year;

    @NotNull(message = "Month is required")
    @Min(value = 1, message = "Month must be between 1 and 12")
    @Max(value = 12, message = "Month must be between 1 and 12")
    private Integer month;

    @NotNull(message = "Snapshot JSON is required")
    private String snapshotJson;

    @Builder.Default
    private LocalDateTime snapshotDate = LocalDateTime.now();

    private String description;
    private Integer totalShifts;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Display helper
    public String getMonthYearDisplay() {
        return String.format("%d-%02d", year, month);
    }
}