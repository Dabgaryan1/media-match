package com.danielabgaryan.mediamatch.dto;

import java.time.LocalDate;
import java.util.Set;
import com.danielabgaryan.mediamatch.model.Genre;
import com.danielabgaryan.mediamatch.model.MediaType;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class CreateMediaRequest {

    @NotBlank(message = "Title is required")
    private String title;

    @NotBlank
    @Size(max = 300, message = "Description must be 300 character or less")
    private String description;

    @NotNull(message = "Release date is required")
    private LocalDate releaseDate;

    @NotBlank(message = "Image URL is required")
    private String imageUrl;

    @NotNull(message = "Media Type is required")
    private MediaType mediaType;

    @NotEmpty(message = "At least one genre is required")
    private Set<Genre> genres;

    public CreateMediaRequest() {
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDate getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(LocalDate releaseDate) {
        this.releaseDate = releaseDate;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public MediaType getMediaType() {
        return mediaType;
    }

    public void setMediaType(MediaType mediaType) {
        this.mediaType = mediaType;
    }

    public Set<Genre> getGenres() {
        return genres;
    }

    public void setGenres(Set<Genre> genres) {
        this.genres = genres;
    }
}
