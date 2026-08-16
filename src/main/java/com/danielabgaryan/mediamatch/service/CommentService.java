package com.danielabgaryan.mediamatch.service;

import org.springframework.stereotype.Service;
import com.danielabgaryan.mediamatch.repository.CommentRepository;
import com.danielabgaryan.mediamatch.repository.MediaListRepository;
import com.danielabgaryan.mediamatch.repository.UserRepository;
import com.danielabgaryan.mediamatch.exception.ForbiddenException;
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

    public Comment createComment(String email, Long mediaListId, String content) {
        User user = userRepository.findByEmail(email).orElseThrow(() ->
            new ResourceNotFoundException("User not found"));
        
        MediaList mediaList = getMediaListOrThrow(mediaListId);
        Comment comment = new Comment(user, mediaList, content);
        return commentRepository.save(comment);
    }

    public void deleteComment(Long commentId, String email) {
        Comment comment = getCommentById(commentId);
        verifyOwnership(comment, email);
        commentRepository.delete(comment);
    }

    public List<Comment> getCommentsByMediaList(Long mediaListId) {
        getMediaListOrThrow(mediaListId);
        return commentRepository.findByMediaList_Id(mediaListId);
    }

    public List<Comment> getCommentsByUser(Long userId) {
        getUserOrThrow(userId);
        return commentRepository.findByUser_Id(userId);
    }

    public Comment getCommentById(Long commentId) {
        return commentRepository.findById(commentId).orElseThrow(() -> new ResourceNotFoundException("Comment not found"));
    }

    //helper method to get User or throw a ResourceNotFoundException
    private User getUserOrThrow(Long userId) {
        return userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User Not found"));
    }

    //helper method to get Media List or throw a ResourceNotFoundException
    private MediaList getMediaListOrThrow(Long mediaListId) {
        return mediaListRepository.findById(mediaListId).orElseThrow(() -> new ResourceNotFoundException("Media List not found"));
    }

    private void verifyOwnership(Comment comment, String email) {
        if (!comment.getUser().getEmail().equals(email)) {
            throw new ForbiddenException("You do not own this comment");
        }
    }
}
