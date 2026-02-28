package com.revhire.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.revhire.controller.favourites.FavouriteController;
import com.revhire.dto.favourites.FavouriteJobResponse;
import com.revhire.security.JwtAuthFilter;
import com.revhire.security.JwtService;
import com.revhire.service.FavouriteService;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(FavouriteController.class)
@AutoConfigureMockMvc(addFilters = false)
class FavouriteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private FavouriteService favouriteService;

    @MockBean
    private JwtAuthFilter jwtAuthFilter;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private UserDetailsService userDetailsService;

    @Test
    void shouldAddFavourite() throws Exception {
        mockMvc.perform(post("/api/v1/favourites/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Job added to favourites"))
                .andExpect(jsonPath("$.data").doesNotExist());

        Mockito.verify(favouriteService).addFavourite(any(), eq(1L));
    }

    @Test
    void shouldGetMyFavourites() throws Exception {
        FavouriteJobResponse response = new FavouriteJobResponse();
        response.setId(1L);

        Mockito.when(favouriteService.getMyFavourites(any()))
                .thenReturn(List.of(response));

        mockMvc.perform(get("/api/v1/favourites/my"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Favourites retrieved"))
                .andExpect(jsonPath("$.data[0].id").value(1L));
    }

    @Test
    void shouldRemoveFavourite() throws Exception {
        mockMvc.perform(delete("/api/v1/favourites/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Job removed from favourites"))
                .andExpect(jsonPath("$.data").doesNotExist());

        Mockito.verify(favouriteService).removeFavourite(any(), eq(1L));
    }
}