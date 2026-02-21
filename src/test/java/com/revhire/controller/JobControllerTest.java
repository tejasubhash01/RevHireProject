package com.revhire.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.revhire.controller.jobs.JobController;
import com.revhire.dto.jobs.*;
import com.revhire.entity.enums.JobType;
import com.revhire.security.JwtAuthFilter;
import com.revhire.security.JwtService;
import com.revhire.service.JobService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(JobController.class)
@AutoConfigureMockMvc(addFilters = false)
 public class JobControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private JobService jobService;

    @MockBean
    private JwtAuthFilter jwtAuthFilter;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private UserDetailsService userDetailsService;

    @Test
    void shouldCreateJobPost() throws Exception {
        CreateJobPostRequest request = new CreateJobPostRequest();
        request.setTitle("Java Developer");
        request.setDescription("Spring Boot Developer");
        request.setJobType(JobType.FULL_TIME);

        JobPostDto response = new JobPostDto();
        response.setId(1L);
        response.setTitle("Java Developer");

        Mockito.when(jobService.createJobPost(any(), any()))
                .thenReturn(response);

        mockMvc.perform(post("/api/v1/jobs")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Job post created"))
                .andExpect(jsonPath("$.data.id").value(1L));
    }

    @Test
    void shouldGetJobById() throws Exception {
        JobPostDto response = new JobPostDto();
        response.setId(1L);
        response.setTitle("Java Developer");

        Mockito.when(jobService.getJobPostById(1L))
                .thenReturn(response);

        mockMvc.perform(get("/api/v1/jobs/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Job post retrieved"))
                .andExpect(jsonPath("$.data.id").value(1L));
    }

    @Test
    void shouldUpdateJobPost() throws Exception {
        UpdateJobPostRequest request = new UpdateJobPostRequest();
        request.setTitle("Updated Title");

        JobPostDto response = new JobPostDto();
        response.setId(1L);
        response.setTitle("Updated Title");

        Mockito.when(jobService.updateJobPost(any(), eq(1L), any()))
                .thenReturn(response);

        mockMvc.perform(put("/api/v1/jobs/1")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Job post updated"))
                .andExpect(jsonPath("$.data.title").value("Updated Title"));
    }

    @Test
    void shouldDeleteJobPost() throws Exception {
        mockMvc.perform(delete("/api/v1/jobs/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Job post deleted"));

        Mockito.verify(jobService).deleteJobPost(any(), eq(1L));
    }

    @Test
    void shouldCloseJobPost() throws Exception {
        mockMvc.perform(patch("/api/v1/jobs/1/close"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Job post closed"));

        Mockito.verify(jobService).closeJobPost(any(), eq(1L));
    }

    @Test
    void shouldReopenJobPost() throws Exception {
        mockMvc.perform(patch("/api/v1/jobs/1/reopen"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Job post reopened"));

        Mockito.verify(jobService).reopenJobPost(any(), eq(1L));
    }

    @Test
    void shouldMarkJobFilled() throws Exception {
        mockMvc.perform(patch("/api/v1/jobs/1/mark-filled"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Job marked as filled"));

        Mockito.verify(jobService).markJobFilled(any(), eq(1L));
    }

    @Test
    void shouldSearchJobs() throws Exception {
        JobPostDto dto = new JobPostDto();
        dto.setId(1L);
        dto.setTitle("Java Developer");

        Mockito.when(jobService.searchJobs(any(), any()))
                .thenReturn(new PageImpl<>(List.of(dto), PageRequest.of(0, 20), 1));

        mockMvc.perform(get("/api/v1/jobs/search"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Jobs retrieved"))
                .andExpect(jsonPath("$.data.content[0].id").value(1L));
    }

    @Test
    void shouldGetMyJobs() throws Exception {
        JobPostDto dto = new JobPostDto();
        dto.setId(1L);
        dto.setTitle("My Job");

        Mockito.when(jobService.getMyJobPosts(any(), any()))
                .thenReturn(new PageImpl<>(List.of(dto), PageRequest.of(0, 20), 1));

        mockMvc.perform(get("/api/v1/jobs/my"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Your job posts retrieved"))
                .andExpect(jsonPath("$.data.content[0].id").value(1L));
    }
}