package com.vbforge.concierge.util;

import com.vbforge.concierge.dto.ConciergeDto;
import com.vbforge.concierge.dto.MonthHistoryDto;
import com.vbforge.concierge.dto.ShiftAssignmentDto;
import com.vbforge.concierge.entity.Concierge;
import com.vbforge.concierge.entity.MonthHistory;
import com.vbforge.concierge.entity.ShiftAssignment;
import com.vbforge.concierge.enums.ColorType;
import com.vbforge.concierge.enums.ShiftType;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Test data builder utility for creating test objects
 * Provides fluent API for building entities and DTOs in tests
 */
public class TestDataBuilder {

    // ============================================================
    // CONCIERGE BUILDERS
    // ============================================================

    public static Concierge.ConciergeBuilder defaultConcierge() {
        return Concierge.builder()
                .name("Test Concierge")
                .color(ColorType.BLUE)
                .active(true);
    }

    public static Concierge createAlice() {
        return defaultConcierge()
                .name("Alice")
                .color(ColorType.BLUE)
                .build();
    }

    public static Concierge createBob() {
        return defaultConcierge()
                .name("Bob")
                .color(ColorType.PURPLE)
                .build();
    }

    public static Concierge createCarol() {
        return defaultConcierge()
                .name("Carol")
                .color(ColorType.GREEN)
                .build();
    }

    public static ConciergeDto.ConciergeDtoBuilder defaultConciergeDto() {
        return ConciergeDto.builder()
                .name("Test Concierge")
                .color(ColorType.BLUE)
                .active(true);
    }

    // ============================================================
    // SHIFT ASSIGNMENT BUILDERS
    // ============================================================

    public static ShiftAssignment.ShiftAssignmentBuilder defaultShiftAssignment() {
        return ShiftAssignment.builder()
                .shiftDate(LocalDate.now())
                .shiftType(ShiftType.FULL_DAY);
    }

    public static ShiftAssignment createShiftForDate(LocalDate date, Concierge concierge) {
        return defaultShiftAssignment()
                .shiftDate(date)
                .concierge(concierge)
                .build();
    }

    public static ShiftAssignment createShiftForToday(Concierge concierge) {
        return createShiftForDate(LocalDate.now(), concierge);
    }

    public static ShiftAssignmentDto.ShiftAssignmentDtoBuilder defaultShiftAssignmentDto() {
        return ShiftAssignmentDto.builder()
                .shiftDate(LocalDate.now())
                .shiftType(ShiftType.FULL_DAY)
                .conciergeId(1L)
                .conciergeName("Test Concierge")
                .conciergeColor(ColorType.BLUE.name());
    }

    // ============================================================
    // MONTH HISTORY BUILDERS
    // ============================================================

    public static MonthHistory.MonthHistoryBuilder defaultMonthHistory() {
        return MonthHistory.builder()
                .year(2025)
                .month(11)
                .snapshotJson("{\"shifts\":[]}")
                .snapshotDate(LocalDateTime.now())
                .totalShifts(0)
                .description("Test snapshot");
    }

    public static MonthHistory createHistoryForMonth(int year, int month) {
        return defaultMonthHistory()
                .year(year)
                .month(month)
                .build();
    }

    public static MonthHistoryDto.MonthHistoryDtoBuilder defaultMonthHistoryDto() {
        return MonthHistoryDto.builder()
                .year(2025)
                .month(11)
                .snapshotJson("{\"shifts\":[]}")
                .snapshotDate(LocalDateTime.now())
                .totalShifts(0)
                .description("Test snapshot");
    }

    // ============================================================
    // BULK DATA CREATION
    // ============================================================

    /**
     * Create multiple shifts for a month
     */
    public static ShiftAssignment[] createShiftsForMonth(int year, int month, Concierge... concierges) {
        LocalDate firstDay = LocalDate.of(year, month, 1);
        LocalDate lastDay = firstDay.plusMonths(1).minusDays(1);

        int totalDays = lastDay.getDayOfMonth();
        ShiftAssignment[] shifts = new ShiftAssignment[totalDays];

        for (int day = 1; day <= totalDays; day++) {
            LocalDate date = LocalDate.of(year, month, day);
            Concierge concierge = concierges[(day - 1) % concierges.length];
            shifts[day - 1] = createShiftForDate(date, concierge);
        }

        return shifts;
    }

    /**
     * Create sample concierges
     */
    public static Concierge[] createSampleConcierges() {
        return new Concierge[]{
                createAlice(),
                createBob(),
                createCarol()
        };
    }

}
