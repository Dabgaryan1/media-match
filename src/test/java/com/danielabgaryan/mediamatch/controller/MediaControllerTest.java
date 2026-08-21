package com.danielabgaryan.mediamatch.controller;

import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.danielabgaryan.mediamatch.exception.ResourceNotFoundException;
import com.danielabgaryan.mediamatch.model.Genre;
import com.danielabgaryan.mediamatch.model.Media;
import com.danielabgaryan.mediamatch.service.MediaService;

@WebMvcTest(MediaController.class)
public class MediaControllerTest {
    private static final LocalDate RELEASE_DATE = LocalDate.of(2025, 1, 1);

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private MediaService mediaService;

    @Test
    void createMedia_whenRequestIsValid_returnsMedia() throws Exception {
        Media media = media(1L, "Test Movie", com.danielabgaryan.mediamatch.model.MediaType.MOVIE);
        when(mediaService.createMedia(
                eq("Test Movie"),
                eq("Test description"),
                eq(RELEASE_DATE),
                eq("movie.jpg"),
                eq(com.danielabgaryan.mediamatch.model.MediaType.MOVIE),
                anySet()
            ))
            .thenReturn(media);

        mockMvc.perform(post("/media")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "title": "Test Movie",
                        "description": "Test description",
                        "releaseDate": "2025-01-01",
                        "imageUrl": "movie.jpg",
                        "mediaType": "MOVIE",
                        "genres": [
                            { "name": "Action" }
                        ]
                    }
                    """))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.title").value("Test Movie"))
            .andExpect(jsonPath("$.description").value("Test description"))
            .andExpect(jsonPath("$.releaseDate").value("2025-01-01"))
            .andExpect(jsonPath("$.imageUrl").value("movie.jpg"))
            .andExpect(jsonPath("$.mediaType").value("MOVIE"))
            .andExpect(jsonPath("$.genres[0].id").value(10))
            .andExpect(jsonPath("$.genres[0].name").value("Action"));

        verify(mediaService).createMedia(
            eq("Test Movie"),
            eq("Test description"),
            eq(RELEASE_DATE),
            eq("movie.jpg"),
            eq(com.danielabgaryan.mediamatch.model.MediaType.MOVIE),
            argThat(genres -> genres.size() == 1 && "Action".equals(genres.iterator().next().getName()))
        );
    }

    @Test
    void createMedia_whenRequestIsInvalid_returnsValidationErrors() throws Exception {
        mockMvc.perform(post("/media")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "title": "",
                        "description": "",
                        "imageUrl": "",
                        "genres": []
                    }
                    """))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.title").value("Title is required"))
            .andExpect(jsonPath("$.description").exists())
            .andExpect(jsonPath("$.releaseDate").value("Release date is required"))
            .andExpect(jsonPath("$.imageUrl").value("Image URL is required"))
            .andExpect(jsonPath("$.mediaType").value("Media Type is required"))
            .andExpect(jsonPath("$.genres").value("At least one genre is required"));

        verify(mediaService, never()).createMedia(
            eq(""), eq(""), eq(null), eq(""), eq(null), anySet()
        );
    }

    @Test
    void getMediaById_whenMediaExists_returnsMedia() throws Exception {
        Media media = media(1L, "Test Movie", com.danielabgaryan.mediamatch.model.MediaType.MOVIE);
        when(mediaService.getMediaById(1L)).thenReturn(media);

        mockMvc.perform(get("/media/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.title").value("Test Movie"))
            .andExpect(jsonPath("$.mediaType").value("MOVIE"));

        verify(mediaService).getMediaById(1L);
    }

    @Test
    void getMediaById_whenMediaDoesNotExist_returnsNotFound() throws Exception {
        when(mediaService.getMediaById(1L)).thenThrow(new ResourceNotFoundException("Media not found"));

        mockMvc.perform(get("/media/1"))
            .andExpect(status().isNotFound())
            .andExpect(content().string("Media not found"));

        verify(mediaService).getMediaById(1L);
    }

    @Test
    void getMediaByExactTitle_returnsMatchingMedia() throws Exception {
        List<Media> results = List.of(media(1L, "Test Movie", com.danielabgaryan.mediamatch.model.MediaType.MOVIE));
        when(mediaService.getMediaByExactTitle("Test Movie")).thenReturn(results);

        mockMvc.perform(get("/media/exacttitle/Test Movie"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(1))
            .andExpect(jsonPath("$[0].id").value(1))
            .andExpect(jsonPath("$[0].title").value("Test Movie"));

        verify(mediaService).getMediaByExactTitle("Test Movie");
    }

    @Test
    void getMediaByMediaType_returnsMatchingMedia() throws Exception {
        List<Media> results = List.of(media(1L, "Test Movie", com.danielabgaryan.mediamatch.model.MediaType.MOVIE));
        when(mediaService.getMediaByType(com.danielabgaryan.mediamatch.model.MediaType.MOVIE)).thenReturn(results);

        mockMvc.perform(get("/media/mediatype/MOVIE"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(1))
            .andExpect(jsonPath("$[0].mediaType").value("MOVIE"));

        verify(mediaService).getMediaByType(com.danielabgaryan.mediamatch.model.MediaType.MOVIE);
    }

    @Test
    void getMediaByGenre_returnsMatchingMedia() throws Exception {
        List<Media> results = List.of(media(1L, "Test Movie", com.danielabgaryan.mediamatch.model.MediaType.MOVIE));
        when(mediaService.getMediaByGenreName("Action")).thenReturn(results);

        mockMvc.perform(get("/media/genrename/Action"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(1))
            .andExpect(jsonPath("$[0].genres[0].name").value("Action"));

        verify(mediaService).getMediaByGenreName("Action");
    }

    @Test
    void getMediaByTitle_returnsPartialTitleMatches() throws Exception {
        List<Media> results = List.of(media(1L, "Test Movie", com.danielabgaryan.mediamatch.model.MediaType.MOVIE));
        when(mediaService.getMediaByTitle("Test")).thenReturn(results);

        mockMvc.perform(get("/media/title/Test"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(1))
            .andExpect(jsonPath("$[0].title").value("Test Movie"));

        verify(mediaService).getMediaByTitle("Test");
    }

    @Test
    void updateMedia_whenRequestIsValid_returnsUpdatedMedia() throws Exception {
        Media updatedMedia = media(1L, "Updated Movie", com.danielabgaryan.mediamatch.model.MediaType.MOVIE);
        when(mediaService.updateMedia(
                eq(1L),
                eq("Updated Movie"),
                eq("Updated description"),
                eq(RELEASE_DATE),
                eq("updated.jpg"),
                eq(com.danielabgaryan.mediamatch.model.MediaType.MOVIE),
                anySet()
            ))
            .thenReturn(updatedMedia);

        mockMvc.perform(put("/media/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "title": "Updated Movie",
                        "description": "Updated description",
                        "releaseDate": "2025-01-01",
                        "imageUrl": "updated.jpg",
                        "mediaType": "MOVIE",
                        "genres": [
                            { "name": "Action" }
                        ]
                    }
                    """))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.title").value("Updated Movie"));

        verify(mediaService).updateMedia(
            eq(1L),
            eq("Updated Movie"),
            eq("Updated description"),
            eq(RELEASE_DATE),
            eq("updated.jpg"),
            eq(com.danielabgaryan.mediamatch.model.MediaType.MOVIE),
            argThat(genres -> genres.size() == 1 && "Action".equals(genres.iterator().next().getName()))
        );
    }

    @Test
    void deleteMedia_passesIdToService() throws Exception {
        mockMvc.perform(delete("/media/1"))
            .andExpect(status().isOk())
            .andExpect(content().string(""));

        verify(mediaService).deleteMediaById(1L);
    }

    private Media media(
        Long id,
        String title,
        com.danielabgaryan.mediamatch.model.MediaType mediaType
    ) {
        Genre genre = new Genre("Action");
        genre.setId(10L);

        Media media = new Media(
            title,
            "Test description",
            RELEASE_DATE,
            "movie.jpg",
            mediaType,
            Set.of(genre)
        );
        media.setId(id);
        return media;
    }
}
