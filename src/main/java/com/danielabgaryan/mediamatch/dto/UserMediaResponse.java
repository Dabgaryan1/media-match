package com.danielabgaryan.mediamatch.dto;

import com.danielabgaryan.mediamatch.model.Status;
import java.time.LocalDateTime;

import com.danielabgaryan.mediamatch.model.Media;

public class UserMediaResponse {
    private Long id;
    private UserResponse user;
    private Media media;
    private Status status;
    private Integer rating;
    private boolean favorite;
    private LocalDateTime addedAt;

    public UserMediaResponse(Long id, UserResponse user, 
        Media media, Status status, Integer rating, boolean favorite,
        LocalDateTime addedAt
    ) {
        this.id = id;
        this.user = user;
        this.media = media;
        this.status = status;
        this.rating = rating;
        this.favorite = favorite;
        this.addedAt = addedAt;
    }

    public Long getId() {
        return id;
    }

    public UserResponse getUser() {
        return user;
    }

    public Media getMedia() {
        return media;
    }

    public Status getStatus() {
        return status;
    }

    public Integer getRating() {
        return rating;
    }

    public boolean getFavorite() {
        return favorite;
    }

    public LocalDateTime getAddedAt() {
        return addedAt;
    }
}
