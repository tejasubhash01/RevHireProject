package com.revhire.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.revhire.controller.jobseeker.JobSeekerController;
import com.revhire.dto.jobseeker.*;
import com.revhire.entity.enums.EmploymentStatus;
import com.revhire.security.JwtAuthFilter;
import com.revhire.security.JwtService;
import com.revhire.service.JobSeekerService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(JobSeekerController.class)
@AutoConfigureMockMvc(addFilters = false)
 public class JobSeekerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private JobSeekerService jobSeekerService;

    @MockBean
    private JwtAuthFilter jwtAuthFilter;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private UserDetailsService userDetailsService;

    @Test
    void shouldCreateProfile() throws Exception {
        CreateJobSeekerProfileRequest request = new CreateJobSeekerProfileRequest();
        request.setHeadline("Java Developer");
        request.setEmploymentStatus(EmploymentStatus.EMPLOYED);

        JobSeekerProfileDto response = new JobSeekerProfileDto();
        response.setId(1L);
        response.setHeadline("Java Developer");

        Mockito.when(jobSeekerService.createProfile(any(), any()))
                .thenReturn(response);

        mockMvc.perform(post("/api/v1/jobseekers/profile")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Profile created successfully"))
                .andExpect(jsonPath("$.data.id").value(1L));
    }

    @Test
    void shouldGetMyProfile() throws Exception {
        JobSeekerProfileDto response = new JobSeekerProfileDto();
        response.setId(1L);

        Mockito.when(jobSeekerService.getMyProfile(any()))
                .thenReturn(response);

        mockMvc.perform(get("/api/v1/jobseekers/profile/me"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Profile retrieved"))
                .andExpect(jsonPath("$.data.id").value(1L));
    }

    @Test
    void shouldUpdateProfile() throws Exception {
        CreateJobSeekerProfileRequest request = new CreateJobSeekerProfileRequest();
        request.setHeadline("Updated");

        JobSeekerProfileDto response = new JobSeekerProfileDto();
        response.setId(1L);
        response.setHeadline("Updated");

        Mockito.when(jobSeekerService.updateProfile(any(), any()))
                .thenReturn(response);

        mockMvc.perform(put("/api/v1/jobseekers/profile")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Profile updated"))
                .andExpect(jsonPath("$.data.headline").value("Updated"));
    }

    @Test
    void shouldCreateOrUpdateResumeText() throws Exception {
        UpdateResumeTextRequest request = new UpdateResumeTextRequest();
        request.setObjective("Looking for backend role");

        ResumeTextDto response = new ResumeTextDto();
        response.setId(1L);
        response.setObjective("Looking for backend role");

        Mockito.when(jobSeekerService.createOrUpdateResumeText(any(), any()))
                .thenReturn(response);

        mockMvc.perform(post("/api/v1/jobseekers/resume/text")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Resume text saved"))
                .andExpect(jsonPath("$.data.id").value(1L));
    }

    @Test
    void shouldGetMyResumeText() throws Exception {
        ResumeTextDto response = new ResumeTextDto();
        response.setId(1L);

        Mockito.when(jobSeekerService.getMyResumeText(any()))
                .thenReturn(response);

        mockMvc.perform(get("/api/v1/jobseekers/resume/text/me"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Resume text retrieved"))
                .andExpect(jsonPath("$.data.id").value(1L));
    }

    @Test
    void shouldUploadResumeFile() throws Exception {
        MockMultipartFile file =
                new MockMultipartFile("file", "resume.pdf",
                        MediaType.APPLICATION_PDF_VALUE,
                        "test data".getBytes());

        ResumeFileDto response = new ResumeFileDto();
        response.setId(1L);
        response.setFileName("resume.pdf");
        response.setUploadDate(LocalDateTime.now());

        Mockito.when(jobSeekerService.uploadResumeFile(any(), any()))
                .thenReturn(response);

        mockMvc.perform(multipart("/api/v1/jobseekers/resume/file")
                        .file(file))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Resume file uploaded"))
                .andExpect(jsonPath("$.data.fileName").value("resume.pdf"));
    }

    @Test
    void shouldGetMyResumeFile() throws Exception {
        ResumeFileDto response = new ResumeFileDto();
        response.setId(1L);
        response.setFileName("resume.pdf");

        Mockito.when(jobSeekerService.getMyResumeFile(any()))
                .thenReturn(response);

        mockMvc.perform(get("/api/v1/jobseekers/resume/file/me"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Resume file info retrieved"))
                .andExpect(jsonPath("$.data.fileName").value("resume.pdf"));
    }
}