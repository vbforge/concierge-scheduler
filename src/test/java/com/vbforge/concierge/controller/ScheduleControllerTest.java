package com.vbforge.concierge.controller;

import com.vbforge.concierge.dto.ConciergeDto;
import com.vbforge.concierge.dto.MonthScheduleDto;
import com.vbforge.concierge.dto.ShiftAssignmentDto;
import com.vbforge.concierge.enums.ColorType;
import com.vbforge.concierge.service.ConciergeService;
import com.vbforge.concierge.service.MonthScheduleService;
import com.vbforge.concierge.service.ShiftSchedulingService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Controller tests for ScheduleController
 */
//@WebMvcTest(ScheduleController.class)
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ScheduleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MonthScheduleService monthScheduleService;

    @MockBean
    private ShiftSchedulingService shiftSchedulingService;

    @MockBean
    private ConciergeService conciergeService;

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldShowSchedule() throws Exception {
        // Given
        MonthScheduleDto schedule = MonthScheduleDto.builder()
                .year(2025)
                .month(11)
                .monthName("November")
                .totalDays(30)
                .assignedDays(25)
                .unassignedDays(5)
                .firstDayOfMonth(LocalDate.of(2025, 11, 1))
                .lastDayOfMonth(LocalDate.of(2025, 11, 30))
                .startingDayOfWeek(6)
                .dailyAssignments(new HashMap<>())
                .concierges(List.of())
                .build();

        when(monthScheduleService.getMonthSchedule(anyInt(), anyInt())).thenReturn(schedule);
        when(conciergeService.getAllActiveConcierges()).thenReturn(List.of());

        // When & Then
        mockMvc.perform(get("/schedule"))
                .andExpect(status().isOk())
                .andExpect(view().name("schedule/calendar"))
                .andExpect(model().attributeExists("schedule"))
                .andExpect(model().attribute("activePage", "schedule"));
    }

    @Test
    @WithMockUser(roles = "CONCIERGE")
    void shouldAllowConciergeToViewSchedule() throws Exception {
        // Given
        MonthScheduleDto schedule = MonthScheduleDto.builder()
                .year(2025)
                .month(11)
                .monthName("November")
                .totalDays(30)
                .assignedDays(25)
                .unassignedDays(5)
                .firstDayOfMonth(LocalDate.of(2025, 11, 1))
                .lastDayOfMonth(LocalDate.of(2025, 11, 30))
                .startingDayOfWeek(6)
                .dailyAssignments(new HashMap<>())
                .concierges(List.of())
                .build();

        when(monthScheduleService.getMonthSchedule(anyInt(), anyInt())).thenReturn(schedule);
        when(conciergeService.getAllActiveConcierges()).thenReturn(List.of());

        // When & Then
        mockMvc.perform(get("/schedule"))
                .andExpect(status().isOk())
                .andExpect(view().name("schedule/calendar"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldAssignShift() throws Exception {
        // Given
        ShiftAssignmentDto shift = ShiftAssignmentDto.builder()
                .id(1L)
                .shiftDate(LocalDate.of(2025, 11, 15))
                .conciergeId(1L)
                .build();

        when(shiftSchedulingService.assignShift(any(ShiftAssignmentDto.class))).thenReturn(shift);

        // When & Then
        mockMvc.perform(post("/schedule/assign")
                        .with(csrf())
                        .param("date", "2025-11-15")
                        .param("conciergeId", "1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/schedule/2025/11"))
                .andExpect(flash().attributeExists("successMessage"));
    }

    @Test
    @WithMockUser(roles = "CONCIERGE")
    void shouldDenyAssignShiftForConciergeRole() throws Exception {
        mockMvc.perform(post("/schedule/assign")
                        .with(csrf())
                        .param("date", "2025-11-15")
                        .param("conciergeId", "1"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldRemoveShift() throws Exception {
        mockMvc.perform(post("/schedule/remove")
                        .with(csrf())
                        .param("date", "2025-11-15"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/schedule/2025/11"))
                .andExpect(flash().attributeExists("successMessage"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldNavigateToToday() throws Exception {
        LocalDate today = LocalDate.now();

        mockMvc.perform(get("/schedule/today"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/schedule/" + today.getYear() + "/" + today.getMonthValue()));
    }
}