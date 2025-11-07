package com.vbforge.concierge.service;

import com.vbforge.concierge.dto.ShiftAssignmentDto;
import com.vbforge.concierge.enums.ShiftType;
import com.vbforge.concierge.exception.*;
import com.vbforge.concierge.repository.ConciergeRepository;
import com.vbforge.concierge.repository.ShiftAssignmentRepository;
import com.vbforge.concierge.service.impl.ValidationServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for ValidationService
 */
@ExtendWith(MockitoExtension.class)
public class ValidationServiceTest {

    @Mock
    private ConciergeRepository conciergeRepository;

    @Mock
    private ShiftAssignmentRepository shiftRepository;

    @InjectMocks
    private ValidationServiceImpl validationService;

    @Test
    void testValidateShiftAssignment() {
        // Given
        ShiftAssignmentDto dto = ShiftAssignmentDto.builder()
                .shiftDate(LocalDate.now().plusDays(1))
                .conciergeId(1L)
                .shiftType(ShiftType.FULL_DAY)
                .build();

        // When/Then
        assertThatCode(() -> validationService.validateShiftAssignment(dto))
                .doesNotThrowAnyException();
    }

    @Test
    void testShouldThrowExceptionWhenShiftAssignmentIsNull() {
        // When/Then
        assertThatThrownBy(() -> validationService.validateShiftAssignment(null))
                .isInstanceOf(InvalidInputException.class)
                .hasMessageContaining("cannot be null");
    }

    @Test
    void testShouldThrowExceptionWhenShiftDateIsNull() {
        // Given
        ShiftAssignmentDto dto = ShiftAssignmentDto.builder()
                .conciergeId(1L)
                .shiftType(ShiftType.FULL_DAY)
                .build();

        // When/Then
        assertThatThrownBy(() -> validationService.validateShiftAssignment(dto))
                .isInstanceOf(InvalidInputException.class)
                .hasMessageContaining("date is required");
    }

    @Test
    void testShouldThrowExceptionWhenConciergeIdIsNull() {
        // Given
        ShiftAssignmentDto dto = ShiftAssignmentDto.builder()
                .shiftDate(LocalDate.now())
                .shiftType(ShiftType.FULL_DAY)
                .build();

        // When/Then
        assertThatThrownBy(() -> validationService.validateShiftAssignment(dto))
                .isInstanceOf(InvalidInputException.class)
                .hasMessageContaining("Concierge ID is required");
    }

    @Test
    void testValidateDate() {
        // Given
        LocalDate validDate = LocalDate.now();

        // When/Then
        assertThatCode(() -> validationService.validateDate(validDate))
                .doesNotThrowAnyException();
    }

    @Test
    void testShouldThrowExceptionWhenDateIsNull() {
        // When/Then
        assertThatThrownBy(() -> validationService.validateDate(null))
                .isInstanceOf(InvalidDateException.class)
                .hasMessageContaining("cannot be null");
    }

    @Test
    void testShouldThrowExceptionWhenDateIsTooFarInPast() {
        // Given
        LocalDate tooOld = LocalDate.now().minusYears(11);

        // When/Then
        assertThatThrownBy(() -> validationService.validateDate(tooOld))
                .isInstanceOf(InvalidDateException.class)
                .hasMessageContaining("10 years in the past");
    }

    @Test
    void testShouldThrowExceptionWhenDateIsTooFarInFuture() {
        // Given
        LocalDate tooFuture = LocalDate.now().plusYears(11);

        // When/Then
        assertThatThrownBy(() -> validationService.validateDate(tooFuture))
                .isInstanceOf(InvalidDateException.class)
                .hasMessageContaining("10 years in the future");
    }

    @Test
    void testValidateDateNotInPast() {
        // Given
        LocalDate futureDate = LocalDate.now().plusDays(1);

        // When/Then
        assertThatCode(() -> validationService.validateDateNotInPast(futureDate))
                .doesNotThrowAnyException();
    }

    @Test
    void testShouldThrowExceptionWhenDateIsInPast() {
        // Given
        LocalDate pastDate = LocalDate.now().minusDays(1);

        // When/Then
        assertThatThrownBy(() -> validationService.validateDateNotInPast(pastDate))
                .isInstanceOf(InvalidDateException.class)
                .hasMessageContaining("past dates");
    }

    @Test
    void testValidateYearMonth() {
        // When/Then
        assertThatCode(() -> validationService.validateYearMonth(2025, 11))
                .doesNotThrowAnyException();
    }

    @Test
    void testShouldThrowExceptionWhenYearIsInvalid() {
        // When/Then
        assertThatThrownBy(() -> validationService.validateYearMonth(2019, 1))
                .isInstanceOf(InvalidInputException.class)
                .hasMessageContaining("between 2020 and 2100");
    }

    @Test
    void testShouldThrowExceptionWhenMonthIsInvalid() {
        // When/Then
        assertThatThrownBy(() -> validationService.validateYearMonth(2025, 13))
                .isInstanceOf(InvalidInputException.class)
                .hasMessageContaining("between 1 and 12");
    }

    @Test
    void testValidateConciergeExists() {
        // Given
        when(conciergeRepository.existsById(1L)).thenReturn(true);

        // When/Then
        assertThatCode(() -> validationService.validateConciergeExists(1L))
                .doesNotThrowAnyException();
    }

    @Test
    void testShouldThrowExceptionWhenConciergeDoesNotExist() {
        // Given
        when(conciergeRepository.existsById(999L)).thenReturn(false);

        // When/Then
        assertThatThrownBy(() -> validationService.validateConciergeExists(999L))
                .isInstanceOf(ConciergeNotFoundException.class);
    }

    @Test
    void testCheckShiftConflict() {
        // Given
        LocalDate date = LocalDate.now();
        when(shiftRepository.existsByShiftDateAndDeletedFalse(date)).thenReturn(false);

        // When/Then
        assertThatCode(() -> validationService.checkShiftConflict(date))
                .doesNotThrowAnyException();
    }

    @Test
    void testShouldThrowExceptionWhenShiftConflictExists() {
        // Given
        LocalDate date = LocalDate.now();
        when(shiftRepository.existsByShiftDateAndDeletedFalse(date)).thenReturn(true);

        // When/Then
        assertThatThrownBy(() -> validationService.checkShiftConflict(date))
                .isInstanceOf(ShiftConflictException.class);
    }

    @Test
    void testValidateConciergeNameUnique() {
        // Given
        when(conciergeRepository.existsByNameIgnoreCase("Alice")).thenReturn(false);

        // When/Then
        assertThatCode(() -> validationService.validateConciergeNameUnique("Alice"))
                .doesNotThrowAnyException();
    }

    @Test
    void testShouldThrowExceptionWhenConciergeNameExists() {
        // Given
        when(conciergeRepository.existsByNameIgnoreCase("Alice")).thenReturn(true);

        // When/Then
        assertThatThrownBy(() -> validationService.validateConciergeNameUnique("Alice"))
                .isInstanceOf(ConciergeDuplicateException.class)
                .hasMessageContaining("Alice");
    }

    @Test
    void testShouldThrowExceptionWhenConciergeNameIsEmpty() {
        // When/Then
        assertThatThrownBy(() -> validationService.validateConciergeNameUnique(""))
                .isInstanceOf(InvalidInputException.class)
                .hasMessageContaining("cannot be empty");
    }

    @Test
    void testValidateConciergeCanBeDeleted() {
        // Given
        when(conciergeRepository.existsById(1L)).thenReturn(true);
        when(conciergeRepository.hasShiftAssignments(1L)).thenReturn(false);

        // When/Then
        assertThatCode(() -> validationService.validateConciergeCanBeDeleted(1L))
                .doesNotThrowAnyException();
    }

    @Test
    void testShouldThrowExceptionWhenConciergeHasShifts() {
        // Given
        when(conciergeRepository.existsById(1L)).thenReturn(true);
        when(conciergeRepository.hasShiftAssignments(1L)).thenReturn(true);

        // When/Then
        assertThatThrownBy(() -> validationService.validateConciergeCanBeDeleted(1L))
                .isInstanceOf(ConciergeInUseException.class)
                .hasMessageContaining("active shift assignments");
    }

}















