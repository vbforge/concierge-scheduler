package com.vbforge.concierge.service.impl;

import com.vbforge.concierge.dto.CalendarDayDto;
import com.vbforge.concierge.dto.ConciergeDto;
import com.vbforge.concierge.dto.MonthScheduleDto;
import com.vbforge.concierge.dto.ShiftAssignmentDto;
import com.vbforge.concierge.service.ConciergeService;
import com.vbforge.concierge.service.MonthScheduleService;
import com.vbforge.concierge.service.ShiftSchedulingService;
import com.vbforge.concierge.service.ValidationService;
import com.vbforge.concierge.util.DateUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Implementation of MonthScheduleService
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MonthScheduleServiceImpl implements MonthScheduleService {

    private final ShiftSchedulingService shiftSchedulingService;
    private final ConciergeService conciergeService;
    private final ValidationService validationService;

    @Override
    public MonthScheduleDto getMonthSchedule(int year, int monthValue) {
        log.debug("Getting month schedule for {}-{}", year, monthValue);

        validationService.validateYearMonth(year, monthValue);

        // Get all shifts for the month
        List<ShiftAssignmentDto> shifts = getShiftsForMonth(year, monthValue);

        // Create map of date to shift
        Map<LocalDate, ShiftAssignmentDto> dailyAssignments = shifts.stream()
                .collect(Collectors.toMap(ShiftAssignmentDto::getShiftDate, s -> s));

        // Get all concierges
        List<ConciergeDto> concierges = conciergeService.getAllActiveConcierges();

        // Calculate statistics
        int totalDays = DateUtils.getTotalDaysInMonth(year, monthValue);
        int assignedDays = shifts.size();
        int unassignedDays = totalDays - assignedDays;

        LocalDate firstDay = DateUtils.getFirstDayOfMonth(year, monthValue);
        LocalDate lastDay = DateUtils.getLastDayOfMonth(year, monthValue);

        return MonthScheduleDto.builder()
                .year(year)
                .month(monthValue)
                .monthName(DateUtils.getMonthName(monthValue))
                .dailyAssignments(dailyAssignments)
                .concierges(concierges)
                .totalDays(totalDays)
                .assignedDays(assignedDays)
                .unassignedDays(unassignedDays)
                .firstDayOfMonth(firstDay)
                .lastDayOfMonth(lastDay)
                .startingDayOfWeek(DateUtils.getDayOfWeekValue(firstDay))
                .build();
    }

    @Override
    public List<ShiftAssignmentDto> getShiftsForMonth(int year, int monthValue) {
        log.debug("Getting shifts for month {}-{}", year, monthValue);

        validationService.validateYearMonth(year, monthValue);

        LocalDate startDate = DateUtils.getFirstDayOfMonth(year, monthValue);
        LocalDate endDate = DateUtils.getLastDayOfMonth(year, monthValue);

        return shiftSchedulingService.getShiftsInDateRange(startDate, endDate);
    }

    @Override
    public List<CalendarDayDto> getCalendarDays(int year, int monthValue) {
        log.debug("Getting calendar days for {}-{}", year, monthValue);

        validationService.validateYearMonth(year, monthValue);

        // Get calendar grid dates (42 days including prev/next month)
        List<LocalDate> gridDates = DateUtils.getCalendarGridDates(year, monthValue);

        // Get shifts for all dates in the grid
        LocalDate startDate = gridDates.get(0);
        LocalDate endDate = gridDates.get(gridDates.size() - 1);
        List<ShiftAssignmentDto> shifts = shiftSchedulingService.getShiftsInDateRange(startDate, endDate);

        // Create map of date to shift
        Map<LocalDate, ShiftAssignmentDto> shiftMap = shifts.stream()
                .collect(Collectors.toMap(ShiftAssignmentDto::getShiftDate, s -> s));

        // Create calendar day DTOs
        LocalDate today = LocalDate.now();
        LocalDate firstOfMonth = DateUtils.getFirstDayOfMonth(year, monthValue);
        LocalDate lastOfMonth = DateUtils.getLastDayOfMonth(year, monthValue);

        return gridDates.stream()
                .map(date -> {
                    boolean isCurrentMonth = !date.isBefore(firstOfMonth) && !date.isAfter(lastOfMonth);
                    ShiftAssignmentDto shift = shiftMap.get(date);

                    return CalendarDayDto.builder()
                            .date(date)
                            .dayOfMonth(date.getDayOfMonth())
                            .dayOfWeek(date.getDayOfWeek())
                            .dayOfWeekShort(DateUtils.getDayOfWeekShort(date))
                            .isToday(date.equals(today))
                            .isWeekend(DateUtils.isWeekend(date))
                            .isCurrentMonth(isCurrentMonth)
                            .isAssigned(shift != null)
                            .shiftAssignment(shift)
                            .build();
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<LocalDate> getUnassignedDates(int year, int monthValue) {
        log.debug("Getting unassigned dates for {}-{}", year, monthValue);

        validationService.validateYearMonth(year, monthValue);

        List<LocalDate> allDates = DateUtils.getDatesInMonth(year, monthValue);
        List<ShiftAssignmentDto> shifts = getShiftsForMonth(year, monthValue);

        Set<LocalDate> assignedDates = shifts.stream()
                .map(ShiftAssignmentDto::getShiftDate)
                .collect(Collectors.toSet());

        return allDates.stream()
                .filter(date -> !assignedDates.contains(date))
                .collect(Collectors.toList());
    }

    @Override
    public Map<String, Integer> getShiftCountByConcierge(int year, int monthValue) {
        log.debug("Getting shift count by concierge for {}-{}", year, monthValue);

        validationService.validateYearMonth(year, monthValue);

        List<ShiftAssignmentDto> shifts = getShiftsForMonth(year, monthValue);

        return shifts.stream()
                .collect(Collectors.groupingBy(
                        ShiftAssignmentDto::getConciergeName,
                        Collectors.collectingAndThen(Collectors.counting(), Long::intValue)
                ));
    }

    @Override
    public int countAssignedDays(int year, int monthValue) {
        log.debug("Counting assigned days for {}-{}", year, monthValue);

        validationService.validateYearMonth(year, monthValue);

        return getShiftsForMonth(year, monthValue).size();
    }

    @Override
    public int countUnassignedDays(int year, int monthValue) {
        log.debug("Counting unassigned days for {}-{}", year, monthValue);

        validationService.validateYearMonth(year, monthValue);

        int totalDays = DateUtils.getTotalDaysInMonth(year, monthValue);
        int assignedDays = countAssignedDays(year, monthValue);

        return totalDays - assignedDays;
    }

    @Override
    public boolean isMonthFullyAssigned(int year, int monthValue) {
        log.debug("Checking if month {}-{} is fully assigned", year, monthValue);

        validationService.validateYearMonth(year, monthValue);

        return countUnassignedDays(year, monthValue) == 0;
    }
}
