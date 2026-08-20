package com.danielabgaryan.mediamatch.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.danielabgaryan.mediamatch.exception.ResourceNotFoundException;
import com.danielabgaryan.mediamatch.model.Genre;
import com.danielabgaryan.mediamatch.model.Media;
import com.danielabgaryan.mediamatch.model.MediaType;
import com.danielabgaryan.mediamatch.repository.GenreRepository;
import com.danielabgaryan.mediamatch.repository.MediaRepository;

@ExtendWith(MockitoExtension.class)
public class MediaServiceTest {
    @Mock
    private MediaRepository mediaRepository;

    @Mock
    private GenreRepository genreRepository;

    @InjectMocks
    private MediaService mediaService;

    @Test
    void createMedia_withExistingGenre_usesSavedGenreAndSavesMedia() {
        Genre requestGenre = new Genre("Drama");
        Genre savedGenre = new Genre("Drama");
        savedGenre.setId(1L);
        when(genreRepository.findByNameIgnoreCase("Drama")).thenReturn(Optional.of(savedGenre));
        when(mediaRepository.save(any(Media.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Media result = mediaService.createMedia(
            "Title", "Description", LocalDate.of(2020, 1, 1), "image", MediaType.MOVIE,
            Set.of(requestGenre));

        assertEquals("Title", result.getTitle());
        assertEquals(Set.of(savedGenre), result.getGenres());
        verify(genreRepository, never()).save(requestGenre);
        verify(mediaRepository).save(result);
    }

    @Test
    void createMedia_withNewGenre_savesGenreBeforeMedia() {
        Genre newGenre = new Genre("Drama");
        when(genreRepository.findByNameIgnoreCase("Drama")).thenReturn(Optional.empty());
        when(genreRepository.save(newGenre)).thenReturn(newGenre);
        when(mediaRepository.save(any(Media.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Media result = mediaService.createMedia(
            "Title", "Description", null, null, MediaType.BOOK, Set.of(newGenre));

        assertTrue(result.getGenres().contains(newGenre));
        verify(genreRepository).save(newGenre);
    }

    @Test
    void createMedia_withNullGenres_savesMediaWithEmptyGenres() {
        when(mediaRepository.save(any(Media.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Media result = mediaService.createMedia(
            "Title", null, null, null, MediaType.GAME, null);

        assertTrue(result.getGenres().isEmpty());
        verify(genreRepository, never()).findByNameIgnoreCase(any(String.class));
    }

    @Test
    void getMediaById_whenMediaExists_returnsMedia() {
        Media media = new Media();
        when(mediaRepository.findById(1L)).thenReturn(Optional.of(media));

        assertSame(media, mediaService.getMediaById(1L));
    }

    @Test
    void getMediaById_whenMediaDoesNotExist_throwsResourceNotFoundException() {
        when(mediaRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> mediaService.getMediaById(1L));
    }

    @Test
    void updateMedia_whenMediaExists_updatesAllFieldsAndSaves() {
        Media media = new Media();
        Genre genre = new Genre("Drama");
        LocalDate releaseDate = LocalDate.of(2024, 2, 3);
        when(mediaRepository.findById(1L)).thenReturn(Optional.of(media));
        when(genreRepository.findByNameIgnoreCase("Drama")).thenReturn(Optional.of(genre));
        when(mediaRepository.save(media)).thenReturn(media);

        Media result = mediaService.updateMedia(
            1L, "Updated", "Updated description", releaseDate, "new-image", MediaType.TV_SHOW,
            Set.of(genre));

        assertEquals("Updated", result.getTitle());
        assertEquals("Updated description", result.getDescription());
        assertEquals(releaseDate, result.getReleaseDate());
        assertEquals("new-image", result.getImageUrl());
        assertEquals(MediaType.TV_SHOW, result.getMediaType());
        assertEquals(Set.of(genre), result.getGenres());
        verify(mediaRepository).save(media);
    }

    @Test
    void updateMedia_whenMediaDoesNotExist_doesNotResolveGenresOrSave() {
        when(mediaRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
            () -> mediaService.updateMedia(
                1L, "Title", null, null, null, MediaType.MOVIE, Set.of(new Genre("Drama"))));

        verify(genreRepository, never()).findByNameIgnoreCase(any(String.class));
        verify(mediaRepository, never()).save(any(Media.class));
    }

    @Test
    void deleteMediaById_whenMediaExists_deletesMedia() {
        Media media = new Media();
        when(mediaRepository.findById(1L)).thenReturn(Optional.of(media));

        mediaService.deleteMediaById(1L);

        verify(mediaRepository).delete(media);
    }

    @Test
    void deleteMediaById_whenMediaDoesNotExist_doesNotDelete() {
        when(mediaRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> mediaService.deleteMediaById(1L));
        verify(mediaRepository, never()).delete(any(Media.class));
    }

    @Test
    void getMediaByExactTitle_returnsRepositoryResults() {
        List<Media> media = List.of(new Media());
        when(mediaRepository.findByTitleIgnoreCase("Title")).thenReturn(media);

        assertSame(media, mediaService.getMediaByExactTitle("Title"));
    }

    @Test
    void getMediaByTitle_returnsRepositoryResults() {
        List<Media> media = List.of(new Media());
        when(mediaRepository.findByTitleContainingIgnoreCase("tit")).thenReturn(media);

        assertSame(media, mediaService.getMediaByTitle("tit"));
    }

    @Test
    void getMediaByGenreName_returnsRepositoryResults() {
        List<Media> media = List.of(new Media());
        when(mediaRepository.findByGenres_NameIgnoreCase("Drama")).thenReturn(media);

        assertSame(media, mediaService.getMediaByGenreName("Drama"));
    }

    @Test
    void getMediaByType_returnsRepositoryResults() {
        List<Media> media = List.of(new Media());
        when(mediaRepository.findByMediaType(MediaType.MOVIE)).thenReturn(media);

        assertSame(media, mediaService.getMediaByType(MediaType.MOVIE));
    }
}
