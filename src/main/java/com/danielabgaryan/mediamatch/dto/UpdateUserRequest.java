package com.danielabgaryan.mediamatch.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class UpdateUserRequest {
    @Size(min=3, max=30)
    @NotBlank(message = "Username is required")
    private String username;

    @Email
    @NotBlank(message = "Email is required")
    private String email;

    public UpdateUserRequest(String username, String email) {
        this.username = username;
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
