package com.danielabgaryan.mediamatch.controller;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.danielabgaryan.mediamatch.service.MediaListService;
import org.springframework.security.core.Authentication;
import jakarta.validation.Valid;
import com.danielabgaryan.mediamatch.dto.CreateMediaListRequest;
import com.danielabgaryan.mediamatch.dto.MediaListResponse;
import com.danielabgaryan.mediamatch.dto.UserResponse;
import com.danielabgaryan.mediamatch.model.MediaList;
import com.danielabgaryan.mediamatch.model.User;

import java.util.List;

@RestController
@RequestMapping("/media-lists")
public class MediaListController {
    private final MediaListService mediaListService;

    public MediaListController(MediaListService mediaListService) {
        this.mediaListService = mediaListService;
    }

    @PostMapping
    public MediaListResponse createMediaList(@Valid @RequestBody CreateMediaListRequest request, Authentication authentication) {
        String email = authentication.getName();

        MediaList mediaList = mediaListService.createMediaList(
            email,
            request.getName(),
            request.getDescription()
        );

        return toMediaListResponse(mediaList);
    }

    @GetMapping("/{id}")
    public MediaListResponse getMediaListById(@PathVariable Long id) {
        MediaList mediaList = mediaListService.getMediaListById(id);
        return toMediaListResponse(mediaList);
    }

    @GetMapping("/user/{userId}")
    public List<MediaListResponse> getMediaListsByUserId(@PathVariable Long userId) {
        return mediaListService.getMediaListsByUserId(userId)
            .stream().map(this::toMediaListResponse)
            .toList();
    }

    @GetMapping("/name/{name}")
    public List<MediaListResponse> getMediaListsByName(@PathVariable String name) {
        return mediaListService.getMediaListsByName(name)
            .stream().map(this::toMediaListResponse)
            .toList();
    }

    @GetMapping("/user/{userId}/name/{name}")
    public List<MediaListResponse> getMediaListsByUserIdAndName(@PathVariable Long userId, @PathVariable String name) {
        return mediaListService.getMediaListsByUserIdAndName(userId, name)
            .stream().map(this::toMediaListResponse)
            .toList();
    }

    @PutMapping("/{id}")
    public MediaListResponse updateMediaList(@PathVariable Long id, @Valid @RequestBody CreateMediaListRequest request, Authentication authentication) {
        String email = authentication.getName();

        MediaList mediaList = mediaListService.updateMediaList(id, email, request.getName(), request.getDescription());
        return toMediaListResponse(mediaList);
    }

    @PutMapping("/{listId}/media/{mediaId}")
    public MediaListResponse addMediaToList(@PathVariable Long listId, @PathVariable Long mediaId, Authentication authentication) {
        String email = authentication.getName();
        MediaList mediaList = mediaListService.addMediaToList(listId, email, mediaId);
        return toMediaListResponse(mediaList);
    }

    @DeleteMapping("/{listId}/media/{mediaId}")
    public MediaListResponse removeMediaFromList(@PathVariable Long listId, @PathVariable Long mediaId, Authentication authentication) {
        String email = authentication.getName();

        MediaList mediaList = mediaListService.removeMediaFromList(listId, email, mediaId);

        return toMediaListResponse(mediaList);
    }

    @DeleteMapping("/{id}")
    public void deleteMediaList(@PathVariable Long id, Authentication authentication) {
        String email = authentication.getName();

        mediaListService.deleteMediaList(id, email);
    }

    //helper method to convert MediaList entity into a safe API response
    private MediaListResponse toMediaListResponse(MediaList mediaList) {
        User user = mediaList.getUser();

        UserResponse userResponse = new UserResponse(user.getId(), user.getUsername(), user.getEmail(), user.getBio(), user.getProfilePictureUrl());

        return new MediaListResponse(mediaList.getId(), mediaList.getName(), mediaList.getDescription(), userResponse);
    }
}
