package com.danielabgaryan.mediamatch.dto;

import com.danielabgaryan.mediamatch.model.Status;
import jakarta.validation.constraints.NotNull;

public class CreateUserMediaRequest {
    @NotNull(message = "User ID is required")
    private Long userId;

    @NotNull(message = "Media ID is required")
    private Long mediaId;

    @NotNull(message = "Status is required")
    private Status status;

    public CreateUserMediaRequest(Long userId, Long mediaId, Status status) {
        this.userId = userId;
        this.mediaId = mediaId;
        this.status = status;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getMediaId() {
        return mediaId;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }
}
