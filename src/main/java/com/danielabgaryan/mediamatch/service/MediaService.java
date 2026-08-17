package com.danielabgaryan.mediamatch.service;

import org.springframework.stereotype.Service;
import com.danielabgaryan.mediamatch.repository.MediaRepository;
import com.danielabgaryan.mediamatch.repository.GenreRepository;
import com.danielabgaryan.mediamatch.exception.ResourceNotFoundException;
import com.danielabgaryan.mediamatch.model.Genre;
import com.danielabgaryan.mediamatch.model.Media;
import com.danielabgaryan.mediamatch.model.MediaType;
import java.util.List;
import java.util.HashSet;
import java.util.Set;
import java.time.LocalDate;

@Service
public class MediaService {
    private final MediaRepository mediaRepository;
    private final GenreRepository genreRepository;

    public MediaService(MediaRepository mediaRepository, GenreRepository genreRepository) {
        this.mediaRepository = mediaRepository;
        this.genreRepository = genreRepository;
    }

    public Media createMedia(String title, String description, LocalDate releaseDate, String imageUrl, MediaType type, Set<Genre> genres) {
        
        Set<Genre> savedGenres = resolveGenres(genres);

        Media media = new Media(title, description, releaseDate, imageUrl, type, savedGenres);

        return mediaRepository.save(media);
    }

    public Media getMediaById(Long id) {
        return mediaRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Media not found"));
    }

    public void deleteMediaById(Long id) {
        Media media = getMediaById(id);

        mediaRepository.delete(media);
    }

    public Media updateMedia(Long id, String title, String description, LocalDate releaseDate, String imageUrl, MediaType type, Set<Genre> genres) {
        Media media = getMediaById(id);

        Set<Genre> savedGenres = resolveGenres(genres);

        media.setTitle(title);
        media.setDescription(description);
        media.setReleaseDate(releaseDate);
        media.setImageUrl(imageUrl);
        media.setMediaType(type);
        media.setGenres(savedGenres);

        return mediaRepository.save(media);
    }

    //helper method to resolve genres, ensuring they are saved in the database
    private Set<Genre> resolveGenres(Set<Genre> genres) {
        Set<Genre> savedGenres = new HashSet<>();
        if (genres != null) {
            for (Genre genre : genres) {
                Genre savedGenre = genreRepository.findByNameIgnoreCase(genre.getName())
                    .orElseGet(() -> genreRepository.save(genre));
                savedGenres.add(savedGenre);
            }
        }
        return savedGenres;
    }

    public List<Media> getMediaByExactTitle(String title) {
        return mediaRepository.findByTitleIgnoreCase(title);
    }

    public List<Media> getMediaByTitle(String title) {
        return mediaRepository.findByTitleContainingIgnoreCase(title);
    }

    public List<Media> getMediaByGenreName(String genreName) {
        return mediaRepository.findByGenres_NameIgnoreCase(genreName);
    }

    public List<Media> getMediaByType(MediaType mediaType) {
        return mediaRepository.findByMediaType(mediaType);
    }
}
