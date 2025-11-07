package com.vbforge.concierge.exception;

/**
 * Thrown when a shift assignment is not found
 */
public class ShiftAssignmentNotFoundException extends ResourceNotFoundException {

    public ShiftAssignmentNotFoundException(Long id) {
        super("ShiftAssignment", "id", id);
    }

    public ShiftAssignmentNotFoundException(String date) {
        super("ShiftAssignment", "date", date);
    }
}