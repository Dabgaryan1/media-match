package com.danielabgaryan.mediamatch.service;

import org.springframework.stereotype.Service;
import com.danielabgaryan.mediamatch.repository.GenreRepository;
import com.danielabgaryan.mediamatch.exception.DuplicateResourceException;
import com.danielabgaryan.mediamatch.exception.ResourceNotFoundException;
import com.danielabgaryan.mediamatch.model.Genre;
import java.util.List;

@Service
public class GenreService {
    private GenreRepository genreRepository;

    public GenreService(GenreRepository genreRepository) {
        this.genreRepository = genreRepository;
    }

    public Genre createGenre(String name) {
        if(genreRepository.existsByNameIgnoreCase(name)) {
            throw new DuplicateResourceException("Genre already exists");
        }
        Genre genre = new Genre(name);
        return genreRepository.save(genre);
    }

    public Genre getGenreById(Long genreId) {
        return genreRepository.findById(genreId).orElseThrow(() -> new ResourceNotFoundException("Genre not found"));
    }

    public Genre getGenreByName(String name) {
        return genreRepository.findByNameIgnoreCase(name)
                .orElseThrow(() -> new ResourceNotFoundException("Genre not found"));
    }

    public List<Genre> getAllGenres() {
        return genreRepository.findAll();
    }

    public void deleteGenre(Long genreId) {
        Genre genre = getGenreById(genreId);
        genreRepository.delete(genre);
    }
}
