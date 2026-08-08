package com.danielabgaryan.mediamatch.controller;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.danielabgaryan.mediamatch.dto.CreateGenreRequest;
import com.danielabgaryan.mediamatch.model.Genre;
import com.danielabgaryan.mediamatch.service.GenreService;
import java.util.List;

@RestController
@RequestMapping("/genres")
public class GenreController {
    private final GenreService genreService;

    public GenreController(GenreService genreService) {
        this.genreService = genreService;
    }

    @PostMapping
    public Genre createGenre(@RequestBody CreateGenreRequest request) {
        return genreService.createGenre(request.getName());
    }

    @GetMapping("/{id}") 
    public Genre getGenreById(@PathVariable Long id) {
        return genreService.getGenreById(id);
    }

    @GetMapping("/name/{name}") 
    public Genre getGenreByName(@PathVariable String name) {
        return genreService.getGenreByName(name);
    }

    @GetMapping
    public List<Genre> getAllGenres() {
        return genreService.getAllGenres();
    }

    @DeleteMapping("/{id}")
    public void deleteGenre(@PathVariable Long id) {
        genreService.deleteGenre(id);
    }
}
