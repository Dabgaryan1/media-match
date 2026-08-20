package com.danielabgaryan.mediamatch.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.danielabgaryan.mediamatch.exception.ForbiddenException;
import com.danielabgaryan.mediamatch.exception.ResourceNotFoundException;
import com.danielabgaryan.mediamatch.model.Comment;
import com.danielabgaryan.mediamatch.model.MediaList;
import com.danielabgaryan.mediamatch.model.User;
import com.danielabgaryan.mediamatch.repository.CommentRepository;
import com.danielabgaryan.mediamatch.repository.MediaListRepository;
import com.danielabgaryan.mediamatch.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
public class CommentServiceTest {
    @Mock
    private CommentRepository commentRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private MediaListRepository mediaListRepository;

    @InjectMocks
    private CommentService commentService;

    @Test
    void createComment_whenUserAndMediaListExist_savesComment() {
        User user = createUser("owner@example.com");
        MediaList mediaList = new MediaList();
        when(userRepository.findByEmail("owner@example.com")).thenReturn(Optional.of(user));
        when(mediaListRepository.findById(2L)).thenReturn(Optional.of(mediaList));
        when(commentRepository.save(any(Comment.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));

        Comment result = commentService.createComment("owner@example.com", 2L, "Great list");

        assertSame(user, result.getUser());
        assertSame(mediaList, result.getMediaList());
        assertEquals("Great list", result.getContent());
        verify(commentRepository).save(result);
    }

    @Test
    void createComment_whenUserDoesNotExist_throwsResourceNotFoundException() {
        when(userRepository.findByEmail("missing@example.com")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
            () -> commentService.createComment("missing@example.com", 2L, "Comment"));

        verify(mediaListRepository, never()).findById(2L);
        verify(commentRepository, never()).save(any(Comment.class));
    }

    @Test
    void createComment_whenMediaListDoesNotExist_throwsResourceNotFoundException() {
        when(userRepository.findByEmail("owner@example.com"))
            .thenReturn(Optional.of(createUser("owner@example.com")));
        when(mediaListRepository.findById(2L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
            () -> commentService.createComment("owner@example.com", 2L, "Comment"));

        verify(commentRepository, never()).save(any(Comment.class));
    }

    @Test
    void getCommentById_whenCommentExists_returnsComment() {
        Comment comment = createOwnedComment();
        when(commentRepository.findById(3L)).thenReturn(Optional.of(comment));

        assertSame(comment, commentService.getCommentById(3L));
    }

    @Test
    void getCommentById_whenCommentDoesNotExist_throwsResourceNotFoundException() {
        when(commentRepository.findById(3L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> commentService.getCommentById(3L));
    }

    @Test
    void deleteComment_whenUserIsOwner_deletesComment() {
        Comment comment = createOwnedComment();
        when(commentRepository.findById(3L)).thenReturn(Optional.of(comment));

        commentService.deleteComment(3L, "owner@example.com");

        verify(commentRepository).delete(comment);
    }

    @Test
    void deleteComment_whenUserIsNotOwner_throwsForbiddenException() {
        Comment comment = createOwnedComment();
        when(commentRepository.findById(3L)).thenReturn(Optional.of(comment));

        assertThrows(ForbiddenException.class,
            () -> commentService.deleteComment(3L, "other@example.com"));

        verify(commentRepository, never()).delete(any(Comment.class));
    }

    @Test
    void getCommentsByMediaList_whenMediaListExists_returnsComments() {
        List<Comment> comments = List.of(createOwnedComment());
        when(mediaListRepository.findById(2L)).thenReturn(Optional.of(new MediaList()));
        when(commentRepository.findByMediaList_Id(2L)).thenReturn(comments);

        assertSame(comments, commentService.getCommentsByMediaList(2L));
        verify(commentRepository).findByMediaList_Id(2L);
    }

    @Test
    void getCommentsByMediaList_whenMediaListDoesNotExist_throwsResourceNotFoundException() {
        when(mediaListRepository.findById(2L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
            () -> commentService.getCommentsByMediaList(2L));

        verify(commentRepository, never()).findByMediaList_Id(2L);
    }

    @Test
    void getCommentsByUser_whenUserExists_returnsComments() {
        List<Comment> comments = List.of(createOwnedComment());
        when(userRepository.findById(1L)).thenReturn(Optional.of(createUser("owner@example.com")));
        when(commentRepository.findByUser_Id(1L)).thenReturn(comments);

        assertSame(comments, commentService.getCommentsByUser(1L));
        verify(commentRepository).findByUser_Id(1L);
    }

    @Test
    void getCommentsByUser_whenUserDoesNotExist_throwsResourceNotFoundException() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> commentService.getCommentsByUser(1L));
        verify(commentRepository, never()).findByUser_Id(1L);
    }

    private Comment createOwnedComment() {
        return new Comment(createUser("owner@example.com"), new MediaList(), "Comment");
    }

    private User createUser(String email) {
        User user = new User();
        user.setEmail(email);
        return user;
    }
}
