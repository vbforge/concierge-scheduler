package com.vbforge.concierge.repository;

import com.vbforge.concierge.entity.Concierge;
import com.vbforge.concierge.enums.ColorType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for Concierge entity
 */
@Repository
public interface ConciergeRepository extends JpaRepository<Concierge, Long> {

    /**
     * Find concierge by name (case-insensitive)
     */
    Optional<Concierge> findByNameIgnoreCase(String name);

    /**
     * Find all active concierges
     */
    List<Concierge> findByActiveTrue();

    /**
     * Find all inactive concierges
     */
    List<Concierge> findByActiveFalse();

    /**
     * Find concierges by color
     */
    List<Concierge> findByColor(ColorType color);

    /**
     * Find all non-deleted concierges
     */
    List<Concierge> findByDeletedFalse();

    /**
     * Find active and non-deleted concierges
     */
    List<Concierge> findByActiveTrueAndDeletedFalse();

    /**
     * Check if concierge name exists (case-insensitive)
     */
    boolean existsByNameIgnoreCase(String name);

    /**
     * Check if concierge has any shift assignments
     */
    @Query("SELECT CASE WHEN COUNT(s) > 0 THEN true ELSE false END " +
           "FROM ShiftAssignment s WHERE s.concierge.id = :conciergeId AND s.deleted = false")
    boolean hasShiftAssignments(@Param("conciergeId") Long conciergeId);

    /**
     * Count shift assignments for concierge in specific year and month
     */
    @Query("SELECT COUNT(s) FROM ShiftAssignment s " +
           "WHERE s.concierge.id = :conciergeId " +
           "AND YEAR(s.shiftDate) = :year " +
           "AND MONTH(s.shiftDate) = :month " +
           "AND s.deleted = false")
    Long countShiftsByMonth(@Param("conciergeId") Long conciergeId, 
                            @Param("year") int year, 
                            @Param("month") int month);

    /**
     * Count total shift assignments for concierge
     */
    @Query("SELECT COUNT(s) FROM ShiftAssignment s " +
           "WHERE s.concierge.id = :conciergeId AND s.deleted = false")
    Long countTotalShifts(@Param("conciergeId") Long conciergeId);
}