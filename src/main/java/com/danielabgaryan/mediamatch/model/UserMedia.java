package com.danielabgaryan.mediamatch.model;

import java.time.LocalDateTime;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.PrePersist;

@Entity
@Table(
    name = "user_media",
    //prevent duplicate entries for the same user and media combination
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"user_id", "media_id"})
    }
)
public class UserMedia {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(optional = false)
    @JoinColumn(name = "media_id", nullable = false)
    private Media media;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status;

    @Min(1)
    @Max(5)
    private Integer rating;

    @Column(nullable = false)
    private boolean favorite;

    @Column(nullable = false, updatable = false)
    private LocalDateTime addedAt;

    public UserMedia() {
    }

    public UserMedia(User user, Media media, Status status, Integer rating, boolean favorite) {
        this.user = user;
        this.media = media;
        this.status = status;
        this.rating = rating;
        this.favorite = favorite;
    }

    public Long getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Media getMedia() {
        return media;
    }

    public void setMedia(Media media) {
        this.media = media;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public boolean getFavorite() {
        return favorite;
    }

    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
    }

    public LocalDateTime getAddedAt() {
        return addedAt;
    }

    @PrePersist
    protected void onCreate() {
        if (addedAt == null) {
            addedAt = LocalDateTime.now();
        }
    }
}
