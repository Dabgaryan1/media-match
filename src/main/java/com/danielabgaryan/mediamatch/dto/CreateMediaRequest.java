package com.danielabgaryan.mediamatch.dto;

import java.time.LocalDate;
import java.util.Set;
import com.danielabgaryan.mediamatch.model.Genre;
import com.danielabgaryan.mediamatch.model.MediaType;

public class CreateMediaRequest {
    private String title;
    private String description;
    private LocalDate releaseDate;
    private String imageUrl;
    private MediaType mediaType;
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
