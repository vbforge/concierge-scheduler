package com.vbforge.concierge.controller;

import com.vbforge.concierge.dto.ConciergeDto;
import com.vbforge.concierge.service.ConciergeService;
import com.vbforge.concierge.service.MonthScheduleService;
import com.vbforge.concierge.util.DateUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controller for statistics and reports
 */
@Slf4j
@Controller
@RequestMapping("/statistics")
@RequiredArgsConstructor
public class StatisticsController {

    private final MonthScheduleService monthScheduleService;
    private final ConciergeService conciergeService;

    /**
     * Show statistics dashboard for current month
     */
    @GetMapping
    public String showStatistics(Model model) {
        System.out.println("statistics...");
        model.addAttribute("activePage", "statistics");
        int currentYear = DateUtils.getCurrentYear();
        int currentMonth = DateUtils.getCurrentMonth();
        
        return showStatisticsForMonth(currentYear, currentMonth, model);
    }

    /**
     * Show statistics for specific month
     */
    @GetMapping("/{year}/{month}")
    public String showStatisticsForMonth(
            @PathVariable int year,
            @PathVariable int month,
            Model model) {

        log.debug("Showing statistics for {}-{}", year, month);

        try {
            // Get all active concierges
            List<ConciergeDto> concierges = conciergeService.getAllActiveConcierges();

            // Get shift count by concierge for the month
            Map<String, Integer> shiftCounts = monthScheduleService.getShiftCountByConcierge(year, month);

            // Calculate statistics
            int totalShifts = shiftCounts.values().stream()
                    .mapToInt(Integer::intValue)
                    .sum();

            double averageShifts = concierges.isEmpty() ? 0 : 
                (double) totalShifts / concierges.size();

            // Find most and least active concierge
            String mostActive = null;
            String leastActive = null;
            int maxShifts = 0;
            int minShifts = Integer.MAX_VALUE;

            for (Map.Entry<String, Integer> entry : shiftCounts.entrySet()) {
                if (entry.getValue() > maxShifts) {
                    maxShifts = entry.getValue();
                    mostActive = entry.getKey();
                }
                if (entry.getValue() < minShifts) {
                    minShifts = entry.getValue();
                    leastActive = entry.getKey();
                }
            }

            // Prepare chart data
            Map<String, Object> chartData = new HashMap<>();
            chartData.put("labels", shiftCounts.keySet());
            chartData.put("data", shiftCounts.values());

            // Add to model
            model.addAttribute("year", year);
            model.addAttribute("month", month);
            model.addAttribute("monthName", DateUtils.getMonthName(month));
            model.addAttribute("concierges", concierges);
            model.addAttribute("shiftCounts", shiftCounts);
            model.addAttribute("totalShifts", totalShifts);
            model.addAttribute("averageShifts", String.format("%.1f", averageShifts));
            model.addAttribute("mostActive", mostActive);
            model.addAttribute("maxShifts", maxShifts);
            model.addAttribute("leastActive", leastActive);
            model.addAttribute("minShifts", minShifts == Integer.MAX_VALUE ? 0 : minShifts);
            model.addAttribute("chartData", chartData);
            model.addAttribute("activePage", "statistics");

            // Navigation
            int[] prevMonth = DateUtils.getPreviousMonth(year, month);
            int[] nextMonth = DateUtils.getNextMonth(year, month);
            
            model.addAttribute("prevYear", prevMonth[0]);
            model.addAttribute("prevMonth", prevMonth[1]);
            model.addAttribute("nextYear", nextMonth[0]);
            model.addAttribute("nextMonth", nextMonth[1]);

            return "statistics/dashboard";
            
        } catch (Exception e) {
            log.error("Error loading statistics", e);
            model.addAttribute("errorMessage", "Failed to load statistics: " + e.getMessage());
            model.addAttribute("activePage", "statistics");
            return "statistics/dashboard";
        }
    }
}