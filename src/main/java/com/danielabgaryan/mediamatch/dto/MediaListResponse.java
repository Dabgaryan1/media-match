package com.danielabgaryan.mediamatch.dto;

public class MediaListResponse {
    private Long id;
    private String name;
    private String description;
    private UserResponse user;

    public MediaListResponse(Long id, String name, String description, UserResponse user) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.user = user;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public UserResponse getUser() {
        return user;
    }

    public void setUser(UserResponse user) {
        this.user = user;
    }
}
