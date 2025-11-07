package com.vbforge.concierge.service;

import com.vbforge.concierge.dto.ShiftAssignmentDto;
import com.vbforge.concierge.entity.Concierge;
import com.vbforge.concierge.entity.ShiftAssignment;
import com.vbforge.concierge.exception.ShiftAssignmentNotFoundException;
import com.vbforge.concierge.exception.ShiftConflictException;
import com.vbforge.concierge.mapper.ShiftAssignmentMapper;
import com.vbforge.concierge.repository.ConciergeRepository;
import com.vbforge.concierge.repository.ShiftAssignmentRepository;
import com.vbforge.concierge.service.impl.ShiftSchedulingServiceImpl;
import com.vbforge.concierge.util.TestDataBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for ShiftSchedulingService
 */
@ExtendWith(MockitoExtension.class)
public class ShiftSchedulingServiceTest {

    @Mock
    private ShiftAssignmentRepository shiftRepository;

    @Mock
    private ConciergeRepository conciergeRepository;

    @Mock
    private ShiftAssignmentMapper shiftMapper;

    @Mock
    private ValidationService validationService;

    @InjectMocks
    private ShiftSchedulingServiceImpl shiftSchedulingService;

    private Concierge alice;
    private ShiftAssignment shift;
    private ShiftAssignmentDto shiftDto;
    private LocalDate testDate;

    @BeforeEach
    void setUp() {
        alice = TestDataBuilder.createAlice();
        alice.setId(1L);

        testDate = LocalDate.now().plusDays(1);

        shift = TestDataBuilder.createShiftForDate(testDate, alice);
        shift.setId(1L);

        shiftDto = TestDataBuilder.defaultShiftAssignmentDto()
                .id(1L)
                .shiftDate(testDate)
                .conciergeId(1L)
                .conciergeName("Alice")
                .build();
    }

    @Test
    void testAssignShift() {
        // Given
        when(shiftMapper.toEntity(shiftDto)).thenReturn(shift);
        when(conciergeRepository.findById(1L)).thenReturn(Optional.of(alice));
        when(shiftRepository.save(shift)).thenReturn(shift);
        when(shiftMapper.toDto(shift)).thenReturn(shiftDto);
        doNothing().when(validationService).validateShiftAssignment(any());
        doNothing().when(validationService).checkShiftConflict(any());
        doNothing().when(validationService).validateConciergeExists(any());

        // When
        ShiftAssignmentDto result = shiftSchedulingService.assignShift(shiftDto);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getShiftDate()).isEqualTo(testDate);
        verify(shiftRepository).save(shift);
    }

    @Test
    void testShouldThrowExceptionWhenAssigningConflictingShift() {
        // Given
        doThrow(new ShiftConflictException(testDate.toString()))
                .when(validationService).checkShiftConflict(testDate);

        // When/Then
        assertThatThrownBy(() -> shiftSchedulingService.assignShift(shiftDto))
                .isInstanceOf(ShiftConflictException.class);

        verify(shiftRepository, never()).save(any());
    }

    @Test
    void testGetShiftById() {
        // Given
        when(shiftRepository.findById(1L)).thenReturn(Optional.of(shift));
        when(shiftMapper.toDto(shift)).thenReturn(shiftDto);

        // When
        ShiftAssignmentDto result = shiftSchedulingService.getShiftById(1L);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
    }

    @Test
    void testGetShiftByDate() {
        // Given
        when(shiftRepository.findByShiftDateAndDeletedFalse(testDate))
                .thenReturn(Optional.of(shift));
        when(shiftMapper.toDto(shift)).thenReturn(shiftDto);

        // When
        ShiftAssignmentDto result = shiftSchedulingService.getShiftByDate(testDate);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getShiftDate()).isEqualTo(testDate);
    }

    @Test
    void testReturnNullWhenShiftNotFoundByDate() {
        // Given
        when(shiftRepository.findByShiftDateAndDeletedFalse(testDate))
                .thenReturn(Optional.empty());

        // When
        ShiftAssignmentDto result = shiftSchedulingService.getShiftByDate(testDate);

        // Then
        assertThat(result).isNull();
    }

    @Test
    void testRemoveShift() {
        // Given
        when(shiftRepository.findById(1L)).thenReturn(Optional.of(shift));
        when(shiftRepository.save(shift)).thenReturn(shift);

        // When
        shiftSchedulingService.removeShift(1L);

        // Then
        verify(shiftRepository).save(shift);
        assertThat(shift.isDeleted()).isTrue();
    }

    @Test
    void testShouldThrowExceptionWhenRemovingNonExistentShift() {
        // Given
        when(shiftRepository.findById(999L)).thenReturn(Optional.empty());

        // When/Then
        assertThatThrownBy(() -> shiftSchedulingService.removeShift(999L))
                .isInstanceOf(ShiftAssignmentNotFoundException.class);
    }

    @Test
    void testGetShiftsByConcierge() {
        // Given
        List<ShiftAssignment> shifts = List.of(shift);
        when(shiftRepository.findByConciergeIdAndDeletedFalse(1L)).thenReturn(shifts);
        when(shiftMapper.toDtoList(shifts)).thenReturn(List.of(shiftDto));

        // When
        List<ShiftAssignmentDto> result = shiftSchedulingService.getShiftsByConcierge(1L);

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getConciergeId()).isEqualTo(1L);
    }

    @Test
    void testCheckIfShiftIsAssigned() {
        // Given
        when(shiftRepository.existsByShiftDateAndDeletedFalse(testDate)).thenReturn(true);

        // When
        boolean isAssigned = shiftSchedulingService.isShiftAssigned(testDate);

        // Then
        assertThat(isAssigned).isTrue();
    }

    @Test
    void testBulkAssignShifts() {
        // Given
        List<ShiftAssignmentDto> dtoList = List.of(shiftDto);
        when(shiftMapper.toEntity(any())).thenReturn(shift);
        when(conciergeRepository.findById(anyLong())).thenReturn(Optional.of(alice));
        when(shiftRepository.save(any())).thenReturn(shift);
        when(shiftMapper.toDto(any())).thenReturn(shiftDto);

        // When
        List<ShiftAssignmentDto> result = shiftSchedulingService.bulkAssignShifts(dtoList);

        // Then
        assertThat(result).hasSize(1);
    }

}
