package com.danielabgaryan.mediamatch.dto;

public class CreateCommentRequest {
    private Long userId;
    private Long mediaListId;
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
