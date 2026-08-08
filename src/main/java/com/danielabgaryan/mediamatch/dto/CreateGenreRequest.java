package com.danielabgaryan.mediamatch.dto;

public class CreateGenreRequest {
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
