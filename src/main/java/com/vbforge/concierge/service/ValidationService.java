package com.vbforge.concierge.service;

import com.vbforge.concierge.dto.ShiftAssignmentDto;
import com.vbforge.concierge.exception.*;

import java.time.LocalDate;

/**
 * Service interface for Validation operations
 */
public interface ValidationService {

    /**
     * Validate shift assignment
     * @param shiftAssignmentDto Shift assignment to validate
     * @throws InvalidInputException if validation fails
     */
    void validateShiftAssignment(ShiftAssignmentDto shiftAssignmentDto);

    /**
     * Validate date
     * @param date Date to validate
     * @throws InvalidDateException if date is invalid
     */
    void validateDate(LocalDate date);

    /**
     * Validate that date is not in the past
     * @param date Date to validate
     * @throws InvalidDateException if date is in the past
     */
    void validateDateNotInPast(LocalDate date);

    /**
     * Validate year and month
     * @param year Year
     * @param monthValue Month (1-12)
     * @throws InvalidInputException if invalid
     */
    void validateYearMonth(int year, int monthValue);

    /**
     * Validate concierge exists
     * @param conciergeId Concierge ID
     * @throws ConciergeNotFoundException if not found
     */
    void validateConciergeExists(Long conciergeId);

    /**
     * Check for shift conflicts
     * @param date Shift date
     * @throws ShiftConflictException if shift already exists
     */
    void checkShiftConflict(LocalDate date);

    /**
     * Validate concierge name is unique
     * @param name Concierge name
     * @throws ConciergeDuplicateException if name exists
     */
    void validateConciergeNameUnique(String name);

    /**
     * Validate concierge can be deleted
     * @param conciergeId Concierge ID
     * @throws ConciergeInUseException if has active shifts
     */
    void validateConciergeCanBeDeleted(Long conciergeId);

}
