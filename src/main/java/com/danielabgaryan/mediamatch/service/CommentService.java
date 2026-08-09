package com.danielabgaryan.mediamatch.service;

import org.springframework.stereotype.Service;
import com.danielabgaryan.mediamatch.repository.CommentRepository;
import com.danielabgaryan.mediamatch.repository.MediaListRepository;
import com.danielabgaryan.mediamatch.repository.UserRepository;
import com.danielabgaryan.mediamatch.exception.ResourceNotFoundException;
import com.danielabgaryan.mediamatch.model.Comment;
import com.danielabgaryan.mediamatch.model.User;
import com.danielabgaryan.mediamatch.model.MediaList;
import java.util.List;

@Service
public class CommentService {
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final MediaListRepository mediaListRepository;

    public CommentService(CommentRepository commentRepository, UserRepository userRepository, MediaListRepository mediaListRepository) {
        this.commentRepository = commentRepository;
        this.userRepository = userRepository;
        this.mediaListRepository = mediaListRepository;
    }

    public Comment createComment(Long userId, Long mediaListId, String content) {
        User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        MediaList mediaList = mediaListRepository.findById(mediaListId).orElseThrow(() -> new ResourceNotFoundException("Media list not found"));
        
        Comment comment = new Comment(user, mediaList, content);
        return commentRepository.save(comment);
    }

    public void deleteComment(Long commentId) {
        Comment comment = getCommentById(commentId);
        commentRepository.delete(comment);
    }

    public List<Comment> getCommentsByMediaList(Long mediaListId) {
        if(!mediaListRepository.existsById(mediaListId)) {
            throw new ResourceNotFoundException("Media list not found");
        }
        return commentRepository.findByMediaList_Id(mediaListId);
    }

    public List<Comment> getCommentsByUser(Long userId) {
        if(!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User not found");
        }
        return commentRepository.findByUser_Id(userId);
    }

    public Comment getCommentById(Long commentId) {
        return commentRepository.findById(commentId).orElseThrow(() -> new ResourceNotFoundException("Comment not found"));
    }   
}
