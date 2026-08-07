package com.danielabgaryan.mediamatch.dto;

public class CreateMediaListRequest {
    private Long userId;
    private String name;
    private String description;

    public CreateMediaListRequest(Long userId, String name, String description) {
        this.userId = userId;
        this.name = name;
        this.description = description;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;

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
}
