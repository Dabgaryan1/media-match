package com.danielabgaryan.mediamatch.controller;

import com.danielabgaryan.mediamatch.dto.MediaSearchResponse;
import com.danielabgaryan.mediamatch.service.TmdbService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MediaSearchController {
    private final TmdbService tmdbService;

    public MediaSearchController(TmdbService tmdbService) {
        this.tmdbService = tmdbService;
    }

    @GetMapping("/media/search")
    public MediaSearchResponse searchMovies(@RequestParam String query,
            @RequestParam(defaultValue = "1") int page) {
        return tmdbService.searchMovies(query, page);
    }
}
