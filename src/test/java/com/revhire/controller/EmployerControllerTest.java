package com.revhire.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.revhire.controller.employer.EmployerController;
import com.revhire.dto.employer.*;
import com.revhire.security.JwtAuthFilter;
import com.revhire.security.JwtService;
import com.revhire.service.EmployerService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(EmployerController.class)
@AutoConfigureMockMvc(addFilters = false)
class EmployerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private EmployerService employerService;

    @MockBean
    private JwtAuthFilter jwtAuthFilter;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private UserDetailsService userDetailsService;


    @Test
    void shouldCreateEmployerProfile() throws Exception {

        CreateEmployerProfileRequest request = new CreateEmployerProfileRequest();
        request.setCompanyName("RevHire Pvt Ltd");
        request.setIndustry("IT");

        EmployerProfileDto response = new EmployerProfileDto();
        response.setId(1L);
        response.setCompanyName("RevHire Pvt Ltd");

        Mockito.when(employerService.createProfile(any(), any()))
                .thenReturn(response);

        mockMvc.perform(post("/api/v1/employers/profile")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Employer profile created"))
                .andExpect(jsonPath("$.data.companyName").value("RevHire Pvt Ltd"));
    }


    @Test
    void shouldGetMyProfile() throws Exception {

        EmployerProfileDto profile = new EmployerProfileDto();
        profile.setId(1L);
        profile.setCompanyName("RevHire Pvt Ltd");

        Mockito.when(employerService.getMyProfile(any()))
                .thenReturn(profile);

        mockMvc.perform(get("/api/v1/employers/profile/me"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Profile retrieved"))
                .andExpect(jsonPath("$.data.companyName").value("RevHire Pvt Ltd"));
    }


    @Test
    void shouldUpdateProfile() throws Exception {

        CreateEmployerProfileRequest request = new CreateEmployerProfileRequest();
        request.setCompanyName("Updated Company");

        EmployerProfileDto response = new EmployerProfileDto();
        response.setId(1L);
        response.setCompanyName("Updated Company");

        Mockito.when(employerService.updateProfile(any(), any()))
                .thenReturn(response);

        mockMvc.perform(put("/api/v1/employers/profile")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Profile updated"))
                .andExpect(jsonPath("$.data.companyName").value("Updated Company"));
    }


    @Test
    void shouldGetDashboardStats() throws Exception {

        EmployerDashboardDto dashboard = new EmployerDashboardDto();
        dashboard.setTotalJobs(10L);
        dashboard.setActiveJobs(5L);
        dashboard.setTotalApplications(100L);
        dashboard.setPendingReviews(20L);

        Mockito.when(employerService.getDashboardStats(any()))
                .thenReturn(dashboard);

        mockMvc.perform(get("/api/v1/employers/dashboard"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Dashboard stats"))
                .andExpect(jsonPath("$.data.totalJobs").value(10));
    }


    @Test
    void shouldFailWhenCompanyNameIsBlank() throws Exception {

        CreateEmployerProfileRequest request = new CreateEmployerProfileRequest();
        request.setCompanyName(""); // @NotBlank violation

        mockMvc.perform(post("/api/v1/employers/profile")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}