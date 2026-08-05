package com.danielabgaryan.mediamatch.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;
import com.danielabgaryan.mediamatch.model.Status;
import com.danielabgaryan.mediamatch.model.UserMedia;


public interface UserMediaRepository extends JpaRepository<UserMedia, Long> {

    List<UserMedia> findByUser_Id(Long userId);

    Optional<UserMedia> findByUser_IdAndMedia_Id(Long userId, Long mediaId);

    boolean existsByUser_IdAndMedia_Id(Long userId, Long mediaId);

    List<UserMedia> findByUser_IdAndStatus(Long userId, Status status);

    List<UserMedia> findByUser_IdAndFavoriteTrue(Long userId);

}
