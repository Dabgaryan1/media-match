package com.danielabgaryan.mediamatch.dto;

import java.util.List;
import com.danielabgaryan.mediamatch.model.MediaType;

public record MediaSearchResponse(int page, int totalPages, long totalResults, List<Result> results) {
    // Search results have external IDs; they are not persisted Media entities.
    public record Result(String externalSource, long externalId, String title,
            String description, String posterPath, String releaseDate, MediaType mediaType) {}
}
