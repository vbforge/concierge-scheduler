package com.vbforge.concierge.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * DTO for displaying a complete month schedule
 * Used in calendar view
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MonthScheduleDto {

    private Integer year;
    private Integer month;
    private String monthName;
    
    /**
     * Map of date to shift assignment
     * Key: LocalDate, Value: ShiftAssignmentDto
     */
    private Map<LocalDate, ShiftAssignmentDto> dailyAssignments;

    /**
     * List of all concierges
     */
    private List<ConciergeDto> concierges;

    /**
     * Total days in the month
     */
    private Integer totalDays;

    /**
     * Number of assigned days
     */
    private Integer assignedDays;

    /**
     * Number of unassigned days
     */
    private Integer unassignedDays;

    /**
     * First day of the month (for calendar grid)
     */
    private LocalDate firstDayOfMonth;

    /**
     * Last day of the month
     */
    private LocalDate lastDayOfMonth;

    /**
     * Starting day of week (1=Monday, 7=Sunday)
     */
    private Integer startingDayOfWeek;
}