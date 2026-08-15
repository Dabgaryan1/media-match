package com.danielabgaryan.mediamatch.controller;

import com.danielabgaryan.mediamatch.model.User;

import org.springframework.security.core.Authentication;
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
import com.danielabgaryan.mediamatch.dto.UserMediaResponse;
import com.danielabgaryan.mediamatch.dto.UserResponse;
import com.danielabgaryan.mediamatch.model.UserMedia;
import com.danielabgaryan.mediamatch.service.UserMediaService;
import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/user-medias") 
public class UserMediaController {
    private final UserMediaService userMediaService;

    public UserMediaController(UserMediaService userMediaService) {
        this.userMediaService = userMediaService;
    }

    @PostMapping
    public UserMediaResponse addMediaToUser(@Valid @RequestBody CreateUserMediaRequest request, Authentication authentication) {
        String email = authentication.getName();

        UserMedia userMedia = userMediaService.addMediaToUser(
            email,
            request.getMediaId(),
            request.getStatus()
        );

        return toUserMediaResponse(userMedia);
    }

    @GetMapping("/{id}")
    public UserMediaResponse getUserMediaById(@PathVariable Long id) {
        UserMedia usermedia = userMediaService.getUserMediaById(id);
        return toUserMediaResponse(usermedia);
    }

    @GetMapping("/user/{userId}")
    public List<UserMediaResponse> getUserLibrary(@PathVariable Long userId) {
        return userMediaService.getUserLibrary(userId).stream()
        .map(this::toUserMediaResponse).toList();
    }

    @GetMapping("/user/{userId}/favorites")
    public List<UserMediaResponse> getUserFavorites(@PathVariable Long userId) {
        return userMediaService.getUserFavorites(userId).stream()
        .map(this::toUserMediaResponse).toList();
    }

    @PutMapping("/{id}/status")
    public UserMediaResponse updateStatus(@PathVariable Long id, @Valid @RequestBody UpdateStatusRequest request, Authentication authentication) {
        String email = authentication.getName();
        UserMedia userMedia = userMediaService.updateStatus(id, email, request.getStatus());
        return toUserMediaResponse(userMedia);
    }

    @PutMapping("/{id}/rating")
    public UserMediaResponse updateRating(@PathVariable Long id, @Valid @RequestBody UpdateRatingRequest request, Authentication authentication) {
        String email = authentication.getName();
        UserMedia userMedia = userMediaService.updateRating(id, email, request.getRating());
        return toUserMediaResponse(userMedia);
    }

    @PutMapping("/{id}/favorite")
    public UserMediaResponse updateFavorite(@PathVariable Long id, @RequestBody UpdateFavoriteRequest request, Authentication authentication) {
        String email = authentication.getName();
        UserMedia userMedia = userMediaService.updateFavorite(id, email, request.getFavorite());
        return toUserMediaResponse(userMedia);
    }

    @DeleteMapping("/{id}")
    public void removeMediaFromUser(@PathVariable Long id, Authentication authentication) {
        String email = authentication.getName();
        userMediaService.removeMediaFromUser(id, email);
    }

    //helper function to convert UserMedia entity into a safe API response
    private UserMediaResponse toUserMediaResponse(UserMedia userMedia) {
        User user = userMedia.getUser();

        UserResponse userResponse = new UserResponse(
            user.getId(),
            user.getUsername(),
            user.getEmail(),
            user.getBio(),
            user.getProfilePictureUrl()
        );

        return new UserMediaResponse(
            userMedia.getId(),
            userResponse,
            userMedia.getMedia(),
            userMedia.getStatus(),
            userMedia.getRating(),
            userMedia.getFavorite(),
            userMedia.getAddedAt()
        );      
        
    }
}
