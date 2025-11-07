package com.vbforge.concierge.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.vbforge.concierge.dto.MonthHistoryDto;
import com.vbforge.concierge.dto.ShiftAssignmentDto;
import com.vbforge.concierge.entity.MonthHistory;
import com.vbforge.concierge.exception.MonthHistoryNotFoundException;
import com.vbforge.concierge.exception.SnapshotException;
import com.vbforge.concierge.mapper.MonthHistoryMapper;
import com.vbforge.concierge.repository.MonthHistoryRepository;
import com.vbforge.concierge.service.HistoryService;
import com.vbforge.concierge.service.MonthScheduleService;
import com.vbforge.concierge.service.ShiftSchedulingService;
import com.vbforge.concierge.service.ValidationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

/**
 * Implementation of HistoryService
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class HistoryServiceImpl implements HistoryService {

    private final MonthHistoryRepository historyRepository;
    private final MonthHistoryMapper historyMapper;
    private final MonthScheduleService monthScheduleService;
    private final ShiftSchedulingService shiftSchedulingService;
    private final ValidationService validationService;
    private final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule());


    @Override
    @Transactional
    public MonthHistoryDto createSnapshot(int year, int monthValue) {
        return createSnapshot(year, monthValue, null);
    }

    @Override
    @Transactional
    public MonthHistoryDto createSnapshot(int year, int monthValue, String description) {
        log.info("Creating snapshot for {}-{}", year, monthValue);

        validationService.validateYearMonth(year, monthValue);

        // Get all shifts for the month
        List<ShiftAssignmentDto> shifts = monthScheduleService.getShiftsForMonth(year, monthValue);

        // Serialize to JSON
        String snapshotJson;
        try {
            snapshotJson = objectMapper.writeValueAsString(shifts);
        } catch (JsonProcessingException e) {
            throw new SnapshotException("Failed to serialize shifts to JSON", e);
        }

        // Create history record
        MonthHistory history = MonthHistory.builder()
                .year(year)
                .month(monthValue)
                .snapshotJson(snapshotJson)
                .snapshotDate(LocalDateTime.now())
                .description(description)
                .totalShifts(shifts.size())
                .build();

        MonthHistory saved = historyRepository.save(history);

        log.info("Snapshot created with ID: {}", saved.getId());
        return historyMapper.toDto(saved);
    }

    @Override
    public MonthHistoryDto getHistoryById(Long id) {
        log.debug("Getting history by ID: {}", id);

        MonthHistory history = findHistoryById(id);
        return historyMapper.toDto(history);
    }

    @Override
    public MonthHistoryDto getHistoryByYearMonth(int year, int monthValue) {
        log.debug("Getting history for {}-{}", year, monthValue);

        validationService.validateYearMonth(year, monthValue);

        return historyRepository.findByYearAndMonthAndDeletedFalse(year, monthValue)
                .map(historyMapper::toDto)
                .orElse(null);
    }

    @Override
    public List<MonthHistoryDto> getHistoryByYear(int year) {
        log.debug("Getting history for year: {}", year);

        List<MonthHistory> histories = historyRepository
                .findByYearAndDeletedFalseOrderByMonthDesc(year);
        return historyMapper.toDtoList(histories);
    }

    @Override
    public List<MonthHistoryDto> getAllHistory() {
        log.debug("Getting all history");

        List<MonthHistory> histories = historyRepository.findByDeletedFalseOrderByYearDescMonthDesc();
        return historyMapper.toDtoList(histories);
    }

    @Override
    public Page<MonthHistoryDto> getHistoryPage(Pageable pageable) {
        log.debug("Getting history page");

        Page<MonthHistory> page = historyRepository.findByDeletedFalse(pageable);
        return page.map(historyMapper::toDto);
    }

    @Override
    public List<MonthHistoryDto> getLatestHistory(int limit) {
        log.debug("Getting latest {} history records", limit);

        List<MonthHistory> histories = historyRepository
                .findLatestHistory(PageRequest.of(0, limit));
        return historyMapper.toDtoList(histories);
    }

    @Override
    @Transactional
    public void restoreFromSnapshot(Long historyId) {
        log.info("Restoring from snapshot ID: {}", historyId);

        MonthHistory history = findHistoryById(historyId);

        // Deserialize shifts from JSON
        List<ShiftAssignmentDto> shifts;
        try {
            ShiftAssignmentDto[] shiftsArray = objectMapper.readValue(
                    history.getSnapshotJson(),
                    ShiftAssignmentDto[].class
            );
            shifts = Arrays.asList(shiftsArray);
        } catch (JsonProcessingException e) {
            throw new SnapshotException("Failed to deserialize shifts from JSON", e);
        }

        // Clear existing shifts for the month
        LocalDate firstDay = LocalDate.of(history.getYear(), history.getMonth(), 1);
        LocalDate lastDay = firstDay.plusMonths(1).minusDays(1);

        List<ShiftAssignmentDto> existingShifts = shiftSchedulingService
                .getShiftsInDateRange(firstDay, lastDay);

        for (ShiftAssignmentDto shift : existingShifts) {
            shiftSchedulingService.removeShift(shift.getId());
        }

        // Restore shifts from snapshot
        shiftSchedulingService.bulkAssignShifts(shifts);

        log.info("Restored {} shifts from snapshot", shifts.size());
    }

    @Override
    @Transactional
    public void duplicateSchedule(int sourceYear, int sourceMonth, int targetYear, int targetMonth) {
        log.info("Duplicating schedule from {}-{} to {}-{}",
                sourceYear, sourceMonth, targetYear, targetMonth);

        validationService.validateYearMonth(sourceYear, sourceMonth);
        validationService.validateYearMonth(targetYear, targetMonth);

        // Get source month shifts
        List<ShiftAssignmentDto> sourceShifts = monthScheduleService
                .getShiftsForMonth(sourceYear, sourceMonth);

        // Calculate day offset
        LocalDate sourceFirst = LocalDate.of(sourceYear, sourceMonth, 1);
        LocalDate targetFirst = LocalDate.of(targetYear, targetMonth, 1);

        // Create new shifts for target month
        List<ShiftAssignmentDto> targetShifts = sourceShifts.stream()
                .map(shift -> {
                    int dayOfMonth = shift.getShiftDate().getDayOfMonth();
                    LocalDate newDate = targetFirst.withDayOfMonth(
                            Math.min(dayOfMonth, targetFirst.lengthOfMonth())
                    );

                    return ShiftAssignmentDto.builder()
                            .shiftDate(newDate)
                            .conciergeId(shift.getConciergeId())
                            .shiftType(shift.getShiftType())
                            .notes("Duplicated from " + sourceYear + "-" + sourceMonth)
                            .build();
                })
                .toList();

        // Assign new shifts
        shiftSchedulingService.bulkAssignShifts(targetShifts);

        log.info("Duplicated {} shifts to target month", targetShifts.size());
    }

    @Override
    @Transactional
    public void deleteHistory(Long id) {
        log.info("Deleting history ID: {}", id);

        MonthHistory history = findHistoryById(id);
        history.softDelete();
        historyRepository.save(history);

        log.info("History soft deleted: {}", id);
    }

    @Override
    public boolean snapshotExists(int year, int monthValue) {
        return historyRepository.existsByYearAndMonthAndDeletedFalse(year, monthValue);
    }

    @Override
    public long countSnapshots() {
        return historyRepository.countByDeletedFalse();
    }

    // ============================================================
    // PRIVATE HELPER METHODS
    // ============================================================

    private MonthHistory findHistoryById(Long id) {
        return historyRepository.findById(id)
                .orElseThrow(() -> new MonthHistoryNotFoundException(id));
    }
}
