package com.vbforge.concierge.exception;

import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Global exception handler for the entire application
 * Handles both REST API and Thymeleaf MVC exceptions
 */
@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    // ============================================================
    // RESOURCE NOT FOUND EXCEPTIONS (404)
    // ============================================================

    @ExceptionHandler(ResourceNotFoundException.class)
    public String handleResourceNotFoundException(
            ResourceNotFoundException ex,
            RedirectAttributes redirectAttributes) {

        log.error("Resource not found: {}", ex.getMessage());

        redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        return "redirect:/schedule"; // Redirect to main page
    }

    @ExceptionHandler(ConciergeNotFoundException.class)
    public String handleConciergeNotFoundException(
            ConciergeNotFoundException ex,
            RedirectAttributes redirectAttributes) {

        log.error("Concierge not found: {}", ex.getMessage());

        redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        return "redirect:/concierges";
    }

    @ExceptionHandler(ShiftAssignmentNotFoundException.class)
    public String handleShiftAssignmentNotFoundException(
            ShiftAssignmentNotFoundException ex,
            RedirectAttributes redirectAttributes) {

        log.error("Shift assignment not found: {}", ex.getMessage());

        redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        return "redirect:/schedule";
    }

    @ExceptionHandler(MonthHistoryNotFoundException.class)
    public String handleMonthHistoryNotFoundException(
            MonthHistoryNotFoundException ex,
            RedirectAttributes redirectAttributes) {

        log.error("Month history not found: {}", ex.getMessage());

        redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        return "redirect:/history";
    }

    // ============================================================
    // VALIDATION EXCEPTIONS (400)
    // ============================================================

    @ExceptionHandler(ShiftConflictException.class)
    public String handleShiftConflictException(
            ShiftConflictException ex,
            RedirectAttributes redirectAttributes) {

        log.warn("Shift conflict: {}", ex.getMessage());

        redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        return "redirect:/schedule";
    }

    @ExceptionHandler(InvalidInputException.class)
    public String handleInvalidInputException(
            InvalidInputException ex,
            RedirectAttributes redirectAttributes) {

        log.warn("Invalid input: {}", ex.getMessage());

        redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        return "redirect:/schedule";
    }

    @ExceptionHandler(InvalidDateException.class)
    public String handleInvalidDateException(
            InvalidDateException ex,
            RedirectAttributes redirectAttributes) {

        log.warn("Invalid date: {}", ex.getMessage());

        redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        return "redirect:/schedule";
    }

    // ============================================================
    // BUSINESS LOGIC EXCEPTIONS (409)
    // ============================================================

    @ExceptionHandler(ConciergeDuplicateException.class)
    public String handleConciergeDuplicateException(
            ConciergeDuplicateException ex,
            RedirectAttributes redirectAttributes) {

        log.warn("Duplicate concierge: {}", ex.getMessage());

        redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        return "redirect:/concierges";
    }

    @ExceptionHandler(ConciergeInUseException.class)
    public String handleConciergeInUseException(
            ConciergeInUseException ex,
            RedirectAttributes redirectAttributes) {

        log.warn("Concierge in use: {}", ex.getMessage());

        redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        return "redirect:/concierges";
    }

    // ============================================================
    // EXPORT EXCEPTIONS (500)
    // ============================================================

    @ExceptionHandler(ExportException.class)
    public String handleExportException(
            ExportException ex,
            RedirectAttributes redirectAttributes) {

        log.error("Export failed: {}", ex.getMessage(), ex);

        redirectAttributes.addFlashAttribute("errorMessage",
                "Export failed: " + ex.getMessage());
        return "redirect:/schedule";
    }

    // ============================================================
    // SNAPSHOT EXCEPTIONS (500)
    // ============================================================

    @ExceptionHandler(SnapshotException.class)
    public String handleSnapshotException(
            SnapshotException ex,
            RedirectAttributes redirectAttributes) {

        log.error("Snapshot operation failed: {}", ex.getMessage(), ex);

        redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        return "redirect:/history";
    }

    // ============================================================
    // SPRING VALIDATION EXCEPTIONS (400)
    // ============================================================

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public String handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            RedirectAttributes redirectAttributes) {

        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        log.warn("Validation failed: {}", errors);

        redirectAttributes.addFlashAttribute("validationErrors", errors);
        redirectAttributes.addFlashAttribute("errorMessage", "Validation failed. Please check your input.");
        return "redirect:/schedule";
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public String handleConstraintViolationException(
            ConstraintViolationException ex,
            RedirectAttributes redirectAttributes) {

        log.warn("Constraint violation: {}", ex.getMessage());

        redirectAttributes.addFlashAttribute("errorMessage",
                "Validation error: " + ex.getMessage());
        return "redirect:/schedule";
    }

    // ============================================================
    // SECURITY EXCEPTIONS (403)
    // ============================================================

    @ExceptionHandler(AccessDeniedException.class)
    public String handleAccessDeniedException(
            AccessDeniedException ex,
            RedirectAttributes redirectAttributes) {

        log.warn("Access denied: {}", ex.getMessage());

        redirectAttributes.addFlashAttribute("errorMessage",
                "Access denied. You don't have permission to perform this action.");
        return "redirect:/schedule";
    }

    // ============================================================
    // GENERIC EXCEPTIONS (500)
    // ============================================================

    @ExceptionHandler(Exception.class)
    public String handleGlobalException(
            Exception ex,
            RedirectAttributes redirectAttributes) {

        log.error("Unexpected error occurred", ex);

        redirectAttributes.addFlashAttribute("errorMessage",
                "An unexpected error occurred. Please try again later.");
        return "redirect:/schedule";
    }

    // ============================================================
    // REST API ERROR RESPONSE (for future REST endpoints)
    // ============================================================

    @ExceptionHandler(ConciergeSchedulerException.class)
    public ResponseEntity<ErrorResponse> handleConciergeSchedulerException(
            ConciergeSchedulerException ex,
            WebRequest request) {

        ErrorResponse errorResponse = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                "Bad Request",
                ex.getMessage(),
                request.getDescription(false)
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    // ============================================================
    // ERROR RESPONSE DTO (for REST API)
    // ============================================================

    public record ErrorResponse(
            LocalDateTime timestamp,
            int status,
            String error,
            String message,
            String path
    ) {}

}
