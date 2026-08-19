package com.danielabgaryan.mediamatch.dto;

import java.time.LocalDateTime;

public class CommentResponse {
    private Long id;
    private UserResponse user;
    private MediaListResponse mediaList;
    private String content;
    private LocalDateTime createdAt;

    public CommentResponse(Long id, UserResponse user, MediaListResponse mediaList,
        String content, LocalDateTime createdAt
    ) {
        this.id = id;
        this.user = user;
        this.mediaList = mediaList;
        this.content = content;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public UserResponse getUser() {
        return user;
    }

    public MediaListResponse getMediaList() {
        return mediaList;
    }

    public String getContent() {
        return content;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
