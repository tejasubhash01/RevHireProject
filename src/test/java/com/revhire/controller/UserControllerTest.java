package com.revhire.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.revhire.controller.users.UserController;
import com.revhire.dto.user.ChangePasswordRequest;
import com.revhire.dto.user.UpdateUserRequest;
import com.revhire.dto.user.UserResponse;
import com.revhire.entity.enums.Role;
import com.revhire.security.JwtAuthFilter;
import com.revhire.security.JwtService;
import com.revhire.service.UserService;
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

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    @MockBean
    private JwtAuthFilter jwtAuthFilter;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private UserDetailsService userDetailsService;

    @Test
    void shouldGetCurrentUser() throws Exception {
        UserResponse response = new UserResponse();
        response.setId(1L);
        response.setEmail("test@example.com");
        response.setName("Test User");
        response.setRole(Role.JOB_SEEKER);

        Mockito.when(userService.getCurrentUser(any()))
                .thenReturn(response);

        mockMvc.perform(get("/api/v1/users/me"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("User profile retrieved"))
                .andExpect(jsonPath("$.data.id").value(1L));
    }

    @Test
    void shouldUpdateUser() throws Exception {
        UpdateUserRequest request = new UpdateUserRequest();
        request.setName("Updated Name");

        UserResponse response = new UserResponse();
        response.setId(1L);
        response.setName("Updated Name");

        Mockito.when(userService.updateUser(any(), any()))
                .thenReturn(response);

        mockMvc.perform(put("/api/v1/users/me")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("User profile updated"))
                .andExpect(jsonPath("$.data.name").value("Updated Name"));
    }

    @Test
    void shouldChangePassword() throws Exception {
        ChangePasswordRequest request = new ChangePasswordRequest();
        request.setOldPassword("oldPass");
        request.setNewPassword("newPass");

        mockMvc.perform(post("/api/v1/users/change-password")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Password changed successfully"));
    }

    @Test
    void shouldDeactivateAccount() throws Exception {
        mockMvc.perform(delete("/api/v1/users/me"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Account deactivated"));
    }

    @Test
    void shouldFailChangePasswordWhenFieldsMissing() throws Exception {
        ChangePasswordRequest request = new ChangePasswordRequest();

        mockMvc.perform(post("/api/v1/users/change-password")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}