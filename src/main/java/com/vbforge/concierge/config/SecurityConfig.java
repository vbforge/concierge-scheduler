package com.vbforge.concierge.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

/**
 * Spring Security Configuration
 * Configures authentication, authorization, and security filters
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    /**
     * Configure HTTP security
     * Defines which endpoints require authentication and authorization rules
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // Authorize requests
                .authorizeHttpRequests(auth -> auth
                        // Public endpoints (no authentication required)
                        .requestMatchers(
                                "/login",
                                "/logout",
                                "/css/**",
                                "/js/**",
                                "/images/**",
                                "/error/**",
                                "/h2-console/**" // For development only
                        ).permitAll()

                        // Admin-only endpoints
                        .requestMatchers(
                                "/concierges/new",
                                "/concierges/*/edit",
                                "/concierges/*/delete",
                                "/schedule/assign",
                                "/schedule/remove/**",
                                "/history/create",
                                "/history/*/restore"
                        ).hasRole("ADMIN")

                        // Endpoints accessible by both ADMIN and CONCIERGE
                        .requestMatchers(
                                "/schedule",
                                "/schedule/**",
                                "/concierges",
                                "/concierges/**",
                                "/history",
                                "/history/**",
                                "/statistics",
                                "/statistics/**"
                        ).hasAnyRole("ADMIN", "CONCIERGE")

                        // All other requests require authentication
                        .anyRequest().authenticated()
                )

                // Login configuration
                .formLogin(form -> form
                        .loginPage("/login")
                        .loginProcessingUrl("/login")
                        .defaultSuccessUrl("/schedule", true)
                        .failureUrl("/login?error=true")
                        .usernameParameter("username")
                        .passwordParameter("password")
                        .permitAll()
                )

                // Logout configuration
                .logout(logout -> logout
                        .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                        .logoutSuccessUrl("/login?logout=true")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                        .permitAll()
                )

                // Exception handling
                .exceptionHandling(exception -> exception
                        .accessDeniedPage("/error/403")
                )

                // Session management
                .sessionManagement(session -> session
                        .maximumSessions(1)
                        .maxSessionsPreventsLogin(false)
                );

        // For H2 Console (development only) - disable CSRF and frame options
        // IMPORTANT: Remove this in production!
        http.csrf(csrf -> csrf
                .ignoringRequestMatchers("/h2-console/**")
        );
        http.headers(headers -> headers
                .frameOptions(frame -> frame.sameOrigin())
        );

        return http.build();
    }

    /**
     * Password encoder bean
     * Uses BCrypt for secure password hashing
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12); // 12 rounds of hashing
    }

    /**
     * Authentication manager bean
     * Required for manual authentication
     */
    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

}
