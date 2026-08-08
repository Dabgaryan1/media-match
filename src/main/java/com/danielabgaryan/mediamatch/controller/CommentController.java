package com.danielabgaryan.mediamatch.controller;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;
import com.danielabgaryan.mediamatch.dto.CreateCommentRequest;
import com.danielabgaryan.mediamatch.model.Comment;
import com.danielabgaryan.mediamatch.service.CommentService;

@RestController
@RequestMapping("/comments")
public class CommentController {
    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @PostMapping
    public Comment createComment(@RequestBody CreateCommentRequest request) {
        return commentService.createComment(
            request.getUserId(),
            request.getMediaListId(),
            request.getContent()
        );
    }

    @GetMapping("/{id}")
    public Comment getCommentById(@PathVariable Long id) {
        return commentService.getCommentById(id);
    }

    @GetMapping("/media-list/{mediaListId}")
    public List<Comment> getCommentsByMediaList(@PathVariable Long mediaListId) {
        return commentService.getCommentsByMediaList(mediaListId);
    }

    @GetMapping("/user/{userId}")
    public List<Comment> getCommentsByUser(@PathVariable Long userId) {
        return commentService.getCommentsByUser(userId);
    }

    @DeleteMapping("/{id}")
    public void deleteComment(@PathVariable Long id) {
        commentService.deleteComment(id);
    }
}
