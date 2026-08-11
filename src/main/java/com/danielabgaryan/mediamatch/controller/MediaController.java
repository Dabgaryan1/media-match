package com.danielabgaryan.mediamatch.controller;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.danielabgaryan.mediamatch.model.Media;
import com.danielabgaryan.mediamatch.model.MediaType;
import com.danielabgaryan.mediamatch.dto.CreateMediaRequest;
import com.danielabgaryan.mediamatch.service.MediaService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PathVariable;
import java.util.List;

@RestController
@RequestMapping("/media")
public class MediaController {
    private final MediaService mediaService;

    public MediaController(MediaService mediaService) {
        this.mediaService = mediaService;
    }

    @PostMapping
    public Media createMedia(@Valid @RequestBody CreateMediaRequest request) {
        return mediaService.createMedia(
            request.getTitle(),
            request.getDescription(),
            request.getReleaseDate(),
            request.getImageUrl(),
            request.getMediaType(),
            request.getGenres()
        );
    }

    @GetMapping("/{id}")
    public Media getMediaById(@PathVariable Long id) {
        return mediaService.getMediaById(id);
    }

    @GetMapping("/exacttitle/{exactTitle}") 
    public List<Media> getMediaByExactTitle(@PathVariable String exactTitle) {
        return mediaService.getMediaByExactTitle(exactTitle);
    }

    @GetMapping("/mediatype/{mediaType}")
    public List<Media> getMediaByMediaType(@PathVariable MediaType mediaType) {
        return mediaService.getMediaByType(mediaType);
    }
    
    @GetMapping("/genrename/{genreName}")
    public List<Media> getMediaByGenre(@PathVariable String genreName) {
        return mediaService.getMediaByGenreName(genreName);
    }

    @GetMapping("/title/{title}")
    public List<Media> getMediaByTitle(@PathVariable String title) {
        return mediaService.getMediaByTitle(title);
    }
    
    @PutMapping("/{id}")
    public Media updateMedia(@PathVariable Long id, @RequestBody CreateMediaRequest request) {
        return mediaService.updateMedia(
            id, 
            request.getTitle(), 
            request.getDescription(), 
            request.getReleaseDate(),
            request.getImageUrl(), 
            request.getMediaType(), 
            request.getGenres() 
        );
    }

    @DeleteMapping("/{id}")
    public void deleteMedia(@PathVariable Long id) {
        mediaService.deleteMediaById(id);
    }
}   
