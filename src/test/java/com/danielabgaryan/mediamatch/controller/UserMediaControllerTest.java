package com.danielabgaryan.mediamatch.controller;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.danielabgaryan.mediamatch.exception.ForbiddenException;
import com.danielabgaryan.mediamatch.exception.ResourceNotFoundException;
import com.danielabgaryan.mediamatch.model.Media;
import com.danielabgaryan.mediamatch.model.Status;
import com.danielabgaryan.mediamatch.model.User;
import com.danielabgaryan.mediamatch.model.UserMedia;
import com.danielabgaryan.mediamatch.service.UserMediaService;

@WebMvcTest(UserMediaController.class)
public class UserMediaControllerTest {
    private static final LocalDateTime ADDED_AT = LocalDateTime.of(2026, 8, 20, 12, 30);

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserMediaService userMediaService;

    @Test
    void addMediaToUser_whenRequestIsValid_returnsUserMediaResponse() throws Exception {
        Authentication authentication = authentication("user@example.com");
        UserMedia userMedia = userMedia(1L, Status.PLANNED, null, false);
        when(userMediaService.addMediaToUser("user@example.com", 20L, Status.PLANNED))
            .thenReturn(userMedia);

        mockMvc.perform(post("/user-medias")
                .principal(authentication)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "mediaId": 20,
                        "status": "PLANNED"
                    }
                    """))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.user.id").value(2))
            .andExpect(jsonPath("$.user.email").value("user@example.com"))
            .andExpect(jsonPath("$.user.passwordHash").doesNotExist())
            .andExpect(jsonPath("$.media.id").value(20))
            .andExpect(jsonPath("$.media.title").value("Test Movie"))
            .andExpect(jsonPath("$.status").value("PLANNED"))
            .andExpect(jsonPath("$.rating").doesNotExist())
            .andExpect(jsonPath("$.favorite").value(false))
            .andExpect(jsonPath("$.addedAt").value("2026-08-20T12:30:00"));

        verify(userMediaService).addMediaToUser("user@example.com", 20L, Status.PLANNED);
    }

    @Test
    void addMediaToUser_whenRequestIsInvalid_returnsValidationErrors() throws Exception {
        mockMvc.perform(post("/user-medias")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.mediaId").value("Media ID is required"))
            .andExpect(jsonPath("$.status").value("Status is required"));

        verifyNoInteractions(userMediaService);
    }

    @Test
    void getUserMediaById_whenEntryExists_returnsUserMediaResponse() throws Exception {
        UserMedia userMedia = userMedia(1L, Status.COMPLETED, 5, true);
        when(userMediaService.getUserMediaById(1L)).thenReturn(userMedia);

        mockMvc.perform(get("/user-medias/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.status").value("COMPLETED"))
            .andExpect(jsonPath("$.rating").value(5))
            .andExpect(jsonPath("$.favorite").value(true));

        verify(userMediaService).getUserMediaById(1L);
    }

    @Test
    void getUserMediaById_whenEntryDoesNotExist_returnsNotFound() throws Exception {
        when(userMediaService.getUserMediaById(1L))
            .thenThrow(new ResourceNotFoundException("User media not found"));

        mockMvc.perform(get("/user-medias/1"))
            .andExpect(status().isNotFound())
            .andExpect(content().string("User media not found"));

        verify(userMediaService).getUserMediaById(1L);
    }

    @Test
    void getUserLibrary_returnsUserMediaResponses() throws Exception {
        List<UserMedia> library = List.of(
            userMedia(1L, Status.PLANNED, null, false),
            userMedia(2L, Status.COMPLETED, 5, true)
        );
        when(userMediaService.getUserLibrary(2L)).thenReturn(library);

        mockMvc.perform(get("/user-medias/user/2"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(2))
            .andExpect(jsonPath("$[0].id").value(1))
            .andExpect(jsonPath("$[0].status").value("PLANNED"))
            .andExpect(jsonPath("$[1].id").value(2))
            .andExpect(jsonPath("$[1].status").value("COMPLETED"));

        verify(userMediaService).getUserLibrary(2L);
    }

    @Test
    void getUserFavorites_returnsFavoriteUserMediaResponses() throws Exception {
        List<UserMedia> favorites = List.of(userMedia(2L, Status.COMPLETED, 5, true));
        when(userMediaService.getUserFavorites(2L)).thenReturn(favorites);

        mockMvc.perform(get("/user-medias/user/2/favorites"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(1))
            .andExpect(jsonPath("$[0].id").value(2))
            .andExpect(jsonPath("$[0].favorite").value(true));

        verify(userMediaService).getUserFavorites(2L);
    }

    @Test
    void updateStatus_whenRequestIsValid_returnsUpdatedUserMediaResponse() throws Exception {
        Authentication authentication = authentication("user@example.com");
        UserMedia userMedia = userMedia(1L, Status.IN_PROGRESS, null, false);
        when(userMediaService.updateStatus(1L, "user@example.com", Status.IN_PROGRESS))
            .thenReturn(userMedia);

        mockMvc.perform(put("/user-medias/1/status")
                .principal(authentication)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "status": "IN_PROGRESS"
                    }
                    """))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.status").value("IN_PROGRESS"));

        verify(userMediaService).updateStatus(1L, "user@example.com", Status.IN_PROGRESS);
    }

    @Test
    void updateStatus_whenStatusIsMissing_returnsValidationError() throws Exception {
        mockMvc.perform(put("/user-medias/1/status")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.status").value("Status is required"));

        verifyNoInteractions(userMediaService);
    }

    @Test
    void updateRating_whenRequestIsValid_returnsUpdatedUserMediaResponse() throws Exception {
        Authentication authentication = authentication("user@example.com");
        UserMedia userMedia = userMedia(1L, Status.COMPLETED, 5, false);
        when(userMediaService.updateRating(1L, "user@example.com", 5)).thenReturn(userMedia);

        mockMvc.perform(put("/user-medias/1/rating")
                .principal(authentication)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "rating": 5
                    }
                    """))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.rating").value(5));

        verify(userMediaService).updateRating(1L, "user@example.com", 5);
    }

    @Test
    void updateRating_whenRatingIsOutOfRange_returnsValidationError() throws Exception {
        mockMvc.perform(put("/user-medias/1/rating")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "rating": 6
                    }
                    """))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.rating").value("must be less than or equal to 5"));

        verifyNoInteractions(userMediaService);
    }

    @Test
    void updateFavorite_returnsUpdatedUserMediaResponse() throws Exception {
        Authentication authentication = authentication("user@example.com");
        UserMedia userMedia = userMedia(1L, Status.PLANNED, null, true);
        when(userMediaService.updateFavorite(1L, "user@example.com", true)).thenReturn(userMedia);

        mockMvc.perform(put("/user-medias/1/favorite")
                .principal(authentication)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "favorite": true
                    }
                    """))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.favorite").value(true));

        verify(userMediaService).updateFavorite(1L, "user@example.com", true);
    }

    @Test
    void updateStatus_whenUserDoesNotOwnEntry_returnsForbidden() throws Exception {
        Authentication authentication = authentication("other@example.com");
        when(userMediaService.updateStatus(1L, "other@example.com", Status.COMPLETED))
            .thenThrow(new ForbiddenException("You do not own this user media"));

        mockMvc.perform(put("/user-medias/1/status")
                .principal(authentication)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "status": "COMPLETED"
                    }
                    """))
            .andExpect(status().isForbidden())
            .andExpect(content().string("You do not own this user media"));

        verify(userMediaService).updateStatus(1L, "other@example.com", Status.COMPLETED);
    }

    @Test
    void removeMediaFromUser_passesAuthenticatedEmailAndIdToService() throws Exception {
        Authentication authentication = authentication("user@example.com");

        mockMvc.perform(delete("/user-medias/1").principal(authentication))
            .andExpect(status().isOk())
            .andExpect(content().string(""));

        verify(userMediaService).removeMediaFromUser(1L, "user@example.com");
    }

    private Authentication authentication(String email) {
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn(email);
        return authentication;
    }

    private UserMedia userMedia(Long id, Status status, Integer rating, boolean favorite) {
        User user = mock(User.class);
        when(user.getId()).thenReturn(2L);
        when(user.getUsername()).thenReturn("testuser");
        when(user.getEmail()).thenReturn("user@example.com");
        when(user.getBio()).thenReturn("Test bio");
        when(user.getProfilePictureUrl()).thenReturn("picture.jpg");

        Media media = new Media(
            "Test Movie",
            "Test description",
            LocalDate.of(2025, 1, 1),
            "movie.jpg",
            com.danielabgaryan.mediamatch.model.MediaType.MOVIE,
            Set.of()
        );
        media.setId(20L);

        UserMedia userMedia = mock(UserMedia.class);
        when(userMedia.getId()).thenReturn(id);
        when(userMedia.getUser()).thenReturn(user);
        when(userMedia.getMedia()).thenReturn(media);
        when(userMedia.getStatus()).thenReturn(status);
        when(userMedia.getRating()).thenReturn(rating);
        when(userMedia.getFavorite()).thenReturn(favorite);
        when(userMedia.getAddedAt()).thenReturn(ADDED_AT);
        return userMedia;
    }
}
