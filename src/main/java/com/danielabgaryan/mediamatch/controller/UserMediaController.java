package com.danielabgaryan.mediamatch.controller;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.danielabgaryan.mediamatch.dto.CreateUserMediaRequest;
import com.danielabgaryan.mediamatch.dto.UpdateFavoriteRequest;
import com.danielabgaryan.mediamatch.dto.UpdateRatingRequest;
import com.danielabgaryan.mediamatch.dto.UpdateStatusRequest;
import com.danielabgaryan.mediamatch.model.UserMedia;
import com.danielabgaryan.mediamatch.service.UserMediaService;
import java.util.List;

@RestController
@RequestMapping("/user-medias") 
public class UserMediaController {
    private final UserMediaService userMediaService;

    public UserMediaController(UserMediaService userMediaService) {
        this.userMediaService = userMediaService;
    }

    @PostMapping
    public UserMedia addMediaToUser(@RequestBody CreateUserMediaRequest request) {
        return userMediaService.addMediaToUser(
            request.getUserId(),
            request.getMediaId(),
            request.getStatus()
        );
    }

    @GetMapping("/{id}")
    public UserMedia getUserMediaById(@PathVariable Long id) {
        return userMediaService.getUserMediaById(id);
    }

    @GetMapping("/user/{userId}")
    public List<UserMedia> getUserLibrary(@PathVariable Long userId) {
        return userMediaService.getUserLibrary(userId);
    }

    @GetMapping("/user/{userId}/favorites")
    public List<UserMedia> getUserFavorites(@PathVariable Long userId) {
        return userMediaService.getUserFavorites(userId);
    }

    @PutMapping("/{id}/status")
    public UserMedia updateStatus(@PathVariable Long id, @RequestBody UpdateStatusRequest request) {
        return userMediaService.updateStatus(id, request.getStatus());
    }

    @PutMapping("/{id}/rating")
    public UserMedia updateRating(@PathVariable Long id, @RequestBody UpdateRatingRequest request) {
        return userMediaService.updateRating(id, request.getRating());
    }

    @PutMapping("/{id}/favorite")
    public UserMedia updateFavorite(@PathVariable Long id, @RequestBody UpdateFavoriteRequest request) {
        return userMediaService.updateFavorite(id, request.getFavorite());
    }

    @DeleteMapping("/{id}")
    public void removeMediaFromUser(@PathVariable Long id) {
        userMediaService.removeMediaFromUser(id);
    }
}
