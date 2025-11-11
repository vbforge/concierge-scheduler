package com.vbforge.concierge.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.vbforge.concierge.dto.MonthHistoryDto;
import com.vbforge.concierge.dto.ShiftAssignmentDto;
import com.vbforge.concierge.entity.MonthHistory;
import com.vbforge.concierge.exception.MonthHistoryNotFoundException;
import com.vbforge.concierge.exception.SnapshotAlreadyExistsException;
import com.vbforge.concierge.exception.SnapshotCreationException;
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
import java.util.Optional;

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

    /*@Override
    @Transactional
    public MonthHistoryDto createSnapshot(int year, int monthValue, String description) {
        log.info("Creating snapshot for {}-{}", year, monthValue);

        validationService.validateYearMonth(year, monthValue);

        // ✅ Step 1: Check if snapshot already exists
        Optional<MonthHistory> existing = historyRepository.findByYearAndMonthAndDeletedFalse(year, monthValue);
        if (existing.isPresent()) {
            log.warn("Snapshot for {}-{} already exists (ID={})", year, monthValue, existing.get().getId());
            throw new SnapshotException("Snapshot for " + year + "-" + monthValue + " already exists");
        }

        // ✅ Step 2: Get all shifts for the month
        List<ShiftAssignmentDto> shifts = monthScheduleService.getShiftsForMonth(year, monthValue);

        // ✅ Step 3: Serialize to JSON
        String snapshotJson;
        try {
            snapshotJson = objectMapper.writeValueAsString(shifts);
        } catch (JsonProcessingException e) {
            throw new SnapshotException("Failed to serialize shifts to JSON", e);
        }

        // ✅ Step 4: Create and save new history record
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
    }*/

    @Override
    @Transactional
    public MonthHistoryDto createSnapshot(int year, int month, String description) {
        log.info("Creating new snapshot for {}/{}", month, year);

        try {
            // 1️⃣ Check if a snapshot already exists for this year/month
            Optional<MonthHistory> existing = historyRepository.findByYearAndMonth(year, month);

            if (existing.isPresent()) {
                MonthHistory found = existing.get();

                if (!found.isDeleted()) {
                    // Snapshot already exists and is active
                    log.warn("Snapshot already exists for {}/{} (ID={}) and is active.", month, year, found.getId());
                    throw new SnapshotAlreadyExistsException(
                            String.format("Snapshot for %d/%d already exists (ID=%d)", month, year, found.getId())
                    );
                }

                // 2️⃣ Reuse deleted snapshot
                log.info("Reusing previously deleted snapshot for {}/{} (ID={})", month, year, found.getId());
                found.setDeleted(false);
                found.setUpdatedAt(LocalDateTime.now());
                found.setDescription(description);
//                found.setSnapshotJson(snapshotJson);

                MonthHistory reused = historyRepository.save(found);
                log.debug("Snapshot successfully reactivated for {}/{} with ID={}", month, year, reused.getId());
                return historyMapper.toDto(reused);
            }

            // 3️⃣ No snapshot found → create new one
            MonthHistory newSnapshot = new MonthHistory();
            newSnapshot.setYear(year);
            newSnapshot.setMonth(month);
            newSnapshot.setDescription(description);
//            newSnapshot.setSnapshotJson(snapshotJson);
            newSnapshot.setCreatedAt(LocalDateTime.now());
            newSnapshot.setUpdatedAt(LocalDateTime.now());
            newSnapshot.setDeleted(false);

            MonthHistory saved = historyRepository.save(newSnapshot);
            log.info("New snapshot created for {}/{} with ID={}", month, year, saved.getId());
            return historyMapper.toDto(saved);

        } catch (SnapshotAlreadyExistsException e) {
            throw e; // rethrow custom business exception as is
        } catch (Exception e) {
            log.error("Failed to create snapshot for {}/{}: {}", month, year, e.getMessage(), e);
            throw new SnapshotCreationException(
                    String.format("Unable to create snapshot for %d/%d", month, year), e
            );
        }
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
