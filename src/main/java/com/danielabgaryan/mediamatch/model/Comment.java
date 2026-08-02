package com.danielabgaryan.mediamatch.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "comments")
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(optional = false)
    @JoinColumn(name = "media_list_id", nullable = false)
    private MediaList mediaList;

    @NotBlank
    @Size(max = 300)
    @Column(nullable = false, length = 300)
    private String content;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    protected Comment() {
    }

    public Comment(User user, MediaList mediaList, String content) {
        this.user = user;
        this.mediaList = mediaList;
        this.content = content;
    }

    public Long getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public MediaList getMediaList() {
        return mediaList;
    }

    public String getContent() {
        return content;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }
}
