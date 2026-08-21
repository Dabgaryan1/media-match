package com.danielabgaryan.mediamatch.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.danielabgaryan.mediamatch.exception.DuplicateResourceException;
import com.danielabgaryan.mediamatch.exception.ResourceNotFoundException;
import com.danielabgaryan.mediamatch.model.Genre;
import com.danielabgaryan.mediamatch.repository.GenreRepository;

@ExtendWith(MockitoExtension.class)
public class GenreServiceTest {
    @Mock
    private GenreRepository genreRepository;

    @InjectMocks
    private GenreService genreService;

    @Test
    void createGenre_whenNameIsAvailable_savesGenre() {
        when(genreRepository.existsByNameIgnoreCase("Drama")).thenReturn(false);
        when(genreRepository.save(any(Genre.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Genre result = genreService.createGenre("Drama");

        assertEquals("Drama", result.getName());
        verify(genreRepository).save(result);
    }

    @Test
    void createGenre_whenNameExists_throwsDuplicateResourceException() {
        when(genreRepository.existsByNameIgnoreCase("Drama")).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> genreService.createGenre("Drama"));
        verify(genreRepository, never()).save(any(Genre.class));
    }

    @Test
    void getGenreById_whenGenreExists_returnsGenre() {
        Genre genre = new Genre("Drama");
        when(genreRepository.findById(1L)).thenReturn(Optional.of(genre));

        assertSame(genre, genreService.getGenreById(1L));
    }

    @Test
    void getGenreById_whenGenreDoesNotExist_throwsResourceNotFoundException() {
        when(genreRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> genreService.getGenreById(1L));
    }

    @Test
    void getGenreByName_whenGenreExists_returnsGenre() {
        Genre genre = new Genre("Drama");
        when(genreRepository.findByNameIgnoreCase("drama")).thenReturn(Optional.of(genre));

        assertSame(genre, genreService.getGenreByName("drama"));
    }

    @Test
    void getGenreByName_whenGenreDoesNotExist_throwsResourceNotFoundException() {
        when(genreRepository.findByNameIgnoreCase("missing")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> genreService.getGenreByName("missing"));
    }

    @Test
    void getAllGenres_returnsAllGenres() {
        List<Genre> genres = List.of(new Genre("Drama"), new Genre("Comedy"));
        when(genreRepository.findAll()).thenReturn(genres);

        assertSame(genres, genreService.getAllGenres());
    }

    @Test
    void deleteGenre_whenGenreExists_deletesGenre() {
        Genre genre = new Genre("Drama");
        when(genreRepository.findById(1L)).thenReturn(Optional.of(genre));

        genreService.deleteGenre(1L);

        verify(genreRepository).delete(genre);
    }

    @Test
    void deleteGenre_whenGenreDoesNotExist_doesNotDelete() {
        when(genreRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> genreService.deleteGenre(1L));
        verify(genreRepository, never()).delete(any(Genre.class));
    }
}
