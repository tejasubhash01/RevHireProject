package com.revhire.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.revhire.controller.applications.ApplicationController;
import com.revhire.dto.applications.*;
import com.revhire.entity.enums.ApplicationStatus;
import com.revhire.security.JwtAuthFilter;
import com.revhire.security.JwtService;
import com.revhire.service.ApplicationService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ApplicationController.class)
@AutoConfigureMockMvc(addFilters = false)
class ApplicationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ApplicationService applicationService;

    @MockBean
    private JwtAuthFilter jwtAuthFilter;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private UserDetailsService userDetailsService;


    @Test
    void shouldApplyForJobSuccessfully() throws Exception {

        ApplyJobRequest request = new ApplyJobRequest();
        request.setJobPostId(1L);
        request.setCoverLetter("Interested");

        ApplicationResponse response = new ApplicationResponse();
        response.setId(1L);
        response.setStatus(ApplicationStatus.APPLIED);

        Mockito.when(applicationService.applyForJob(any(), any()))
                .thenReturn(response);

        mockMvc.perform(post("/api/v1/applications/apply")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Application submitted"))
                .andExpect(jsonPath("$.data.id").value(1L));
    }


    @Test
    void shouldGetMyApplications() throws Exception {

        ApplicationResponse response = new ApplicationResponse();
        response.setId(1L);
        response.setStatus(ApplicationStatus.APPLIED);

        Mockito.when(applicationService.getMyApplications(any()))
                .thenReturn(List.of(response));

        mockMvc.perform(get("/api/v1/applications/my"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Applications retrieved"))
                .andExpect(jsonPath("$.data[0].id").value(1L));
    }


    @Test
    void shouldWithdrawApplication() throws Exception {

        WithdrawApplicationRequest request = new WithdrawApplicationRequest();
        request.setReason("Changed mind");

        mockMvc.perform(patch("/api/v1/applications/1/withdraw")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Application withdrawn"));
    }


    @Test
    void shouldUpdateApplicationStatus() throws Exception {

        UpdateApplicationStatusRequest request = new UpdateApplicationStatusRequest();
        request.setStatus(ApplicationStatus.SHORTLISTED);

        ApplicationResponse response = new ApplicationResponse();
        response.setId(1L);
        response.setStatus(ApplicationStatus.SHORTLISTED);

        Mockito.when(applicationService.updateApplicationStatus(any(), eq(1L), any()))
                .thenReturn(response);

        mockMvc.perform(patch("/api/v1/applications/1/status")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Application status updated"))
                .andExpect(jsonPath("$.data.status").value("SHORTLISTED"));
    }


    @Test
    void shouldAddNote() throws Exception {

        NoteRequest request = new NoteRequest();
        request.setNote("Strong candidate");

        ApplicationResponse response = new ApplicationResponse();
        response.setId(1L);

        Mockito.when(applicationService.addNote(any(), eq(1L), any()))
                .thenReturn(response);

        mockMvc.perform(patch("/api/v1/applications/1/note")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Note added"));
    }
}