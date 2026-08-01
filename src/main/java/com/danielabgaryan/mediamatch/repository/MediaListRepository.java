package com.danielabgaryan.mediamatch.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.danielabgaryan.mediamatch.model.MediaList;

public interface MediaListRepository  extends JpaRepository<MediaList, Long> {
    
}
