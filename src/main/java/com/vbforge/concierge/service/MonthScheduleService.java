package com.vbforge.concierge.service;

import com.vbforge.concierge.dto.CalendarDayDto;
import com.vbforge.concierge.dto.MonthScheduleDto;
import com.vbforge.concierge.dto.ShiftAssignmentDto;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * Service interface for Month Schedule operations
 */
public interface MonthScheduleService {

    /**
     * Get complete month schedule
     * @param year Year
     * @param monthValue Month (1-12)
     * @return Month schedule DTO with all data
     */
    MonthScheduleDto getMonthSchedule(int year, int monthValue);

    /**
     * Get shifts for specific month
     * @param year Year
     * @param monthValue Month (1-12)
     * @return List of shift assignments
     */
    List<ShiftAssignmentDto> getShiftsForMonth(int year, int monthValue);

    /**
     * Get calendar days for month (including prev/next month days)
     * @param year Year
     * @param monthValue Month (1-12)
     * @return List of calendar day DTOs
     */
    List<CalendarDayDto> getCalendarDays(int year, int monthValue);

    /**
     * Get unassigned dates in month
     * @param year Year
     * @param monthValue Month (1-12)
     * @return List of unassigned dates
     */
    List<LocalDate> getUnassignedDates(int year, int monthValue);

    /**
     * Get shift count by concierge for month
     * @param year Year
     * @param monthValue Month (1-12)
     * @return Map of concierge name to shift count
     */
    Map<String, Integer> getShiftCountByConcierge(int year, int monthValue);

    /**
     * Count assigned days in month
     * @param year Year
     * @param monthValue Month (1-12)
     * @return Number of assigned days
     */
    int countAssignedDays(int year, int monthValue);

    /**
     * Count unassigned days in month
     * @param year Year
     * @param monthValue Month (1-12)
     * @return Number of unassigned days
     */
    int countUnassignedDays(int year, int monthValue);

    /**
     * Check if month is fully assigned
     * @param year Year
     * @param monthValue Month (1-12)
     * @return true if all days are assigned
     */
    boolean isMonthFullyAssigned(int year, int monthValue);

}
