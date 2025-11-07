package com.vbforge.concierge.service;

import com.vbforge.concierge.dto.ConciergeDto;
import com.vbforge.concierge.enums.ColorType;
import com.vbforge.concierge.exception.ConciergeDuplicateException;
import com.vbforge.concierge.exception.ConciergeInUseException;
import com.vbforge.concierge.exception.ConciergeNotFoundException;

import java.util.List;

/**
 * Service interface for Concierge operations
 */
public interface ConciergeService {

    /**
     * Create a new concierge
     * @param conciergeDto Concierge data
     * @return Created concierge
     * @throws ConciergeDuplicateException if name already exists
     */
    ConciergeDto createConcierge(ConciergeDto conciergeDto);

    /**
     * Update existing concierge
     * @param id Concierge ID
     * @param conciergeDto Updated data
     * @return Updated concierge
     * @throws ConciergeNotFoundException if concierge not found
     */
    ConciergeDto updateConcierge(Long id, ConciergeDto conciergeDto);

    /**
     * Soft delete concierge
     * @param id Concierge ID
     * @throws ConciergeNotFoundException if concierge not found
     * @throws ConciergeInUseException if concierge has active shifts
     */
    void deleteConcierge(Long id);

    /**
     * Get concierge by ID
     * @param id Concierge ID
     * @return Concierge DTO
     * @throws ConciergeNotFoundException if not found
     */
    ConciergeDto getConciergeById(Long id);

    /**
     * Get concierge by name
     * @param name Concierge name
     * @return Concierge DTO
     * @throws ConciergeNotFoundException if not found
     */
    ConciergeDto getConciergeByName(String name);

    /**
     * Get all active concierges
     * @return List of active concierges
     */
    List<ConciergeDto> getAllActiveConcierges();

    /**
     * Get all concierges (including inactive)
     * @return List of all concierges
     */
    List<ConciergeDto> getAllConcierges();

    /**
     * Get concierges by color
     * @param color Color type
     * @return List of concierges with specified color
     */
    List<ConciergeDto> getConciergesByColor(ColorType color);

    /**
     * Activate concierge
     * @param id Concierge ID
     */
    void activateConcierge(Long id);

    /**
     * Deactivate concierge
     * @param id Concierge ID
     */
    void deactivateConcierge(Long id);

    /**
     * Check if concierge name exists
     * @param name Concierge name
     * @return true if exists
     */
    boolean existsByName(String name);

    /**
     * Get shift count for concierge in current month
     * @param conciergeId Concierge ID
     * @return Number of shifts
     */
    Long getCurrentMonthShiftCount(Long conciergeId);

    /**
     * Get total shift count for concierge
     * @param conciergeId Concierge ID
     * @return Total number of shifts
     */
    Long getTotalShiftCount(Long conciergeId);

}
