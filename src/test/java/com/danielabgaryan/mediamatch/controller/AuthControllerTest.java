package com.danielabgaryan.mediamatch.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import com.danielabgaryan.mediamatch.model.User;
import com.danielabgaryan.mediamatch.service.AuthService;
import com.danielabgaryan.mediamatch.service.JwtService;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import com.danielabgaryan.mediamatch.exception.InvalidRequestException;

@WebMvcTest(AuthController.class)
public class AuthControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuthService authService;

    @MockitoBean
    private JwtService jwtService;

    @Test
    void login_whenCredentialsAreCorrect_returnsLoggedUser() throws Exception {
        User user = mock(User.class);

        when(user.getId()).thenReturn(1L);
        when(user.getUsername()).thenReturn("testuser");

        when(authService.authenticate("user@example.com", "password123"))
            .thenReturn(user);
        
        when(jwtService.generateToken(user))
            .thenReturn("test-token");
        
        mockMvc.perform(post("/auth/login")
        .contentType(MediaType.APPLICATION_JSON)
        .content("""
            {
                "email": "user@example.com",
                "password": "password123"
            }
            """))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.token").value("test-token"))
        .andExpect(jsonPath("$.userId").value(1))
        .andExpect(jsonPath("$.username").value("testuser"));

        verify(authService).authenticate("user@example.com", "password123");
        verify(jwtService).generateToken(user);
    }

    @Test
    void login_whenCredentialsAreInvalid_returnsBadRequest() throws Exception {
        when(authService.authenticate("user@example.com", "wrong-password"))
            .thenThrow(new InvalidRequestException("Invalid email or password"));
        
        mockMvc.perform(post("/auth/login")
        .contentType(MediaType.APPLICATION_JSON)
        .content("""
            {
                "email": "user@example.com",
                "password": "wrong-password"
            }
            """))
        .andExpect(status().isBadRequest())
        .andExpect(content().string("Invalid email or password"));

        verify(jwtService, never()).generateToken(any(User.class));           
    }

    @Test
    void login_whenRequestIsInvalid_returnsValidationErrors() throws Exception {
        mockMvc.perform(post("/auth/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                    {
                        "email": "not-an-email",
                        "password": ""
                    }
                    """))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.email").value("Email must be valid"))
            .andExpect(jsonPath("$.password").value("Password is required"));
        
        verify(authService, never()).authenticate(anyString(), anyString());
    }
}