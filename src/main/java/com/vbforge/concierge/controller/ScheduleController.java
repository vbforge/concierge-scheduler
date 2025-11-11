package com.vbforge.concierge.controller;

import com.vbforge.concierge.dto.MonthScheduleDto;
import com.vbforge.concierge.dto.ShiftAssignmentDto;
import com.vbforge.concierge.service.ConciergeService;
import com.vbforge.concierge.service.MonthScheduleService;
import com.vbforge.concierge.service.ShiftSchedulingService;
import com.vbforge.concierge.util.DateUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;

/**
 * Controller for schedule management
 * Main calendar view and shift assignment operations
 */
@Slf4j
@Controller
@RequestMapping("/schedule")
@RequiredArgsConstructor
public class ScheduleController {

    private final MonthScheduleService monthScheduleService;
    private final ShiftSchedulingService shiftSchedulingService;
    private final ConciergeService conciergeService;


    /**
     * Display calendar for current month
     */
    @GetMapping
    public String showSchedule(Model model) {
        int currentYear = DateUtils.getCurrentYear();
        int currentMonth = DateUtils.getCurrentMonth();

        return showScheduleForMonth(currentYear, currentMonth, model);
    }

    /**
     * Display calendar for specific month
     */
    @GetMapping("/{year}/{month}")
    public String showScheduleForMonth(
            @PathVariable int year,
            @PathVariable int month,
            Model model) {

        log.debug("Displaying schedule for {}-{}", year, month);

        try {
            // Set active page for navbar
            model.addAttribute("activePage", "schedule");

            // Get month schedule
            MonthScheduleDto schedule = monthScheduleService.getMonthSchedule(year, month);

            // Get all active concierges for dropdown
            model.addAttribute("schedule", schedule);
            model.addAttribute("concierges", conciergeService.getAllActiveConcierges());
            model.addAttribute("currentYear", year);
            model.addAttribute("currentMonth", month);

            // Navigation helpers
            int[] prevMonth = DateUtils.getPreviousMonth(year, month);
            int[] nextMonth = DateUtils.getNextMonth(year, month);

            model.addAttribute("prevYear", prevMonth[0]);
            model.addAttribute("prevMonth", prevMonth[1]);
            model.addAttribute("nextYear", nextMonth[0]);
            model.addAttribute("nextMonth", nextMonth[1]);

            return "schedule/calendar";

        } catch (Exception e) {
            log.error("Error loading schedule", e);
            model.addAttribute("errorMessage", "Failed to load schedule: " + e.getMessage());
            return "error/500";
        }
    }

    /**
     * Assign shift (ADMIN only)
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/assign")
    public String assignShift(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam Long conciergeId,
            @RequestParam(required = false) String notes,
            RedirectAttributes redirectAttributes) {

        log.info("Assigning shift for date: {} to concierge: {}", date, conciergeId);

        try {
            ShiftAssignmentDto dto = ShiftAssignmentDto.builder()
                    .shiftDate(date)
                    .conciergeId(conciergeId)
                    .notes(notes)
                    .build();

            shiftSchedulingService.assignShift(dto);

            redirectAttributes.addFlashAttribute("successMessage",
                    "Shift assigned successfully for " + DateUtils.formatShortDate(date));

        } catch (Exception e) {
            log.error("Error assigning shift", e);
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Failed to assign shift: " + e.getMessage());
        }

        // Redirect to month view
        return "redirect:/schedule/" + date.getYear() + "/" + date.getMonthValue();
    }

    /**
     * Remove shift (ADMIN only)
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/remove")
    public String removeShift(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            RedirectAttributes redirectAttributes) {

        log.info("Removing shift for date: {}", date);

        try {
            shiftSchedulingService.removeShiftByDate(date);

            redirectAttributes.addFlashAttribute("successMessage",
                    "Shift removed successfully for " + DateUtils.formatShortDate(date));

        } catch (Exception e) {
            log.error("Error removing shift", e);
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Failed to remove shift: " + e.getMessage());
        }

        return "redirect:/schedule/" + date.getYear() + "/" + date.getMonthValue();
    }

    /**
     * Get shift details (AJAX endpoint)
     */
    @GetMapping("/shift/{date}")
    @ResponseBody
    public ShiftAssignmentDto getShiftByDate(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {

        log.debug("Getting shift for date: {}", date);
        return shiftSchedulingService.getShiftByDate(date);
    }

    /**
     * Quick navigation to today
     */
    @GetMapping("/today")
    public String goToToday() {
        LocalDate today = LocalDate.now();
        return "redirect:/schedule/" + today.getYear() + "/" + today.getMonthValue();
    }

}
