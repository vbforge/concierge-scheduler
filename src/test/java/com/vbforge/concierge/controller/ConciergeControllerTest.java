package com.vbforge.concierge.controller;

import com.vbforge.concierge.dto.ConciergeDto;
import com.vbforge.concierge.enums.ColorType;
import com.vbforge.concierge.service.ConciergeService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Controller tests for ConciergeController
 */
//@WebMvcTest(ConciergeController.class)

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ConciergeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ConciergeService conciergeService;

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldListConcierges() throws Exception {
        // Given
        ConciergeDto alice = ConciergeDto.builder()
                .id(1L)
                .name("Alice")
                .color(ColorType.BLUE)
                .active(true)
                .build();

        when(conciergeService.getAllConcierges()).thenReturn(List.of(alice));

        // When & Then
        mockMvc.perform(get("/concierges"))
                .andExpect(status().isOk())
                .andExpect(view().name("concierge/list"))
                .andExpect(model().attributeExists("concierges"))
                .andExpect(model().attribute("activePage", "concierges"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldShowCreateForm() throws Exception {
        mockMvc.perform(get("/concierges/new"))
                .andExpect(status().isOk())
                .andExpect(view().name("concierge/form"))
                .andExpect(model().attributeExists("concierge"))
                .andExpect(model().attributeExists("colors"))
                .andExpect(model().attribute("isEdit", false));
    }

    @Test
    @WithMockUser(roles = "CONCIERGE")
    void shouldDenyCreateFormForConciergeRole() throws Exception {
        mockMvc.perform(get("/concierges/new"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldCreateConcierge() throws Exception {
        // Given
        ConciergeDto alice = ConciergeDto.builder()
                .id(1L)
                .name("Alice")
                .color(ColorType.BLUE)
                .active(true)
                .build();

        when(conciergeService.createConcierge(any(ConciergeDto.class))).thenReturn(alice);

        // When & Then
        mockMvc.perform(post("/concierges")
                        .with(csrf())
                        .param("name", "Alice")
                        .param("color", "BLUE")
                        .param("active", "true"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/concierges"))
                .andExpect(flash().attributeExists("successMessage"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldShowEditForm() throws Exception {
        // Given
        ConciergeDto alice = ConciergeDto.builder()
                .id(1L)
                .name("Alice")
                .color(ColorType.BLUE)
                .active(true)
                .build();

        when(conciergeService.getConciergeById(1L)).thenReturn(alice);

        // When & Then
        mockMvc.perform(get("/concierges/1/edit"))
                .andExpect(status().isOk())
                .andExpect(view().name("concierge/form"))
                .andExpect(model().attributeExists("concierge"))
                .andExpect(model().attribute("isEdit", true));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldUpdateConcierge() throws Exception {
        // Given
        ConciergeDto updated = ConciergeDto.builder()
                .id(1L)
                .name("Alice Updated")
                .color(ColorType.GREEN)
                .active(true)
                .build();

        when(conciergeService.updateConcierge(eq(1L), any(ConciergeDto.class))).thenReturn(updated);

        // When & Then
        mockMvc.perform(post("/concierges/1")
                        .with(csrf())
                        .param("name", "Alice Updated")
                        .param("color", "GREEN")
                        .param("active", "true"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/concierges"))
                .andExpect(flash().attributeExists("successMessage"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldDeleteConcierge() throws Exception {
        // Given
        ConciergeDto alice = ConciergeDto.builder()
                .id(1L)
                .name("Alice")
                .color(ColorType.BLUE)
                .active(true)
                .build();

        when(conciergeService.getConciergeById(1L)).thenReturn(alice);

        // When & Then
        mockMvc.perform(post("/concierges/1/delete")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/concierges"))
                .andExpect(flash().attributeExists("successMessage"));
    }

    @Test
    @WithMockUser(roles = "CONCIERGE")
    void shouldDenyDeleteForConciergeRole() throws Exception {
        mockMvc.perform(post("/concierges/1/delete")
                        .with(csrf()))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldActivateConcierge() throws Exception {
        mockMvc.perform(post("/concierges/1/activate")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/concierges"))
                .andExpect(flash().attributeExists("successMessage"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldDeactivateConcierge() throws Exception {
        mockMvc.perform(post("/concierges/1/deactivate")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/concierges"))
                .andExpect(flash().attributeExists("successMessage"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldShowValidationErrors() throws Exception {
        mockMvc.perform(post("/concierges")
                        .with(csrf())
                        .param("name", "") // Empty name should fail validation
                        .param("color", "BLUE"))
                .andExpect(status().isOk())
                .andExpect(view().name("concierge/form"))
                .andExpect(model().hasErrors());
    }
}