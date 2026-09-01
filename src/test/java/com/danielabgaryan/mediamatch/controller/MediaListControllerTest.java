package com.danielabgaryan.mediamatch.controller;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
import com.danielabgaryan.mediamatch.model.MediaList;
import com.danielabgaryan.mediamatch.model.User;
import com.danielabgaryan.mediamatch.service.MediaListService;

@WebMvcTest(MediaListController.class)
public class MediaListControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private MediaListService mediaListService;

    @Test
    void createMediaList_whenRequestIsValid_returnsMediaListResponse() throws Exception {
        Authentication authentication = authentication("owner@example.com");
        MediaList mediaList = mediaList(10L, "Favorite Movies", "My favorite movies");
        when(mediaListService.createMediaList("owner@example.com", "Favorite Movies", "My favorite movies"))
            .thenReturn(mediaList);

        mockMvc.perform(post("/media-lists")
                .principal(authentication)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "name": "Favorite Movies",
                        "description": "My favorite movies"
                    }
                    """))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(10))
            .andExpect(jsonPath("$.name").value("Favorite Movies"))
            .andExpect(jsonPath("$.description").value("My favorite movies"))
            .andExpect(jsonPath("$.user.id").value(1))
            .andExpect(jsonPath("$.user.username").value("listOwner"))
            .andExpect(jsonPath("$.user.email").value("owner@example.com"))
            .andExpect(jsonPath("$.user.passwordHash").doesNotExist());

        verify(mediaListService).createMediaList("owner@example.com", "Favorite Movies", "My favorite movies");
    }

    @Test
    void createMediaList_whenRequestIsInvalid_returnsValidationErrors() throws Exception {
        mockMvc.perform(post("/media-lists")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "name": "",
                        "description": ""
                    }
                    """))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.name").value("Name is required"))
            .andExpect(jsonPath("$.description").value("Description is required"));

        verify(mediaListService, never()).createMediaList(anyString(), anyString(), anyString());
    }

    @Test
    void getMediaListById_whenListExists_returnsMediaListResponse() throws Exception {
        MediaList mediaList = mediaList(10L, "Favorite Movies", "My favorite movies");
        when(mediaListService.getMediaListById(10L)).thenReturn(mediaList);

        mockMvc.perform(get("/media-lists/10"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(10))
            .andExpect(jsonPath("$.name").value("Favorite Movies"))
            .andExpect(jsonPath("$.user.email").value("owner@example.com"));

        verify(mediaListService).getMediaListById(10L);
    }

    @Test
    void getMediaListById_whenListDoesNotExist_returnsNotFound() throws Exception {
        when(mediaListService.getMediaListById(10L))
            .thenThrow(new ResourceNotFoundException("Media list not found"));

        mockMvc.perform(get("/media-lists/10"))
            .andExpect(status().isNotFound())
            .andExpect(content().string("Media list not found"));

        verify(mediaListService).getMediaListById(10L);
    }

    @Test
    void getMediaListsByUserId_returnsMediaListResponses() throws Exception {
        List<MediaList> mediaLists = List.of(
            mediaList(10L, "Favorite Movies", "My favorite movies"),
            mediaList(11L, "Watch Later", "Movies to watch later")
        );
        when(mediaListService.getMediaListsByUserId(1L)).thenReturn(mediaLists);

        mockMvc.perform(get("/media-lists/user/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(2))
            .andExpect(jsonPath("$[0].id").value(10))
            .andExpect(jsonPath("$[0].name").value("Favorite Movies"))
            .andExpect(jsonPath("$[1].id").value(11))
            .andExpect(jsonPath("$[1].name").value("Watch Later"));

        verify(mediaListService).getMediaListsByUserId(1L);
    }

    @Test
    void searchMediaLists_returnsMediaListResponses() throws Exception {
        List<MediaList> mediaLists = List.of(mediaList(10L, "Favorites", "My favorites"));
        when(mediaListService.getMediaListsByName("Favorites")).thenReturn(mediaLists);

        mockMvc.perform(get("/media-lists/search").param("query", "Favorites"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(1))
            .andExpect(jsonPath("$[0].id").value(10))
            .andExpect(jsonPath("$[0].name").value("Favorites"));

        verify(mediaListService).getMediaListsByName("Favorites");
    }

    @Test
    void searchMediaLists_whenQueryIsMissing_returnsBadRequest() throws Exception {
        mockMvc.perform(get("/media-lists/search"))
            .andExpect(status().isBadRequest());

        verify(mediaListService, never()).getMediaListsByName(anyString());
    }

    @Test
    void getMediaListsByUserIdAndName_returnsMediaListResponses() throws Exception {
        List<MediaList> mediaLists = List.of(mediaList(10L, "Favorites", "My favorites"));
        when(mediaListService.getMediaListsByUserIdAndName(1L, "Favorites")).thenReturn(mediaLists);

        mockMvc.perform(get("/media-lists/user/1/name/Favorites"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(1))
            .andExpect(jsonPath("$[0].id").value(10))
            .andExpect(jsonPath("$[0].user.id").value(1));

        verify(mediaListService).getMediaListsByUserIdAndName(1L, "Favorites");
    }

    @Test
    void updateMediaList_whenRequestIsValid_returnsUpdatedMediaListResponse() throws Exception {
        Authentication authentication = authentication("owner@example.com");
        MediaList updatedList = mediaList(10L, "Updated List", "Updated description");
        when(mediaListService.updateMediaList(10L, "owner@example.com", "Updated List", "Updated description"))
            .thenReturn(updatedList);

        mockMvc.perform(put("/media-lists/10")
                .principal(authentication)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "name": "Updated List",
                        "description": "Updated description"
                    }
                    """))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(10))
            .andExpect(jsonPath("$.name").value("Updated List"))
            .andExpect(jsonPath("$.description").value("Updated description"));

        verify(mediaListService)
            .updateMediaList(10L, "owner@example.com", "Updated List", "Updated description");
    }

    @Test
    void addMediaToList_passesAuthenticatedEmailAndIdsToService() throws Exception {
        Authentication authentication = authentication("owner@example.com");
        MediaList mediaList = mediaList(10L, "Favorite Movies", "My favorite movies");
        when(mediaListService.addMediaToList(10L, "owner@example.com", 20L)).thenReturn(mediaList);

        mockMvc.perform(put("/media-lists/10/media/20").principal(authentication))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(10));

        verify(mediaListService).addMediaToList(10L, "owner@example.com", 20L);
    }

    @Test
    void addMediaToList_whenUserDoesNotOwnList_returnsForbidden() throws Exception {
        Authentication authentication = authentication("other@example.com");
        when(mediaListService.addMediaToList(10L, "other@example.com", 20L))
            .thenThrow(new ForbiddenException("You do not own this media list"));

        mockMvc.perform(put("/media-lists/10/media/20").principal(authentication))
            .andExpect(status().isForbidden())
            .andExpect(content().string("You do not own this media list"));

        verify(mediaListService).addMediaToList(10L, "other@example.com", 20L);
    }

    @Test
    void removeMediaFromList_passesAuthenticatedEmailAndIdsToService() throws Exception {
        Authentication authentication = authentication("owner@example.com");
        MediaList mediaList = mediaList(10L, "Favorite Movies", "My favorite movies");
        when(mediaListService.removeMediaFromList(10L, "owner@example.com", 20L)).thenReturn(mediaList);

        mockMvc.perform(delete("/media-lists/10/media/20").principal(authentication))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(10));

        verify(mediaListService).removeMediaFromList(10L, "owner@example.com", 20L);
    }

    @Test
    void deleteMediaList_passesAuthenticatedEmailAndIdToService() throws Exception {
        Authentication authentication = authentication("owner@example.com");

        mockMvc.perform(delete("/media-lists/10").principal(authentication))
            .andExpect(status().isOk())
            .andExpect(content().string(""));

        verify(mediaListService).deleteMediaList(10L, "owner@example.com");
    }

    private Authentication authentication(String email) {
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn(email);
        return authentication;
    }

    private MediaList mediaList(Long id, String name, String description) {
        User owner = mock(User.class);
        when(owner.getId()).thenReturn(1L);
        when(owner.getUsername()).thenReturn("listOwner");
        when(owner.getEmail()).thenReturn("owner@example.com");
        when(owner.getBio()).thenReturn("Owner bio");
        when(owner.getProfilePictureUrl()).thenReturn("owner-picture.jpg");

        MediaList mediaList = new MediaList(owner, name, description);
        mediaList.setId(id);
        return mediaList;
    }
}
