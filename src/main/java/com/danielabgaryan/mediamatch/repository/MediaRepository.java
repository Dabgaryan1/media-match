package com.danielabgaryan.mediamatch.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import com.danielabgaryan.mediamatch.model.Media;
import com.danielabgaryan.mediamatch.model.MediaType;
import com.danielabgaryan.mediamatch.model.Genre;

public interface MediaRepository extends JpaRepository<Media, Long> {

    List<Media> findByTitleIgnoreCase(String title);

    List<Media> findByTitleContainingIgnoreCase(String title);

    List<Media> findByMediaType(MediaType mediaType);

    List<Media> findByGenresContaining(Genre genre);

    List<Media> findByGenres_NameIgnoreCase(String genreName);

    List<Media> findByMediaTypeAndGenres_NameIgnoreCase(MediaType mediaType, String genreName);
}