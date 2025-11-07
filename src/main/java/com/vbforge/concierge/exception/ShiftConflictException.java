package com.vbforge.concierge.exception;

/**
 * Thrown when shift assignment validation fails
 */
public class ShiftConflictException extends ConciergeSchedulerException {

    public ShiftConflictException(String date) {
        super(String.format("Shift already assigned for date: %s", date));
    }

}