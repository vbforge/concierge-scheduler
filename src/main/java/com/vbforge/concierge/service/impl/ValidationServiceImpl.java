package com.vbforge.concierge.service.impl;

import com.vbforge.concierge.dto.ShiftAssignmentDto;
import com.vbforge.concierge.exception.*;
import com.vbforge.concierge.repository.ConciergeRepository;
import com.vbforge.concierge.repository.ShiftAssignmentRepository;
import com.vbforge.concierge.service.ValidationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

/**
 * Implementation of ValidationService
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ValidationServiceImpl implements ValidationService {

    private final ConciergeRepository conciergeRepository;
    private final ShiftAssignmentRepository shiftAssignmentRepository;

    @Override
    public void validateShiftAssignment(ShiftAssignmentDto shiftAssignmentDto) {
        log.debug("Validating shift assignment: {}", shiftAssignmentDto);

        if (shiftAssignmentDto == null) {
            throw new InvalidInputException("Shift assignment cannot be null");
        }

        if (shiftAssignmentDto.getShiftDate() == null) {
            throw new InvalidInputException("Shift date is required");
        }

        if (shiftAssignmentDto.getConciergeId() == null) {
            throw new InvalidInputException("Concierge ID is required");
        }

        if (shiftAssignmentDto.getShiftType() == null) {
            throw new InvalidInputException("Shift type is required");
        }

        // Validate date
        validateDate(shiftAssignmentDto.getShiftDate());
    }

    @Override
    public void validateDate(LocalDate date) {
        log.debug("Validating date: {}", date);

        if (date == null) {
            throw new InvalidDateException("Date cannot be null");
        }

        // Check if date is not too far in the past or future
        LocalDate now = LocalDate.now();
        LocalDate minDate = now.minusYears(10);
        LocalDate maxDate = now.plusYears(10);

        if (date.isBefore(minDate)) {
            throw new InvalidDateException("Date cannot be more than 10 years in the past");
        }

        if (date.isAfter(maxDate)) {
            throw new InvalidDateException("Date cannot be more than 10 years in the future");
        }
    }

    @Override
    public void validateDateNotInPast(LocalDate date) {
        log.debug("Validating date is not in past: {}", date);
        validateDate(date);
        if(date.isBefore(LocalDate.now())){
            throw new InvalidDateException("Cannot assign shifts to past dates");
        }
    }

    @Override
    public void validateYearMonth(int year, int monthValue) {
        log.debug("Validating year: {}, month: {}", year, monthValue);

        if (year < 2020 || year > 2100) {
            throw new InvalidInputException("Year must be between 2020 and 2100");
        }

        if (monthValue < 1 || monthValue > 12) {
            throw new InvalidInputException("Month must be between 1 and 12");
        }
    }

    @Override
    public void validateConciergeExists(Long conciergeId) {
        log.debug("Validating concierge exists: {}", conciergeId);

        if (conciergeId == null) {
            throw new InvalidInputException("Concierge ID cannot be null");
        }

        boolean exists = conciergeRepository.existsById(conciergeId);
        if (!exists) {
            throw new ConciergeNotFoundException(conciergeId);
        }
    }

    @Override
    public void checkShiftConflict(LocalDate date) {
        log.debug("Checking shift conflict for date: {}", date);

        boolean exists = shiftAssignmentRepository.existsByShiftDateAndDeletedFalse(date);
        if (exists) {
            throw new ShiftConflictException(date.toString());
        }
    }

    @Override
    public void validateConciergeNameUnique(String name) {
        log.debug("Validating concierge name is unique: {}", name);

        if (name == null || name.trim().isEmpty()) {
            throw new InvalidInputException("Concierge name cannot be empty");
        }

        boolean exists = conciergeRepository.existsByNameIgnoreCase(name);
        if (exists) {
            throw new ConciergeDuplicateException(name);
        }
    }

    @Override
    public void validateConciergeCanBeDeleted(Long conciergeId) {
        log.debug("Validating concierge can be deleted: {}", conciergeId);

        validateConciergeExists(conciergeId);

        boolean hasShifts = conciergeRepository.hasShiftAssignments(conciergeId);
        if (hasShifts) {
            throw new ConciergeInUseException(
                    "Concierge ID " + conciergeId + " has active shift assignments"
            );
        }
    }


}
