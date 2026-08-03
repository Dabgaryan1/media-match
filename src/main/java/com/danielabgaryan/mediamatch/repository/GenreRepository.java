package com.danielabgaryan.mediamatch.repository;

import com.danielabgaryan.mediamatch.model.Genre;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.List;

public interface GenreRepository extends JpaRepository<Genre, Long> {
    
    Optional<Genre> findByNameIgnoreCase(String name);

    boolean existsByNameIgnoreCase(String name);

    List<Genre> findByNameContainingIgnoreCase(String name);
}
