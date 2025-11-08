package com.vbforge.concierge.controller;

import com.vbforge.concierge.dto.ConciergeDto;
import com.vbforge.concierge.enums.ColorType;
import com.vbforge.concierge.service.ConciergeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

/**
 * Controller for concierge management
 */
@Slf4j
@Controller
@RequestMapping("/concierges")
@RequiredArgsConstructor
public class ConciergeController {

    private final ConciergeService conciergeService;

    /**
     * List all concierges
     */
    @GetMapping
    public String listConcierges(Model model) {
        log.debug("Listing all concierges");

        try {
            model.addAttribute("activePage", "concierges"); // ✅ highlight navbar
            List<ConciergeDto> concierges = conciergeService.getAllConcierges();
            model.addAttribute("concierges", concierges);
            return "concierge/list";

        } catch (Exception e) {
            log.error("Error listing concierges", e);
            model.addAttribute("errorMessage", "Failed to load concierges: " + e.getMessage());
            return "concierge/list";
        }
    }

    /**
     * Show create concierge form (ADMIN only)
     */
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/new")
    public String showCreateForm(Model model) {
        log.debug("Showing create concierge form");

        model.addAttribute("activePage", "concierges"); // ✅ highlight navbar
        model.addAttribute("concierge", new ConciergeDto());
        model.addAttribute("colors", ColorType.values());
        model.addAttribute("isEdit", false);

        return "concierge/form";
    }

    /**
     * Create concierge (ADMIN only)
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public String createConcierge(
            @Valid @ModelAttribute("concierge") ConciergeDto conciergeDto,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {

        log.info("Creating concierge: {}", conciergeDto.getName());

        if (bindingResult.hasErrors()) {
            log.warn("Validation errors: {}", bindingResult.getAllErrors());
            model.addAttribute("activePage", "concierges"); // ✅ highlight navbar
            model.addAttribute("colors", ColorType.values());
            model.addAttribute("isEdit", false);
            return "concierge/form";
        }

        try {
            ConciergeDto created = conciergeService.createConcierge(conciergeDto);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Concierge '" + created.getName() + "' created successfully");
            return "redirect:/concierges";

        } catch (Exception e) {
            log.error("Error creating concierge", e);
            model.addAttribute("activePage", "concierges"); // ✅ highlight navbar
            model.addAttribute("errorMessage", "Failed to create concierge: " + e.getMessage());
            model.addAttribute("colors", ColorType.values());
            model.addAttribute("isEdit", false);
            return "concierge/form";
        }
    }

    /**
     * Show edit concierge form (ADMIN only)
     */
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        log.debug("Showing edit form for concierge ID: {}", id);

        try {
            ConciergeDto concierge = conciergeService.getConciergeById(id);
            model.addAttribute("activePage", "concierges"); // ✅ highlight navbar
            model.addAttribute("concierge", concierge);
            model.addAttribute("colors", ColorType.values());
            model.addAttribute("isEdit", true);

            return "concierge/form";

        } catch (Exception e) {
            log.error("Error loading concierge", e);
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Failed to load concierge: " + e.getMessage());
            return "redirect:/concierges";
        }
    }

    /**
     * Update concierge (ADMIN only)
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{id}")
    public String updateConcierge(
            @PathVariable Long id,
            @Valid @ModelAttribute("concierge") ConciergeDto conciergeDto,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {

        log.info("Updating concierge ID: {}", id);

        if (bindingResult.hasErrors()) {
            log.warn("Validation errors: {}", bindingResult.getAllErrors());
            model.addAttribute("activePage", "concierges"); // ✅ highlight navbar
            model.addAttribute("colors", ColorType.values());
            model.addAttribute("isEdit", true);
            return "concierge/form";
        }

        try {
            ConciergeDto updated = conciergeService.updateConcierge(id, conciergeDto);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Concierge '" + updated.getName() + "' updated successfully");
            return "redirect:/concierges";

        } catch (Exception e) {
            log.error("Error updating concierge", e);
            model.addAttribute("activePage", "concierges"); // ✅ highlight navbar
            model.addAttribute("errorMessage", "Failed to update concierge: " + e.getMessage());
            model.addAttribute("colors", ColorType.values());
            model.addAttribute("isEdit", true);
            return "concierge/form";
        }
    }

    /**
     * Delete concierge (ADMIN only)
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{id}/delete")
    public String deleteConcierge(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        log.info("Deleting concierge ID: {}", id);

        try {
            ConciergeDto concierge = conciergeService.getConciergeById(id);
            conciergeService.deleteConcierge(id);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Concierge '" + concierge.getName() + "' deleted successfully");

        } catch (Exception e) {
            log.error("Error deleting concierge", e);
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Failed to delete concierge: " + e.getMessage());
        }

        return "redirect:/concierges";
    }

    /**
     * Activate concierge (ADMIN only)
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{id}/activate")
    public String activateConcierge(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        log.info("Activating concierge ID: {}", id);

        try {
            conciergeService.activateConcierge(id);
            redirectAttributes.addFlashAttribute("successMessage", "Concierge activated");

        } catch (Exception e) {
            log.error("Error activating concierge", e);
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Failed to activate concierge: " + e.getMessage());
        }

        return "redirect:/concierges";
    }

    /**
     * Deactivate concierge (ADMIN only)
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{id}/deactivate")
    public String deactivateConcierge(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        log.info("Deactivating concierge ID: {}", id);

        try {
            conciergeService.deactivateConcierge(id);
            redirectAttributes.addFlashAttribute("successMessage", "Concierge deactivated");

        } catch (Exception e) {
            log.error("Error deactivating concierge", e);
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Failed to deactivate concierge: " + e.getMessage());
        }

        return "redirect:/concierges";
    }

}
