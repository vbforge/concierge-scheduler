package com.vbforge.concierge.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * DTO for statistics and reports
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StatisticsDto {

    private Integer year;
    private Integer month;

    /**
     * Map of concierge name to shift count
     */
    private Map<String, Integer> shiftCountByConcierge;

    /**
     * Total shifts in the period
     */
    private Integer totalShifts;

    /**
     * Average shifts per concierge
     */
    private Double averageShiftsPerConcierge;

    /**
     * Most active concierge
     */
    private String mostActiveConcierge;
    private Integer maxShiftCount;

    /**
     * Least active concierge
     */
    private String leastActiveConcierge;
    private Integer minShiftCount;

    /**
     * Distribution data for charts
     */
    private Map<String, Object> chartData;
}