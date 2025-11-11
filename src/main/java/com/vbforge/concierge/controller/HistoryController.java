package com.vbforge.concierge.controller;

import com.vbforge.concierge.dto.MonthHistoryDto;
import com.vbforge.concierge.service.HistoryService;
import com.vbforge.concierge.util.DateUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

/**
 * Controller for history management
 * View and restore past month snapshots
 */
@Slf4j
@Controller
@RequestMapping("/history")
@RequiredArgsConstructor
public class HistoryController {

    private final HistoryService historyService;

    /**
     * List all history snapshots with pagination
     */
    @GetMapping
    public String listHistory(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size,
            Model model) {

        log.debug("Listing history snapshots - page: {}, size: {}", page, size);

        try {
            // Get paginated history
            PageRequest pageRequest = PageRequest.of(page, size, 
                Sort.by(Sort.Direction.DESC, "year", "month"));
            Page<MonthHistoryDto> historyPage = historyService.getHistoryPage(pageRequest);

            model.addAttribute("historyPage", historyPage);
            model.addAttribute("activePage", "history");
            
            return "history/list";
            
        } catch (Exception e) {
            log.error("Error loading history", e);
            model.addAttribute("errorMessage", "Failed to load history: " + e.getMessage());
            model.addAttribute("activePage", "history");
            return "history/list";
        }
    }

    /**
     * View specific history snapshot
     */
    @GetMapping("/{id}")
    public String viewHistory(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        log.debug("Viewing history snapshot ID: {}", id);

        try {
            MonthHistoryDto history = historyService.getHistoryById(id);
            
            model.addAttribute("history", history);
            model.addAttribute("activePage", "history");
            
            return "history/view";
            
        } catch (Exception e) {
            log.error("Error loading history snapshot", e);
            redirectAttributes.addFlashAttribute("errorMessage", 
                "Failed to load snapshot: " + e.getMessage());
            return "redirect:/history";
        }
    }

    /**
     * Create snapshot for current month (ADMIN only)
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/create")
    public String createSnapshot(
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer month,
            @RequestParam(required = false) String description,
            RedirectAttributes redirectAttributes) {

        // Default to current month if not specified
        if (year == null) year = DateUtils.getCurrentYear();
        if (month == null) month = DateUtils.getCurrentMonth();

        log.info("Creating snapshot for {}-{}", year, month);

        try {
            MonthHistoryDto created = historyService.createSnapshot(year, month, description);
            
            redirectAttributes.addFlashAttribute("successMessage", 
                "Snapshot created successfully for " + DateUtils.formatMonthYear(year, month));
            
        } catch (Exception e) {
            log.error("Error creating snapshot", e);
            redirectAttributes.addFlashAttribute("errorMessage", 
                "Failed to create snapshot: " + e.getMessage());
        }

        return "redirect:/history";
    }

    /**
     * Restore shifts from snapshot (ADMIN only)
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{id}/restore")
    public String restoreFromSnapshot(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes) {

        log.info("Restoring from snapshot ID: {}", id);

        try {
            MonthHistoryDto history = historyService.getHistoryById(id);
            historyService.restoreFromSnapshot(id);
            
            redirectAttributes.addFlashAttribute("successMessage", 
                "Schedule restored successfully from " + 
                DateUtils.formatMonthYear(history.getYear(), history.getMonth()));
            
        } catch (Exception e) {
            log.error("Error restoring from snapshot", e);
            redirectAttributes.addFlashAttribute("errorMessage", 
                "Failed to restore snapshot: " + e.getMessage());
        }

        return "redirect:/history";
    }

    /**
     * Duplicate schedule to another month (ADMIN only)
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{id}/duplicate")
    public String duplicateSchedule(
            @PathVariable Long id,
            @RequestParam int targetYear,
            @RequestParam int targetMonth,
            RedirectAttributes redirectAttributes) {

        log.info("Duplicating schedule from snapshot {} to {}-{}", id, targetYear, targetMonth);

        try {
            MonthHistoryDto source = historyService.getHistoryById(id);
            
            historyService.duplicateSchedule(
                source.getYear(), 
                source.getMonth(), 
                targetYear, 
                targetMonth
            );
            
            redirectAttributes.addFlashAttribute("successMessage", 
                "Schedule duplicated successfully to " + 
                DateUtils.formatMonthYear(targetYear, targetMonth));
            
        } catch (Exception e) {
            log.error("Error duplicating schedule", e);
            redirectAttributes.addFlashAttribute("errorMessage", 
                "Failed to duplicate schedule: " + e.getMessage());
        }

        return "redirect:/history";
    }

    /**
     * Delete history snapshot (ADMIN only)
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{id}/delete")
    public String deleteHistory(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        log.info("Deleting history snapshot ID: {}", id);

        try {
            MonthHistoryDto history = historyService.getHistoryById(id);
            historyService.deleteHistory(id);
            
            redirectAttributes.addFlashAttribute("successMessage", 
                "Snapshot deleted successfully for " + 
                DateUtils.formatMonthYear(history.getYear(), history.getMonth()));
            
        } catch (Exception e) {
            log.error("Error deleting history", e);
            redirectAttributes.addFlashAttribute("errorMessage", 
                "Failed to delete snapshot: " + e.getMessage());
        }

        return "redirect:/history";
    }

    /**
     * Create snapshot for specific month (AJAX)
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/snapshot/{year}/{month}")
    @ResponseBody
    public String createSnapshotForMonth(
            @PathVariable int year,
            @PathVariable int month,
            @RequestParam(required = false) String description) {

        log.info("Creating snapshot via AJAX for {}-{}", year, month);

        try {
            MonthHistoryDto created = historyService.createSnapshot(year, month, description);
            return "success";
            
        } catch (Exception e) {
            log.error("Error creating snapshot", e);
            return "error: " + e.getMessage();
        }
    }

    /**
     * Check if snapshot exists for month (AJAX)
     */
    @GetMapping("/exists/{year}/{month}")
    @ResponseBody
    public boolean snapshotExists(@PathVariable int year, @PathVariable int month) {
        return historyService.snapshotExists(year, month);
    }
}