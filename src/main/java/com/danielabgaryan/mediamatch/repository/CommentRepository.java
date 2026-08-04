package com.danielabgaryan.mediamatch.repository;

import com.danielabgaryan.mediamatch.model.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    
    List<Comment> findByUser_Id(Long id);

    List<Comment> findByMediaList_Id(Long id);

    List<Comment> findByUser_IdAndMediaList_Id(Long userId, Long mediaListId);

    Optional<Comment> findByIdAndUser_Id(Long commentId, Long userId);
}
