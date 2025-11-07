package com.vbforge.concierge.exception;

/**
 * Thrown when a month history record is not found
 */
public class MonthHistoryNotFoundException extends ResourceNotFoundException {

    public MonthHistoryNotFoundException(Long id) {
        super("MonthHistory", "id", id);
    }

    public MonthHistoryNotFoundException(int year, int month) {
        super(String.format("MonthHistory not found for year: %d, month: %d", year, month));
    }
}