package com.danielabgaryan.mediamatch.dto;

public class UserResponse {
    private Long id;
    private String username;
    private String email;
    private String bio;
    private String profilePictureUrl;

    public UserResponse(Long id, String username, String email, String bio, String profilePictureUrl) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.bio = bio;
        this.profilePictureUrl = profilePictureUrl;
    }

    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getBio() {
        return bio;
    }

    public String getProfilePictureUrl() {
        return profilePictureUrl;
    }
}
