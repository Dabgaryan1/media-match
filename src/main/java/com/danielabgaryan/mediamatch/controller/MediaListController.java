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
import com.danielabgaryan.mediamatch.dto.CreateMediaListRequest;
import com.danielabgaryan.mediamatch.model.MediaList;
import java.util.List;

@RestController
@RequestMapping("/media-lists")
public class MediaListController {
    private final MediaListService mediaListService;

    public MediaListController(MediaListService mediaListService) {
        this.mediaListService = mediaListService;
    }

    @PostMapping
    public MediaList createMediaList(@RequestBody CreateMediaListRequest request) {
        return mediaListService.createMediaList(
            request.getUserId(),
            request.getName(),
            request.getDescription()
        );
    }

    @GetMapping("/{id}")
    public MediaList getMediaListById(@PathVariable Long id) {
        return mediaListService.getMediaListById(id);
    }

    @GetMapping("/user/{userId}")
    public List<MediaList> getMediaListsByUserId(@PathVariable Long userId) {
        return mediaListService.getMediaListsByUserId(userId);
    }

    @GetMapping("/name/{name}")
    public List<MediaList> getMediaListsByName(@PathVariable String name) {
        return mediaListService.getMediaListsByName(name);
    }

    @GetMapping("/user/{userId}/name/{name}")
    public List<MediaList> getMediaListsByUserIdAndName(@PathVariable Long userId, @PathVariable String name) {
        return mediaListService.getMediaListsByUserIdAndName(userId, name);
    }

    @PutMapping("/{id}")
    public MediaList updateMediaList(@PathVariable Long id, @RequestBody CreateMediaListRequest request) {
        return mediaListService.updateMediaList(id, request.getName(), request.getDescription());
    }

    @PutMapping("/{listId}/media/{mediaId}")
    public MediaList addMediaToList(@PathVariable Long listId, @PathVariable Long mediaId) {
        return mediaListService.addMediaToList(listId, mediaId);
    }

    @DeleteMapping("/{listId}/media/{mediaId}")
    public MediaList removeMediaFromList(@PathVariable Long listId, @PathVariable Long mediaId) {
        return mediaListService.removeMediaFromList(listId, mediaId);
    }

    @DeleteMapping("/{id}")
    public void deleteMediaList(@PathVariable Long id) {
        mediaListService.deleteMediaList(id);
    }
}
