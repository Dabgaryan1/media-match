package com.danielabgaryan.mediamatch.dto;

public class UpdateRatingRequest {
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
