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

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public UserResponse getUser() {
        return user;
    }
}
