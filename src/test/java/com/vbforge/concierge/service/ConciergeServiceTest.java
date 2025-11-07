package com.vbforge.concierge.service;

import com.vbforge.concierge.dto.ConciergeDto;
import com.vbforge.concierge.entity.Concierge;
import com.vbforge.concierge.enums.ColorType;
import com.vbforge.concierge.exception.ConciergeDuplicateException;
import com.vbforge.concierge.exception.ConciergeNotFoundException;
import com.vbforge.concierge.mapper.ConciergeMapper;
import com.vbforge.concierge.repository.ConciergeRepository;
import com.vbforge.concierge.service.impl.ConciergeServiceImpl;
import com.vbforge.concierge.util.TestDataBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Unit tests for ConciergeService
 */
@ExtendWith(MockitoExtension.class)
public class ConciergeServiceTest {

    @Mock
    private ConciergeRepository conciergeRepository;

    @Mock
    private ConciergeMapper conciergeMapper;

    @Mock
    private ValidationService validationService;

    @InjectMocks
    private ConciergeServiceImpl conciergeService;

    private Concierge alice;
    private ConciergeDto aliceDto;

    @BeforeEach
    void setUp() {
        alice = TestDataBuilder.createAlice();
        alice.setId(1L);

        aliceDto = TestDataBuilder.defaultConciergeDto()
                .id(1L)
                .name("Alice")
                .color(ColorType.BLUE)
                .build();
    }

    @Test
    public void testCreateConcierge(){
        //given
        when(conciergeMapper.toEntity(aliceDto)).thenReturn(alice);
        when(conciergeRepository.save(alice)).thenReturn(alice);
        when(conciergeMapper.toDto(alice)).thenReturn(aliceDto);
        doNothing().when(validationService).validateConciergeNameUnique(anyString());

        //when
        ConciergeDto result = conciergeService.createConcierge(aliceDto);

        //then
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Alice");
        verify(validationService).validateConciergeNameUnique("Alice");
        verify(conciergeRepository).save(alice);
    }

    @Test
    void testShouldThrowExceptionWhenCreatingDuplicateConcierge() {
        // Given
        doThrow(new ConciergeDuplicateException("Alice"))
                .when(validationService).validateConciergeNameUnique("Alice");

        // When/Then
        assertThatThrownBy(() -> conciergeService.createConcierge(aliceDto))
                .isInstanceOf(ConciergeDuplicateException.class)
                .hasMessageContaining("Alice");

        verify(conciergeRepository, never()).save(any());
    }

    @Test
    void testGetConciergeById() {
        // Given
        when(conciergeRepository.findById(1L)).thenReturn(Optional.of(alice));
        when(conciergeMapper.toDto(alice)).thenReturn(aliceDto);
        when(conciergeRepository.countShiftsByMonth(anyLong(), anyInt(), anyInt())).thenReturn(5L);
        when(conciergeRepository.countTotalShifts(anyLong())).thenReturn(10L);

        // When
        ConciergeDto result = conciergeService.getConciergeById(1L);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Alice");
        assertThat(result.getCurrentMonthShiftCount()).isEqualTo(5);
        assertThat(result.getTotalShiftCount()).isEqualTo(10);
    }

    @Test
    void testShouldThrowExceptionWhenConciergeNotFound() {
        // Given
        when(conciergeRepository.findById(999L)).thenReturn(Optional.empty());

        // When/Then
        assertThatThrownBy(() -> conciergeService.getConciergeById(999L))
                .isInstanceOf(ConciergeNotFoundException.class);
    }

    @Test
    void testGetAllActiveConcierges() {
        // Given
        Concierge bob = TestDataBuilder.createBob();
        List<Concierge> concierges = List.of(alice, bob);
        when(conciergeRepository.findByActiveTrueAndDeletedFalse()).thenReturn(concierges);
        when(conciergeMapper.toDtoList(concierges)).thenReturn(List.of(aliceDto));

        // When
        List<ConciergeDto> result = conciergeService.getAllActiveConcierges();

        // Then
        assertThat(result).isNotEmpty();
        verify(conciergeRepository).findByActiveTrueAndDeletedFalse();
    }

    @Test
    void testUpdateConcierge() {
        // Given
        when(conciergeRepository.findById(1L)).thenReturn(Optional.of(alice));
        when(conciergeRepository.save(alice)).thenReturn(alice);
        when(conciergeMapper.toDto(alice)).thenReturn(aliceDto);
        doNothing().when(conciergeMapper).updateEntityFromDto(any(), any());

        // When
        ConciergeDto result = conciergeService.updateConcierge(1L, aliceDto);

        // Then
        assertThat(result).isNotNull();
        verify(conciergeMapper).updateEntityFromDto(aliceDto, alice);
        verify(conciergeRepository).save(alice);
    }

    @Test
    void testDeleteConcierge() {
        // Given
        when(conciergeRepository.findById(1L)).thenReturn(Optional.of(alice));
        when(conciergeRepository.save(alice)).thenReturn(alice);
        doNothing().when(validationService).validateConciergeCanBeDeleted(1L);

        // When
        conciergeService.deleteConcierge(1L);

        // Then
        verify(validationService).validateConciergeCanBeDeleted(1L);
        verify(conciergeRepository).save(alice);
        assertThat(alice.isDeleted()).isTrue();
    }

    @Test
    void testActivateConcierge() {
        // Given
        alice.setActive(false);
        when(conciergeRepository.findById(1L)).thenReturn(Optional.of(alice));
        when(conciergeRepository.save(alice)).thenReturn(alice);

        // When
        conciergeService.activateConcierge(1L);

        // Then
        assertThat(alice.isActive()).isTrue();
        verify(conciergeRepository).save(alice);
    }

    @Test
    void testDeactivateConcierge() {
        // Given
        alice.setActive(true);
        when(conciergeRepository.findById(1L)).thenReturn(Optional.of(alice));
        when(conciergeRepository.save(alice)).thenReturn(alice);

        // When
        conciergeService.deactivateConcierge(1L);

        // Then
        assertThat(alice.isActive()).isFalse();
        verify(conciergeRepository).save(alice);
    }

    @Test
    void testCheckIfNameExists() {
        // Given
        when(conciergeRepository.existsByNameIgnoreCase("Alice")).thenReturn(true);

        // When
        boolean exists = conciergeService.existsByName("Alice");

        // Then
        assertThat(exists).isTrue();
    }

}




















