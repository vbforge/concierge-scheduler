package com.vbforge.concierge.service;

import com.vbforge.concierge.dto.MonthHistoryDto;
import com.vbforge.concierge.exception.MonthHistoryNotFoundException;
import com.vbforge.concierge.exception.SnapshotException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * Service interface for Month History operations
 */
public interface HistoryService {

    /**
     * Create snapshot for specific month
     * @param year Year
     * @param monthValue Month (1-12)
     * @return Created history DTO
     * @throws SnapshotException if snapshot creation fails
     */
    MonthHistoryDto createSnapshot(int year, int monthValue);

    /**
     * Create snapshot with description
     * @param year Year
     * @param monthValue Month (1-12)
     * @param description Snapshot description
     * @return Created history DTO
     */
    MonthHistoryDto createSnapshot(int year, int monthValue, String description);

    /**
     * Get history by ID
     * @param id History ID
     * @return History DTO
     * @throws MonthHistoryNotFoundException if not found
     */
    MonthHistoryDto getHistoryById(Long id);

    /**
     * Get history for specific month
     * @param year Year
     * @param monthValue Month (1-12)
     * @return History DTO or null if not exists
     */
    MonthHistoryDto getHistoryByYearMonth(int year, int monthValue);

    /**
     * Get all history records for a year
     * @param year Year
     * @return List of history DTOs
     */
    List<MonthHistoryDto> getHistoryByYear(int year);

    /**
     * Get all history records (sorted by date desc)
     * @return List of history DTOs
     */
    List<MonthHistoryDto> getAllHistory();

    /**
     * Get history with pagination
     * @param pageable Pagination parameters
     * @return Page of history DTOs
     */
    Page<MonthHistoryDto> getHistoryPage(Pageable pageable);

    /**
     * Get latest N history records
     * @param limit Number of records
     * @return List of history DTOs
     */
    List<MonthHistoryDto> getLatestHistory(int limit);

    /**
     * Restore shifts from snapshot
     * @param historyId History ID
     * @throws MonthHistoryNotFoundException if history not found
     * @throws SnapshotException if restoration fails
     */
    void restoreFromSnapshot(Long historyId);

    /**
     * Duplicate month schedule to another month
     * @param sourceYear Source year
     * @param sourceMonth Source month
     * @param targetYear Target year
     * @param targetMonth Target month
     */
    void duplicateSchedule(int sourceYear, int sourceMonth, int targetYear, int targetMonth);

    /**
     * Delete history record
     * @param id History ID
     */
    void deleteHistory(Long id);

    /**
     * Check if snapshot exists for month
     * @param year Year
     * @param monthValue Month (1-12)
     * @return true if exists
     */
    boolean snapshotExists(int year, int monthValue);

    /**
     * Count total snapshots
     * @return Total number of snapshots
     */
    long countSnapshots();

}
