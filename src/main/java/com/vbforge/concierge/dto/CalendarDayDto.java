package com.vbforge.concierge.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.DayOfWeek;
import java.time.LocalDate;

/**
 * DTO for calendar day cell
 * Used in Thymeleaf template for rendering calendar
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CalendarDayDto {

    private LocalDate date;
    private Integer dayOfMonth;
    private DayOfWeek dayOfWeek;
    private String dayOfWeekShort; // MON, TUE, etc.

    private boolean isToday;
    private boolean isWeekend;
    private boolean isCurrentMonth;
    private boolean isAssigned;

    private ShiftAssignmentDto shiftAssignment;

    // Display helpers
    public String getCssClass() {
        StringBuilder css = new StringBuilder("calendar-day");
        if (isToday) css.append(" today");
        if (isWeekend) css.append(" weekend");
        if (!isCurrentMonth) css.append(" other-month");
        if (isAssigned) css.append(" assigned");
        return css.toString();
    }
}