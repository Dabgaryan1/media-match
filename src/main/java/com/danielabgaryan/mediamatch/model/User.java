package com.danielabgaryan.mediamatch.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "users")
public class User {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String passwordHash;

    private String bio;

    private String profilePictureUrl;

    protected User() {
    }

    public User(String email, String username, String passwordHash, String bio, String profilePictureUrl) {
        this.email = email;
        this.username = username;
        this.passwordHash = passwordHash;
        this.bio = bio;
        this.profilePictureUrl = profilePictureUrl;
    }

    
    public Long getId() {
        return id;
    }

    public void setEmail(String email) {
        this.email = email;   
    }

    public String getEmail() {
        return email;
    }   

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getBio() {
        return bio;
    }

    public void setProfilePictureUrl(String profilePictureUrl) {
        this.profilePictureUrl = profilePictureUrl;
    }

    public String getProfilePictureUrl() {
        return profilePictureUrl;
    }
}