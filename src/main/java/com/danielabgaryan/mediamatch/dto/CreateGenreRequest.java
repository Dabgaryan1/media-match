package com.danielabgaryan.mediamatch.dto;
import jakarta.validation.constraints.NotBlank;

public class CreateGenreRequest {
    @NotBlank(message = "Name is required")
    private String name;

    public CreateGenreRequest(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
