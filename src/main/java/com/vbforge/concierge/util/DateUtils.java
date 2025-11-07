package com.vbforge.concierge.util;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Utility class for date operations
 */
public class DateUtils {

    private static final DateTimeFormatter MONTH_YEAR_FORMATTER = DateTimeFormatter.ofPattern("MMMM yyyy");
    private static final DateTimeFormatter SHORT_DATE_FORMATTER = DateTimeFormatter.ofPattern("MMM dd, yyyy");
    private static final DateTimeFormatter ISO_DATE_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE;

    private DateUtils() {
        // Private constructor to prevent instantiation
    }

    /**
     * Get the first day of the month
     */
    public static LocalDate getFirstDayOfMonth(int year, int month) {
        return LocalDate.of(year, month, 1);
    }

    /**
     * Get the last day of the month
     */
    public static LocalDate getLastDayOfMonth(int year, int month) {
        YearMonth yearMonth = YearMonth.of(year, month);
        return yearMonth.atEndOfMonth();
    }

    /**
     * Get total days in month
     */
    public static int getTotalDaysInMonth(int year, int month) {
        YearMonth yearMonth = YearMonth.of(year, month);
        return yearMonth.lengthOfMonth();
    }

    /**
     * Get all dates in a month
     */
    public static List<LocalDate> getDatesInMonth(int year, int month) {
        List<LocalDate> dates = new ArrayList<>();
        LocalDate firstDay = getFirstDayOfMonth(year, month);
        LocalDate lastDay = getLastDayOfMonth(year, month);
        
        LocalDate current = firstDay;
        while (!current.isAfter(lastDay)) {
            dates.add(current);
            current = current.plusDays(1);
        }
        
        return dates;
    }

    /**
     * Get day of week (1=Monday, 7=Sunday)
     */
    public static int getDayOfWeekValue(LocalDate date) {
        return date.getDayOfWeek().getValue();
    }

    /**
     * Check if date is weekend
     */
    public static boolean isWeekend(LocalDate date) {
        DayOfWeek dayOfWeek = date.getDayOfWeek();
        return dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY;
    }

    /**
     * Check if date is today
     */
    public static boolean isToday(LocalDate date) {
        return date.equals(LocalDate.now());
    }

    /**
     * Format date as "Month Year" (e.g., "November 2025")
     */
    public static String formatMonthYear(int year, int month) {
        LocalDate date = LocalDate.of(year, month, 1);
        return date.format(MONTH_YEAR_FORMATTER);
    }

    /**
     * Format date as "MMM dd, yyyy" (e.g., "Nov 06, 2025")
     */
    public static String formatShortDate(LocalDate date) {
        return date.format(SHORT_DATE_FORMATTER);
    }

    /**
     * Format date as ISO (e.g., "2025-11-06")
     */
    public static String formatIsoDate(LocalDate date) {
        return date.format(ISO_DATE_FORMATTER);
    }

    /**
     * Get month name
     */
    public static String getMonthName(int month) {
        LocalDate date = LocalDate.of(2025, month, 1);
        return date.getMonth().getDisplayName(TextStyle.FULL, Locale.ENGLISH);
    }

    /**
     * Get short month name (3 letters)
     */
    public static String getShortMonthName(int month) {
        LocalDate date = LocalDate.of(2025, month, 1);
        return date.getMonth().getDisplayName(TextStyle.SHORT, Locale.ENGLISH);
    }

    /**
     * Get day of week short name (e.g., "MON", "TUE")
     */
    public static String getDayOfWeekShort(LocalDate date) {
        return date.getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.ENGLISH).toUpperCase();
    }

    /**
     * Get current year
     */
    public static int getCurrentYear() {
        return LocalDate.now().getYear();
    }

    /**
     * Get current month
     */
    public static int getCurrentMonth() {
        return LocalDate.now().getMonthValue();
    }

    /**
     * Check if date is in the past
     */
    public static boolean isPast(LocalDate date) {
        return date.isBefore(LocalDate.now());
    }

    /**
     * Check if date is in the future
     */
    public static boolean isFuture(LocalDate date) {
        return date.isAfter(LocalDate.now());
    }

    /**
     * Get dates for calendar grid (including previous/next month days)
     */
    public static List<LocalDate> getCalendarGridDates(int year, int month) {
        List<LocalDate> dates = new ArrayList<>();
        
        LocalDate firstDay = getFirstDayOfMonth(year, month);
        LocalDate lastDay = getLastDayOfMonth(year, month);
        
        // Add days from previous month to start on Monday
        int startDayOfWeek = firstDay.getDayOfWeek().getValue(); // 1=Monday, 7=Sunday
        LocalDate current = firstDay.minusDays(startDayOfWeek - 1);
        
        // Generate 6 weeks (42 days) for consistent calendar grid
        for (int i = 0; i < 42; i++) {
            dates.add(current);
            current = current.plusDays(1);
        }
        
        return dates;
    }

    /**
     * Parse ISO date string
     */
    public static LocalDate parseIsoDate(String dateStr) {
        return LocalDate.parse(dateStr, ISO_DATE_FORMATTER);
    }

    /**
     * Get next month
     */
    public static int[] getNextMonth(int year, int month) {
        if (month == 12) {
            return new int[]{year + 1, 1};
        }
        return new int[]{year, month + 1};
    }

    /**
     * Get previous month
     */
    public static int[] getPreviousMonth(int year, int month) {
        if (month == 1) {
            return new int[]{year - 1, 12};
        }
        return new int[]{year, month - 1};
    }
}