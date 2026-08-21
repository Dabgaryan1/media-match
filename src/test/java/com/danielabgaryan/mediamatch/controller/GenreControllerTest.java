package com.danielabgaryan.mediamatch.controller;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.danielabgaryan.mediamatch.exception.DuplicateResourceException;
import com.danielabgaryan.mediamatch.exception.ResourceNotFoundException;
import com.danielabgaryan.mediamatch.model.Genre;
import com.danielabgaryan.mediamatch.service.GenreService;

@WebMvcTest(GenreController.class)
public class GenreControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private GenreService genreService;

    @Test
    void createGenre_whenRequestIsValid_returnsGenre() throws Exception {
        Genre genre = genre(1L, "Action");
        when(genreService.createGenre("Action")).thenReturn(genre);

        mockMvc.perform(post("/genres")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "name": "Action"
                    }
                    """))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.name").value("Action"));

        verify(genreService).createGenre("Action");
    }

    @Test
    void createGenre_whenNameIsBlank_returnsValidationError() throws Exception {
        mockMvc.perform(post("/genres")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "name": ""
                    }
                    """))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.name").value("Name is required"));

        verify(genreService, never()).createGenre(anyString());
    }

    @Test
    void createGenre_whenGenreAlreadyExists_returnsConflict() throws Exception {
        when(genreService.createGenre("Action"))
            .thenThrow(new DuplicateResourceException("Genre already exists"));

        mockMvc.perform(post("/genres")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "name": "Action"
                    }
                    """))
            .andExpect(status().isConflict())
            .andExpect(content().string("Genre already exists"));

        verify(genreService).createGenre("Action");
    }

    @Test
    void getGenreById_whenGenreExists_returnsGenre() throws Exception {
        when(genreService.getGenreById(1L)).thenReturn(genre(1L, "Action"));

        mockMvc.perform(get("/genres/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.name").value("Action"));

        verify(genreService).getGenreById(1L);
    }

    @Test
    void getGenreById_whenGenreDoesNotExist_returnsNotFound() throws Exception {
        when(genreService.getGenreById(1L)).thenThrow(new ResourceNotFoundException("Genre not found"));

        mockMvc.perform(get("/genres/1"))
            .andExpect(status().isNotFound())
            .andExpect(content().string("Genre not found"));

        verify(genreService).getGenreById(1L);
    }

    @Test
    void getGenreByName_whenGenreExists_returnsGenre() throws Exception {
        when(genreService.getGenreByName("Action")).thenReturn(genre(1L, "Action"));

        mockMvc.perform(get("/genres/name/Action"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.name").value("Action"));

        verify(genreService).getGenreByName("Action");
    }

    @Test
    void getAllGenres_returnsGenres() throws Exception {
        when(genreService.getAllGenres()).thenReturn(List.of(
            genre(1L, "Action"),
            genre(2L, "Comedy")
        ));

        mockMvc.perform(get("/genres"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(2))
            .andExpect(jsonPath("$[0].id").value(1))
            .andExpect(jsonPath("$[0].name").value("Action"))
            .andExpect(jsonPath("$[1].id").value(2))
            .andExpect(jsonPath("$[1].name").value("Comedy"));

        verify(genreService).getAllGenres();
    }

    @Test
    void deleteGenre_passesIdToService() throws Exception {
        mockMvc.perform(delete("/genres/1"))
            .andExpect(status().isOk())
            .andExpect(content().string(""));

        verify(genreService).deleteGenre(1L);
    }

    private Genre genre(Long id, String name) {
        Genre genre = new Genre(name);
        genre.setId(id);
        return genre;
    }
}
