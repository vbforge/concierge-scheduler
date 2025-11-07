package com.vbforge.concierge.service;

import com.vbforge.concierge.dto.ShiftAssignmentDto;
import com.vbforge.concierge.exception.ConciergeNotFoundException;
import com.vbforge.concierge.exception.ShiftAssignmentNotFoundException;
import com.vbforge.concierge.exception.ShiftConflictException;

import java.time.LocalDate;
import java.util.List;

/** service interface for shift scheduling operation */
public interface ShiftSchedulingService {

    /**
     * Assign shift to a concierge for a specific date
     * @param shiftAssignmentDto Shift assignment data
     * @return Created shift assignment
     * @throws ShiftConflictException if shift already exists for date
     * @throws ConciergeNotFoundException if concierge not found
     */
    ShiftAssignmentDto assignShift(ShiftAssignmentDto shiftAssignmentDto);

    /**
     * Update existing shift assignment
     * @param id Shift assignment ID
     * @param shiftAssignmentDto Updated data
     * @return Updated shift assignment
     * @throws ShiftAssignmentNotFoundException if shift not found
     */
    ShiftAssignmentDto updateShift(Long id, ShiftAssignmentDto shiftAssignmentDto);

    /**
     * Remove shift assignment
     * @param id Shift assignment ID
     * @throws ShiftAssignmentNotFoundException if shift not found
     */
    void removeShift(Long id);

    /**
     * Remove shift by date
     * @param date Shift date
     * @throws ShiftAssignmentNotFoundException if shift not found
     */
    void removeShiftByDate(LocalDate date);

    /**
     * Get shift by ID
     * @param id Shift assignment ID
     * @return Shift assignment DTO
     * @throws ShiftAssignmentNotFoundException if not found
     */
    ShiftAssignmentDto getShiftById(Long id);

    /**
     * Get shift by date
     * @param date Shift date
     * @return Shift assignment DTO or null if not assigned
     */
    ShiftAssignmentDto getShiftByDate(LocalDate date);

    /**
     * Get all shifts for a concierge
     * @param conciergeId Concierge ID
     * @return List of shift assignments
     */
    List<ShiftAssignmentDto> getShiftsByConcierge(Long conciergeId);

    /**
     * Get shifts in date range
     * @param startDate Start date (inclusive)
     * @param endDate End date (inclusive)
     * @return List of shift assignments
     */
    List<ShiftAssignmentDto> getShiftsInDateRange(LocalDate startDate, LocalDate endDate);

    /**
     * Check if shift exists for date
     * @param date Shift date
     * @return true if shift exists
     */
    boolean isShiftAssigned(LocalDate date);

    /**
     * Bulk assign shifts
     * @param shiftAssignments List of shift assignments
     * @return List of created shift assignments
     */
    List<ShiftAssignmentDto> bulkAssignShifts(List<ShiftAssignmentDto> shiftAssignments);

    /**
     * Delete all shifts for a concierge
     * @param conciergeId Concierge ID
     */
    void deleteAllShiftsForConcierge(Long conciergeId);

}
