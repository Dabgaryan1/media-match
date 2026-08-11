package com.danielabgaryan.mediamatch.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class CreateCommentRequest {

    @NotNull(message = "User ID is required")
    private Long userId;

    @NotNull(message = "Media List ID is required")
    private Long mediaListId;

    @NotBlank(message = "Comment is required")
    @Size(max = 300, message = "Comment must have 200 characters or less")
    private String content;

    public CreateCommentRequest(Long userId, Long mediaListId, String content) {
        this.userId = userId;
        this.mediaListId = mediaListId;
        this.content = content;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getMediaListId() {
        return mediaListId;
    }

    public void setMediaListId(Long mediaListId) {
        this.mediaListId = mediaListId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
