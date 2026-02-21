package com.revhire.controller;

import com.revhire.controller.notifications.NotificationController;
import com.revhire.dto.notifications.NotificationResponse;
import com.revhire.security.JwtAuthFilter;
import com.revhire.security.JwtService;
import com.revhire.service.NotificationService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(NotificationController.class)
@AutoConfigureMockMvc(addFilters = false)
 public class NotificationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private NotificationService notificationService;

    @MockBean
    private JwtAuthFilter jwtAuthFilter;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private UserDetailsService userDetailsService;

    @Test
    void shouldGetMyNotifications() throws Exception {
        NotificationResponse response = new NotificationResponse();
        response.setId(1L);
        response.setMessage("Application shortlisted");
        response.setIsRead(false);
        response.setLink("/jobs/1");
        response.setCreatedAt(LocalDateTime.now());

        Mockito.when(notificationService.getMyNotifications(any()))
                .thenReturn(List.of(response));

        mockMvc.perform(get("/api/v1/notifications/my"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Notifications retrieved"))
                .andExpect(jsonPath("$.data[0].id").value(1L));
    }

    @Test
    void shouldMarkAsRead() throws Exception {
        mockMvc.perform(patch("/api/v1/notifications/1/read"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Notification marked as read"));

        Mockito.verify(notificationService).markAsRead(any(), eq(1L));
    }

    @Test
    void shouldMarkAllAsRead() throws Exception {
        mockMvc.perform(patch("/api/v1/notifications/read-all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("All notifications marked as read"));

        Mockito.verify(notificationService).markAllAsRead(any());
    }
}