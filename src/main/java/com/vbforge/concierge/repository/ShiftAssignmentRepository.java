package com.vbforge.concierge.repository;

import com.vbforge.concierge.entity.ShiftAssignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Repository for ShiftAssignment entity
 */
@Repository
public interface ShiftAssignmentRepository extends JpaRepository<ShiftAssignment, Long> {

    /**
     * Find shift assignment by date
     */
    Optional<ShiftAssignment> findByShiftDateAndDeletedFalse(LocalDate shiftDate);

    /**
     * Find all shift assignments for a specific concierge
     */
    List<ShiftAssignment> findByConciergeIdAndDeletedFalse(Long conciergeId);

    /**
     * Find all shift assignments in a date range
     */
    List<ShiftAssignment> findByShiftDateBetweenAndDeletedFalse(LocalDate startDate, LocalDate endDate);

    /**
     * Find all shift assignments for a specific month
     */
    @Query("SELECT s FROM ShiftAssignment s " +
           "WHERE YEAR(s.shiftDate) = :year " +
           "AND MONTH(s.shiftDate) = :month " +
           "AND s.deleted = false " +
           "ORDER BY s.shiftDate ASC")
    List<ShiftAssignment> findByYearAndMonth(@Param("year") int year, @Param("month") int month);

    /**
     * Find all shift assignments for a concierge in a specific month
     */
    @Query("SELECT s FROM ShiftAssignment s " +
           "WHERE s.concierge.id = :conciergeId " +
           "AND YEAR(s.shiftDate) = :year " +
           "AND MONTH(s.shiftDate) = :month " +
           "AND s.deleted = false " +
           "ORDER BY s.shiftDate ASC")
    List<ShiftAssignment> findByConciergeAndMonth(@Param("conciergeId") Long conciergeId,
                                                   @Param("year") int year,
                                                   @Param("month") int month);

    /**
     * Check if shift exists for a specific date
     */
    boolean existsByShiftDateAndDeletedFalse(LocalDate shiftDate);

    /**
     * Count shift assignments for a specific month
     */
    @Query("SELECT COUNT(s) FROM ShiftAssignment s " +
           "WHERE YEAR(s.shiftDate) = :year " +
           "AND MONTH(s.shiftDate) = :month " +
           "AND s.deleted = false")
    Long countByYearAndMonth(@Param("year") int year, @Param("month") int month);

    /**
     * Delete all shifts for a specific concierge
     */
    void deleteByConciergeId(Long conciergeId);

    /**
     * Find all shifts in a specific year
     */
    @Query("SELECT s FROM ShiftAssignment s " +
           "WHERE YEAR(s.shiftDate) = :year " +
           "AND s.deleted = false " +
           "ORDER BY s.shiftDate ASC")
    List<ShiftAssignment> findByYear(@Param("year") int year);
}