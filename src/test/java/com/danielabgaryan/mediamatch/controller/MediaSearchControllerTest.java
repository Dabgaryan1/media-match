package com.danielabgaryan.mediamatch.controller;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.context.annotation.Import;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.BadJwtException;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import com.danielabgaryan.mediamatch.config.SecurityConfig;
import com.danielabgaryan.mediamatch.dto.MediaSearchResponse;
import com.danielabgaryan.mediamatch.exception.ExternalServiceException;
import com.danielabgaryan.mediamatch.exception.InvalidRequestException;
import com.danielabgaryan.mediamatch.service.TmdbService;

@WebMvcTest(MediaSearchController.class)
@Import({SecurityConfig.class, MediaSearchControllerTest.SecurityTestConfig.class})
class MediaSearchControllerTest {
    @TestConfiguration
    @EnableWebSecurity
    static class SecurityTestConfig {}

    @Autowired private MockMvc mockMvc;
    @MockitoBean private TmdbService service;
    @MockitoBean private JwtDecoder decoder;

    private void authenticate() {
        when(decoder.decode("test-jwt")).thenReturn(Jwt.withTokenValue("test-jwt")
            .header("alg", "HS256").subject("user@example.com").build());
    }

    @Test
    void authenticatedSearch_returnsResultsWithDefaultPage() throws Exception {
        authenticate();
        when(service.searchMovies("Inception", 1)).thenReturn(new MediaSearchResponse(1, 0, 0, List.of()));
        mockMvc.perform(get("/media/search").param("query", "Inception")
                .header("Authorization", "Bearer test-jwt"))
            .andExpect(status().isOk()).andExpect(jsonPath("$.page").value(1))
            .andExpect(jsonPath("$.results").isEmpty());
        verify(service).searchMovies("Inception", 1);
    }

    @Test
    void missingOrInvalidJwt_returnsUnauthorized() throws Exception {
        mockMvc.perform(get("/media/search").param("query", "movie")).andExpect(status().isUnauthorized());
        when(decoder.decode("invalid")).thenThrow(new BadJwtException("Invalid token"));
        mockMvc.perform(get("/media/search").param("query", "movie")
            .header("Authorization", "Bearer invalid")).andExpect(status().isUnauthorized());
        verifyNoInteractions(service);
    }

    @Test
    void invalidQuery_returnsBadRequest() throws Exception {
        authenticate();
        when(service.searchMovies(" ", 1)).thenThrow(new InvalidRequestException("Search query is required"));
        mockMvc.perform(get("/media/search").param("query", " ")
            .header("Authorization", "Bearer test-jwt")).andExpect(status().isBadRequest());
    }

    @Test
    void missingQueryOrNonNumericPage_returnsBadRequest() throws Exception {
        authenticate();
        mockMvc.perform(get("/media/search").header("Authorization", "Bearer test-jwt"))
            .andExpect(status().isBadRequest());
        mockMvc.perform(get("/media/search").param("query", "movie").param("page", "abc")
            .header("Authorization", "Bearer test-jwt")).andExpect(status().isBadRequest());
        verifyNoInteractions(service);
    }

    @Test
    void providerFailure_returnsServiceUnavailable() throws Exception {
        authenticate();
        when(service.searchMovies("movie", 2)).thenThrow(new ExternalServiceException("Media search is temporarily unavailable"));
        mockMvc.perform(get("/media/search").param("query", "movie").param("page", "2")
            .header("Authorization", "Bearer test-jwt"))
            .andExpect(status().isServiceUnavailable())
            .andExpect(content().string("Media search is temporarily unavailable"));
    }
}
