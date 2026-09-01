package com.danielabgaryan.mediamatch.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.danielabgaryan.mediamatch.model.MediaList;
import java.util.List;

public interface MediaListRepository extends JpaRepository<MediaList, Long> {

    List<MediaList> findByNameContainingIgnoreCase(String name);

    List<MediaList> findByUser_Id(Long userId);

    List<MediaList> findByUser_IdAndNameContainingIgnoreCase(Long userId, String name);

}
