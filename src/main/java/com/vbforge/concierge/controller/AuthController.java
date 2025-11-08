package com.vbforge.concierge.controller;

import com.vbforge.concierge.security.SecurityUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Controller for authentication (login/logout)
 */
@Slf4j
@Controller
public class AuthController {

    /**
     * Show login page
     * If user is already authenticated, redirect to schedule
     */
    @GetMapping("/login")
    public String loginPage(
            @RequestParam(required = false) String error,
            @RequestParam(required = false) String logout,
            Model model) {

        log.debug("Login page requested");

        // If already authenticated, redirect to schedule
        if (SecurityUtils.isAuthenticated()) {
            log.debug("User already authenticated, redirecting to schedule");
            return "redirect:/schedule";
        }

        if (error != null) {
            model.addAttribute("error", "Invalid username or password");
            log.warn("Login failed - invalid credentials");
        }

        if (logout != null) {
            model.addAttribute("message", "You have been logged out successfully");
            log.info("User logged out");
        }

        return "auth/login";
    }

    /**
     * Root path redirect;
     * Redirect to schedule if authenticated, otherwise to login;
     */
    @GetMapping("/")
    public String root() {
        if (SecurityUtils.isAuthenticated()) {
            return "redirect:/schedule";
        }
        return "redirect:/login";
    }

    /**
     * Home page redirect (same as root);
     */
    @GetMapping("/home")
    public String home() {
        return "redirect:/schedule";
    }

}
