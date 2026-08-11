package com.danielabgaryan.mediamatch.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotNull;

public class UpdateRatingRequest {

    @NotNull(message = "Rating must be between 1 and 5")
    @Min(1)
    @Max(5)
    private Integer rating;

    public UpdateRatingRequest(Integer rating) {
        this.rating = rating;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }
}
