package com.danielabgaryan.mediamatch.controller;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.danielabgaryan.mediamatch.exception.ResourceNotFoundException;
import com.danielabgaryan.mediamatch.model.User;
import com.danielabgaryan.mediamatch.service.UserService;

@WebMvcTest(UserController.class)
public class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @Test
    void createUser_whenRequestIsValid_returnsUserResponse() throws Exception {
        User user = mockUser(1L, "testuser", "user@example.com");
        when(userService.createUser("testuser", "user@example.com", "password123"))
            .thenReturn(user);

        mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "userName": "testuser",
                        "email": "user@example.com",
                        "password": "password123"
                    }
                    """))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.username").value("testuser"))
            .andExpect(jsonPath("$.email").value("user@example.com"))
            .andExpect(jsonPath("$.bio").value("Test bio"))
            .andExpect(jsonPath("$.profilePictureUrl").value("picture.jpg"))
            .andExpect(jsonPath("$.passwordHash").doesNotExist());

        verify(userService).createUser("testuser", "user@example.com", "password123");
    }

    @Test
    void createUser_whenRequestIsInvalid_returnsValidationErrors() throws Exception {
        mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "userName": "ab",
                        "email": "invalid-email",
                        "password": "short"
                    }
                    """))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.userName").value("Username must be between 3 and 30 characters"))
            .andExpect(jsonPath("$.email").value("Email must be valid"))
            .andExpect(jsonPath("$.password").value("Password must be at least 8 characters"));

        verify(userService, never()).createUser(anyString(), anyString(), anyString());
    }

    @Test
    void getUserById_whenUserExists_returnsUserResponse() throws Exception {
        User user = mockUser(1L, "testuser", "user@example.com");
        when(userService.getUserById(1L)).thenReturn(user);

        mockMvc.perform(get("/users/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.username").value("testuser"))
            .andExpect(jsonPath("$.email").value("user@example.com"))
            .andExpect(jsonPath("$.passwordHash").doesNotExist());

        verify(userService).getUserById(1L);
    }

    @Test
    void getUserById_whenUserDoesNotExist_returnsNotFound() throws Exception {
        when(userService.getUserById(1L)).thenThrow(new ResourceNotFoundException("User not found"));

        mockMvc.perform(get("/users/1"))
            .andExpect(status().isNotFound())
            .andExpect(content().string("User not found"));

        verify(userService).getUserById(1L);
    }

    @Test
    void getUserByUsername_whenUserExists_returnsUserResponse() throws Exception {
        User user = mockUser(1L, "testuser", "user@example.com");
        when(userService.getUserByUsername("testuser"))
            .thenReturn(user);

        mockMvc.perform(get("/users/username/testuser"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.username").value("testuser"));

        verify(userService).getUserByUsername("testuser");
    }

    @Test
    void getUserByEmail_whenEmailMatchesAuthenticatedUser_returnsUserResponse() throws Exception {
        Authentication authentication = authentication("user@example.com");
        User user = mockUser(1L, "testuser", "user@example.com");
        when(userService.getUserByEmail("user@example.com"))
            .thenReturn(user);

        mockMvc.perform(get("/users/email/user@example.com").principal(authentication))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.email").value("user@example.com"));

        verify(userService).getUserByEmail("user@example.com");
    }

    @Test
    void getUserByEmail_whenEmailDoesNotMatchAuthenticatedUser_returnsForbidden() throws Exception {
        Authentication authentication = authentication("other@example.com");

        mockMvc.perform(get("/users/email/user@example.com").principal(authentication))
            .andExpect(status().isForbidden())
            .andExpect(content().string("You cannot access this user's email lookup"));

        verify(userService, never()).getUserByEmail(anyString());
    }

    @Test
    void updateUser_whenRequestIsValid_returnsUpdatedUserResponse() throws Exception {
        Authentication authentication = authentication("user@example.com");
        User updatedUser = mockUser(1L, "updateduser", "updated@example.com");
        when(userService.updateUser(1L, "user@example.com", "updateduser", "updated@example.com"))
            .thenReturn(updatedUser);

        mockMvc.perform(put("/users/1")
                .principal(authentication)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "username": "updateduser",
                        "email": "updated@example.com"
                    }
                    """))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.username").value("updateduser"))
            .andExpect(jsonPath("$.email").value("updated@example.com"))
            .andExpect(jsonPath("$.passwordHash").doesNotExist());

        verify(userService).updateUser(1L, "user@example.com", "updateduser", "updated@example.com");
    }

    @Test
    void updateUser_whenRequestIsInvalid_returnsValidationErrors() throws Exception {
        mockMvc.perform(put("/users/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "username": "ab",
                        "email": "invalid-email"
                    }
                    """))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.username").value("size must be between 3 and 30"))
            .andExpect(jsonPath("$.email").value("must be a well-formed email address"));

        verify(userService, never()).updateUser(anyLong(), anyString(), anyString(), anyString());
    }

    @Test
    void updatePassword_whenRequestIsValid_returnsUserResponse() throws Exception {
        Authentication authentication = authentication("user@example.com");
        User user = mockUser(1L, "testuser", "user@example.com");
        when(userService.updatePassword(1L, "user@example.com", "oldPassword", "newPassword123"))
            .thenReturn(user);

        mockMvc.perform(put("/users/1/password")
                .principal(authentication)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "currentPassword": "oldPassword",
                        "newPassword": "newPassword123"
                    }
                    """))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.email").value("user@example.com"))
            .andExpect(jsonPath("$.passwordHash").doesNotExist());

        verify(userService).updatePassword(1L, "user@example.com", "oldPassword", "newPassword123");
    }

    @Test
    void updatePassword_whenRequestIsInvalid_returnsValidationErrors() throws Exception {
        mockMvc.perform(put("/users/1/password")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "currentPassword": "",
                        "newPassword": "short"
                    }
                    """))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.currentPassword").value("Current password is required"))
            .andExpect(jsonPath("$.newPassword").value("New password must be at least 8 characters"));

        verify(userService, never()).updatePassword(anyLong(), anyString(), anyString(), anyString());
    }

    @Test
    void deleteUser_passesAuthenticatedEmailToService() throws Exception {
        Authentication authentication = authentication("user@example.com");

        mockMvc.perform(delete("/users/1").principal(authentication))
            .andExpect(status().isOk())
            .andExpect(content().string(""));

        verify(userService).deleteUser(1L, "user@example.com");
    }

    private Authentication authentication(String email) {
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn(email);
        return authentication;
    }

    private User mockUser(Long id, String username, String email) {
        User user = mock(User.class);
        when(user.getId()).thenReturn(id);
        when(user.getUsername()).thenReturn(username);
        when(user.getEmail()).thenReturn(email);
        when(user.getBio()).thenReturn("Test bio");
        when(user.getProfilePictureUrl()).thenReturn("picture.jpg");
        return user;
    }
}
