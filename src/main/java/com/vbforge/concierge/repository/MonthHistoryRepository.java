package com.vbforge.concierge.repository;

import com.vbforge.concierge.entity.MonthHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for MonthHistory entity
 */
@Repository
public interface MonthHistoryRepository extends JpaRepository<MonthHistory, Long> {

    /**
     * Find history by year and month
     */
    Optional<MonthHistory> findByYearAndMonthAndDeletedFalse(Integer year, Integer month);

    /**
     * Find all history records for a specific year
     */
    List<MonthHistory> findByYearAndDeletedFalseOrderByMonthDesc(Integer year);

    /**
     * Find all history records ordered by date (most recent first)
     */
    List<MonthHistory> findByDeletedFalseOrderByYearDescMonthDesc();

    /**
     * Find all history records with pagination
     */
    Page<MonthHistory> findByDeletedFalse(Pageable pageable);

    /**
     * Check if history exists for year and month
     */
    boolean existsByYearAndMonthAndDeletedFalse(Integer year, Integer month);

    /**
     * Find latest N history records
     */
    @Query("SELECT h FROM MonthHistory h " +
           "WHERE h.deleted = false " +
           "ORDER BY h.year DESC, h.month DESC")
    List<MonthHistory> findLatestHistory(Pageable pageable);

    /**
     * Count total snapshots
     */
    long countByDeletedFalse();

    /**
     * Find history records in a year range
     */
    @Query("SELECT h FROM MonthHistory h " +
           "WHERE h.year BETWEEN :startYear AND :endYear " +
           "AND h.deleted = false " +
           "ORDER BY h.year DESC, h.month DESC")
    List<MonthHistory> findByYearRange(@Param("startYear") int startYear, 
                                       @Param("endYear") int endYear);
}