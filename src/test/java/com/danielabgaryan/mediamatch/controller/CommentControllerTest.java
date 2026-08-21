package com.danielabgaryan.mediamatch.controller;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.danielabgaryan.mediamatch.exception.ForbiddenException;
import com.danielabgaryan.mediamatch.exception.ResourceNotFoundException;
import com.danielabgaryan.mediamatch.model.Comment;
import com.danielabgaryan.mediamatch.model.MediaList;
import com.danielabgaryan.mediamatch.model.User;
import com.danielabgaryan.mediamatch.service.CommentService;

@WebMvcTest(CommentController.class)
public class CommentControllerTest {
    private static final LocalDateTime CREATED_AT = LocalDateTime.of(2026, 8, 20, 12, 30);

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CommentService commentService;

    @Test
    void createComment_whenRequestIsValid_returnsCommentResponse() throws Exception {
        Authentication authentication = mock(Authentication.class);
        Comment comment = createComment(1L, "Great list");

        when(authentication.getName()).thenReturn("author@example.com");
        when(commentService.createComment("author@example.com", 10L, "Great list"))
            .thenReturn(comment);

        mockMvc.perform(post("/comments")
                .principal(authentication)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "mediaListId": 10,
                        "content": "Great list"
                    }
                    """))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.user.id").value(2))
            .andExpect(jsonPath("$.mediaList.id").value(10))
            .andExpect(jsonPath("$.content").value("Great list"))
            .andExpect(jsonPath("$.createdAt").value("2026-08-20T12:30:00"));

        verify(commentService).createComment("author@example.com", 10L, "Great list");
    }

    @Test
    void createComment_whenRequestIsInvalid_returnsValidationErrors() throws Exception {
        mockMvc.perform(post("/comments")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "content": ""
                    }
                    """))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.mediaListId").value("Media List ID is required"))
            .andExpect(jsonPath("$.content").value("Comment is required"));

        verify(commentService, never()).createComment(anyString(), anyLong(), anyString());
    }

    @Test
    void getCommentById_whenCommentExists_returnsCommentResponse() throws Exception {
        Comment comment = createComment(1L, "Great list");
        when(commentService.getCommentById(1L)).thenReturn(comment);

        mockMvc.perform(get("/comments/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.content").value("Great list"))
            .andExpect(jsonPath("$.user.id").value(2))
            .andExpect(jsonPath("$.user.username").value("commentAuthor"))
            .andExpect(jsonPath("$.user.email").value("author@example.com"))
            .andExpect(jsonPath("$.user.bio").value("Author bio"))
            .andExpect(jsonPath("$.user.profilePictureUrl").value("author-picture.jpg"))
            .andExpect(jsonPath("$.mediaList.id").value(10))
            .andExpect(jsonPath("$.mediaList.name").value("Favorite Movies"))
            .andExpect(jsonPath("$.mediaList.description").value("My favorite movies"))
            .andExpect(jsonPath("$.mediaList.user.id").value(1))
            .andExpect(jsonPath("$.mediaList.user.username").value("listOwner"))
            .andExpect(jsonPath("$.mediaList.user.email").value("owner@example.com"))
            .andExpect(jsonPath("$.createdAt").value("2026-08-20T12:30:00"));

        verify(commentService).getCommentById(1L);
    }

    @Test
    void getCommentById_whenCommentDoesNotExist_returnsNotFound() throws Exception {
        when(commentService.getCommentById(1L))
            .thenThrow(new ResourceNotFoundException("Comment not found"));

        mockMvc.perform(get("/comments/1"))
            .andExpect(status().isNotFound())
            .andExpect(content().string("Comment not found"));

        verify(commentService).getCommentById(1L);
    }

    @Test
    void getCommentsByMediaList_returnsCommentResponses() throws Exception {
        List<Comment> comments = List.of(
            createComment(1L, "First comment"),
            createComment(2L, "Second comment")
        );

        when(commentService.getCommentsByMediaList(10L))
            .thenReturn(comments);

        mockMvc.perform(get("/comments/media-list/10"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(2))
            .andExpect(jsonPath("$[0].id").value(1))
            .andExpect(jsonPath("$[0].content").value("First comment"))
            .andExpect(jsonPath("$[0].mediaList.id").value(10))
            .andExpect(jsonPath("$[1].id").value(2))
            .andExpect(jsonPath("$[1].content").value("Second comment"));

        verify(commentService).getCommentsByMediaList(10L);
    }

    @Test
    void getCommentsByUser_returnsCommentResponses() throws Exception {
        List<Comment> comments = List.of(
            createComment(1L, "First comment"),
            createComment(2L, "Second comment")
        );

        when(commentService.getCommentsByUser(2L))
            .thenReturn(comments);

        mockMvc.perform(get("/comments/user/2"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(2))
            .andExpect(jsonPath("$[0].id").value(1))
            .andExpect(jsonPath("$[0].user.id").value(2))
            .andExpect(jsonPath("$[0].content").value("First comment"))
            .andExpect(jsonPath("$[1].id").value(2))
            .andExpect(jsonPath("$[1].content").value("Second comment"));

        verify(commentService).getCommentsByUser(2L);
    }

    @Test
    void deleteComment_whenUserOwnsComment_returnsOk() throws Exception {
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("author@example.com");

        mockMvc.perform(delete("/comments/1").principal(authentication))
            .andExpect(status().isOk())
            .andExpect(content().string(""));

        verify(commentService).deleteComment(1L, "author@example.com");
    }

    @Test
    void deleteComment_whenUserDoesNotOwnComment_returnsForbidden() throws Exception {
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("other@example.com");
        org.mockito.Mockito.doThrow(new ForbiddenException("You do not own this comment"))
            .when(commentService).deleteComment(1L, "other@example.com");

        mockMvc.perform(delete("/comments/1").principal(authentication))
            .andExpect(status().isForbidden())
            .andExpect(content().string("You do not own this comment"));

        verify(commentService).deleteComment(1L, "other@example.com");
    }

    private Comment createComment(Long id, String content) {
        User listOwner = mockUser(1L, "listOwner", "owner@example.com", "Owner bio", "owner-picture.jpg");
        MediaList mediaList = new MediaList(listOwner, "Favorite Movies", "My favorite movies");
        mediaList.setId(10L);

        User commentAuthor = mockUser(
            2L,
            "commentAuthor",
            "author@example.com",
            "Author bio",
            "author-picture.jpg"
        );

        Comment comment = mock(Comment.class);
        when(comment.getId()).thenReturn(id);
        when(comment.getUser()).thenReturn(commentAuthor);
        when(comment.getMediaList()).thenReturn(mediaList);
        when(comment.getContent()).thenReturn(content);
        when(comment.getCreatedAt()).thenReturn(CREATED_AT);
        return comment;
    }

    private User mockUser(Long id, String username, String email, String bio, String profilePictureUrl) {
        User user = mock(User.class);
        when(user.getId()).thenReturn(id);
        when(user.getUsername()).thenReturn(username);
        when(user.getEmail()).thenReturn(email);
        when(user.getBio()).thenReturn(bio);
        when(user.getProfilePictureUrl()).thenReturn(profilePictureUrl);
        return user;
    }
}
