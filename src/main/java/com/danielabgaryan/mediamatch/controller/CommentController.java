package com.danielabgaryan.mediamatch.controller;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

import com.danielabgaryan.mediamatch.dto.CommentResponse;
import com.danielabgaryan.mediamatch.dto.CreateCommentRequest;
import com.danielabgaryan.mediamatch.dto.MediaListResponse;
import com.danielabgaryan.mediamatch.dto.UserResponse;
import com.danielabgaryan.mediamatch.model.Comment;
import com.danielabgaryan.mediamatch.model.MediaList;
import com.danielabgaryan.mediamatch.model.User;
import com.danielabgaryan.mediamatch.service.CommentService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/comments")
public class CommentController {
    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @PostMapping
    public CommentResponse createComment(@Valid @RequestBody CreateCommentRequest request, Authentication authentication) {
        String email = authentication.getName();

        Comment comment = commentService.createComment(
            email,
            request.getMediaListId(),
            request.getContent()
        );
        return toCommentResponse(comment);
    }

    @GetMapping("/{id}")
    public CommentResponse getCommentById(@PathVariable Long id) {
        Comment comment = commentService.getCommentById(id);
        return toCommentResponse(comment);
    }

    @GetMapping("/media-list/{mediaListId}")
    public List<CommentResponse> getCommentsByMediaList(@PathVariable Long mediaListId) {
        return commentService.getCommentsByMediaList(mediaListId).stream()
        .map(this::toCommentResponse).toList();
    }

    @GetMapping("/user/{userId}")
    public List<CommentResponse> getCommentsByUser(@PathVariable Long userId) {
        return commentService.getCommentsByUser(userId).stream()
        .map(this::toCommentResponse).toList();
    }

    @DeleteMapping("/{id}")
    public void deleteComment(@PathVariable Long id, Authentication authentication) {
        String email = authentication.getName();
        commentService.deleteComment(id, email);
    }

    public CommentResponse toCommentResponse(Comment comment) {
        User user = comment.getUser();
        MediaList mediaList = comment.getMediaList();
        
        UserResponse userResponse = new UserResponse(
            user.getId(),
            user.getUsername(),
            user.getEmail(),
            user.getBio(),
            user.getProfilePictureUrl()
        );
        
        User mediaListOwner = mediaList.getUser();

        UserResponse mediaListOwnerResponse = new UserResponse(
            mediaListOwner.getId(),
            mediaListOwner.getUsername(),
            mediaListOwner.getEmail(),
            mediaListOwner.getBio(),
            mediaListOwner.getProfilePictureUrl()
        );

        MediaListResponse mediaListResponse = new MediaListResponse(
            mediaList.getId(),
            mediaList.getName(),
            mediaList.getDescription(),
            mediaListOwnerResponse
        );

        return new CommentResponse(
            comment.getId(),
            userResponse,
            mediaListResponse,
            comment.getContent(),
            comment.getCreatedAt()
        );
    }
}
