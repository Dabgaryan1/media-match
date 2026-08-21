package com.danielabgaryan.mediamatch.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
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

import com.danielabgaryan.mediamatch.exception.DuplicateResourceException;
import com.danielabgaryan.mediamatch.exception.ForbiddenException;
import com.danielabgaryan.mediamatch.exception.ResourceNotFoundException;
import com.danielabgaryan.mediamatch.model.Media;
import com.danielabgaryan.mediamatch.model.Status;
import com.danielabgaryan.mediamatch.model.User;
import com.danielabgaryan.mediamatch.model.UserMedia;
import com.danielabgaryan.mediamatch.repository.MediaRepository;
import com.danielabgaryan.mediamatch.repository.UserMediaRepository;
import com.danielabgaryan.mediamatch.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
public class UserMediaServiceTest {
    @Mock
    private UserMediaRepository userMediaRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private MediaRepository mediaRepository;

    @InjectMocks
    private UserMediaService userMediaService;

    @Test
    void addMediaToUser_whenUserAndMediaExist_savesUserMedia() {
        User user = mock(User.class);
        Media media = new Media();
        when(user.getId()).thenReturn(1L);
        when(userRepository.findByEmail("owner@example.com")).thenReturn(Optional.of(user));
        when(mediaRepository.findById(2L)).thenReturn(Optional.of(media));
        when(userMediaRepository.existsByUser_IdAndMedia_Id(1L, 2L)).thenReturn(false);
        when(userMediaRepository.save(any(UserMedia.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));

        UserMedia result = userMediaService.addMediaToUser(
            "owner@example.com", 2L, Status.PLANNED);

        assertSame(user, result.getUser());
        assertSame(media, result.getMedia());
        assertEquals(Status.PLANNED, result.getStatus());
        verify(userMediaRepository).save(result);
    }

    @Test
    void addMediaToUser_whenUserDoesNotExist_throwsResourceNotFoundException() {
        when(userRepository.findByEmail("missing@example.com")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
            () -> userMediaService.addMediaToUser("missing@example.com", 2L, Status.PLANNED));

        verify(mediaRepository, never()).findById(2L);
        verify(userMediaRepository, never()).save(any(UserMedia.class));
    }

    @Test
    void addMediaToUser_whenMediaDoesNotExist_throwsResourceNotFoundException() {
        User user = new User();
        when(userRepository.findByEmail("owner@example.com")).thenReturn(Optional.of(user));
        when(mediaRepository.findById(2L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
            () -> userMediaService.addMediaToUser("owner@example.com", 2L, Status.PLANNED));

        verify(userMediaRepository, never()).save(any(UserMedia.class));
    }

    @Test
    void addMediaToUser_whenEntryAlreadyExists_throwsDuplicateResourceException() {
        User user = mock(User.class);
        when(user.getId()).thenReturn(1L);
        when(userRepository.findByEmail("owner@example.com")).thenReturn(Optional.of(user));
        when(mediaRepository.findById(2L)).thenReturn(Optional.of(new Media()));
        when(userMediaRepository.existsByUser_IdAndMedia_Id(1L, 2L)).thenReturn(true);

        assertThrows(DuplicateResourceException.class,
            () -> userMediaService.addMediaToUser("owner@example.com", 2L, Status.PLANNED));

        verify(userMediaRepository, never()).save(any(UserMedia.class));
    }

    @Test
    void getUserLibrary_whenUserExists_returnsLibrary() {
        List<UserMedia> library = List.of(new UserMedia(), new UserMedia());
        when(userRepository.findById(1L)).thenReturn(Optional.of(new User()));
        when(userMediaRepository.findByUser_Id(1L)).thenReturn(library);

        List<UserMedia> result = userMediaService.getUserLibrary(1L);

        assertSame(library, result);
        verify(userMediaRepository).findByUser_Id(1L);
    }

    @Test
    void getUserLibrary_whenUserDoesNotExist_throwsResourceNotFoundException() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> userMediaService.getUserLibrary(1L));
        verify(userMediaRepository, never()).findByUser_Id(1L);
    }

    @Test
    void getUserMediaById_whenEntryExists_returnsEntry() {
        UserMedia userMedia = new UserMedia();
        when(userMediaRepository.findById(3L)).thenReturn(Optional.of(userMedia));

        assertSame(userMedia, userMediaService.getUserMediaById(3L));
    }

    @Test
    void getUserMediaById_whenEntryDoesNotExist_throwsResourceNotFoundException() {
        when(userMediaRepository.findById(3L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
            () -> userMediaService.getUserMediaById(3L));
    }

    @Test
    void updateStatus_whenUserIsOwner_updatesAndSavesStatus() {
        UserMedia userMedia = createOwnedUserMedia();
        when(userMediaRepository.findById(3L)).thenReturn(Optional.of(userMedia));
        when(userMediaRepository.save(userMedia)).thenReturn(userMedia);

        UserMedia result = userMediaService.updateStatus(
            3L, "owner@example.com", Status.COMPLETED);

        assertEquals(Status.COMPLETED, result.getStatus());
        verify(userMediaRepository).save(userMedia);
    }

    @Test
    void updateStatus_whenUserIsNotOwner_throwsForbiddenException() {
        UserMedia userMedia = createOwnedUserMedia();
        when(userMediaRepository.findById(3L)).thenReturn(Optional.of(userMedia));

        assertThrows(ForbiddenException.class,
            () -> userMediaService.updateStatus(3L, "other@example.com", Status.COMPLETED));

        assertEquals(Status.PLANNED, userMedia.getStatus());
        verify(userMediaRepository, never()).save(any(UserMedia.class));
    }

    @Test
    void updateRating_whenUserIsOwner_updatesAndSavesRating() {
        UserMedia userMedia = createOwnedUserMedia();
        when(userMediaRepository.findById(3L)).thenReturn(Optional.of(userMedia));
        when(userMediaRepository.save(userMedia)).thenReturn(userMedia);

        UserMedia result = userMediaService.updateRating(3L, "owner@example.com", 5);

        assertEquals(5, result.getRating());
        verify(userMediaRepository).save(userMedia);
    }

    @Test
    void updateRating_whenUserIsNotOwner_throwsForbiddenException() {
        UserMedia userMedia = createOwnedUserMedia();
        when(userMediaRepository.findById(3L)).thenReturn(Optional.of(userMedia));

        assertThrows(ForbiddenException.class,
            () -> userMediaService.updateRating(3L, "other@example.com", 5));

        verify(userMediaRepository, never()).save(any(UserMedia.class));
    }

    @Test
    void updateFavorite_whenUserIsOwner_updatesAndSavesFavorite() {
        UserMedia userMedia = createOwnedUserMedia();
        assertFalse(userMedia.getFavorite());
        when(userMediaRepository.findById(3L)).thenReturn(Optional.of(userMedia));
        when(userMediaRepository.save(userMedia)).thenReturn(userMedia);

        UserMedia result = userMediaService.updateFavorite(3L, "owner@example.com", true);

        assertTrue(result.getFavorite());
        verify(userMediaRepository).save(userMedia);
    }

    @Test
    void updateFavorite_whenUserIsNotOwner_throwsForbiddenException() {
        UserMedia userMedia = createOwnedUserMedia();
        when(userMediaRepository.findById(3L)).thenReturn(Optional.of(userMedia));

        assertThrows(ForbiddenException.class,
            () -> userMediaService.updateFavorite(3L, "other@example.com", true));

        assertFalse(userMedia.getFavorite());
        verify(userMediaRepository, never()).save(any(UserMedia.class));
    }

    @Test
    void removeMediaFromUser_whenUserIsOwner_deletesEntry() {
        UserMedia userMedia = createOwnedUserMedia();
        when(userMediaRepository.findById(3L)).thenReturn(Optional.of(userMedia));

        userMediaService.removeMediaFromUser(3L, "owner@example.com");

        verify(userMediaRepository).delete(userMedia);
    }

    @Test
    void removeMediaFromUser_whenUserIsNotOwner_throwsForbiddenException() {
        UserMedia userMedia = createOwnedUserMedia();
        when(userMediaRepository.findById(3L)).thenReturn(Optional.of(userMedia));

        assertThrows(ForbiddenException.class,
            () -> userMediaService.removeMediaFromUser(3L, "other@example.com"));

        verify(userMediaRepository, never()).delete(any(UserMedia.class));
    }

    @Test
    void getUserFavorites_whenUserExists_returnsFavorites() {
        List<UserMedia> favorites = List.of(new UserMedia());
        when(userRepository.findById(1L)).thenReturn(Optional.of(new User()));
        when(userMediaRepository.findByUser_IdAndFavoriteTrue(1L)).thenReturn(favorites);

        List<UserMedia> result = userMediaService.getUserFavorites(1L);

        assertSame(favorites, result);
        verify(userMediaRepository).findByUser_IdAndFavoriteTrue(1L);
    }

    @Test
    void getUserFavorites_whenUserDoesNotExist_throwsResourceNotFoundException() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
            () -> userMediaService.getUserFavorites(1L));

        verify(userMediaRepository, never()).findByUser_IdAndFavoriteTrue(1L);
    }

    private UserMedia createOwnedUserMedia() {
        User owner = new User();
        owner.setEmail("owner@example.com");

        UserMedia userMedia = new UserMedia();
        userMedia.setUser(owner);
        userMedia.setStatus(Status.PLANNED);
        return userMedia;
    }
}
