package com.danielabgaryan.mediamatch.dto;

public class UpdateFavoriteRequest {
    private boolean favorite;

    public UpdateFavoriteRequest(boolean favorite) {
        this.favorite = favorite;
    }

    public boolean getFavorite() {
        return favorite;
    }

    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
    }
}
