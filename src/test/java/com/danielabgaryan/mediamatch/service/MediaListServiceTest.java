package com.danielabgaryan.mediamatch.service;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.danielabgaryan.mediamatch.exception.DuplicateResourceException;
import com.danielabgaryan.mediamatch.exception.ForbiddenException;
import com.danielabgaryan.mediamatch.exception.ResourceNotFoundException;
import com.danielabgaryan.mediamatch.model.Media;
import com.danielabgaryan.mediamatch.model.MediaList;
import com.danielabgaryan.mediamatch.model.User;
import com.danielabgaryan.mediamatch.repository.MediaListRepository;
import com.danielabgaryan.mediamatch.repository.MediaRepository;
import com.danielabgaryan.mediamatch.repository.UserRepository;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.never;
import static org.mockito.ArgumentMatchers.any;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class MediaListServiceTest {
    @Mock
    private MediaListRepository mediaListRepository;

    @Mock
    private UserRepository userRepository;

    @Mock 
    private MediaRepository mediaRepository;

    @InjectMocks
    private MediaListService mediaListService;


    @Test
    void createMediaList_whenUserExists_savesMediaList() {
        User owner = new User();
        owner.setEmail("owner@example.com");
        
        when(userRepository.findByEmail("owner@example.com"))
            .thenReturn(Optional.of(owner));
        when(mediaListRepository.save(any(MediaList.class)))    
            .thenAnswer(invocation -> invocation.getArgument(0));
        
        MediaList result = mediaListService.createMediaList(
            "owner@example.com",
            "Favorites", 
            "My favorite media"
        );
        verify(userRepository).findByEmail("owner@example.com");
        verify(mediaListRepository).save(any(MediaList.class));
        
        assertEquals(owner, result.getUser());
        assertEquals("Favorites", result.getName());
        assertEquals("My favorite media", result.getDescription());
    } 
    
    @Test
    void createMediaList_whenUserDoesNotExist_throwsResourceNotFoundException() {
        when(userRepository.findByEmail("missing@example.com"))
            .thenReturn(Optional.empty());
        
        assertThrows(ResourceNotFoundException.class, () -> {
            mediaListService.createMediaList(
                "missing@example.com",
                "Favorites", 
                "My favorite media"
            );
        });

        verify(userRepository).findByEmail("missing@example.com");
        verify(mediaListRepository, never()).save(any(MediaList.class));
    }
    
    @Test
    void getMediaListById_whenMediaListExists_returnsMediaList() {
        MediaList mediaList = new MediaList();
        mediaList.setId(1L);

        when(mediaListRepository.findById(1L))
            .thenReturn(Optional.of(mediaList));
        
        MediaList result = mediaListService.getMediaListById(1L);
        
        assertEquals(mediaList, result);
        verify(mediaListRepository).findById(1L);
    }

    @Test
    void getMediaListById_whenMediaListDoesNotExist_throwsResourceNotFoundException() {
        when(mediaListRepository.findById(1L))
            .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            mediaListService.getMediaListById(1L);
        });
        verify(mediaListRepository).findById(1L);
    }

    @Test
    void getMediaListsByUserId_whenUserExists_returnsMediaLists() {
        MediaList firstList = new MediaList();
        MediaList secondList = new MediaList();
        List<MediaList> mediaLists = List.of(firstList, secondList);

        when(userRepository.existsById(1L)).thenReturn(true);
        when(mediaListRepository.findByUser_Id(1L)).thenReturn(mediaLists);

        List<MediaList> result = mediaListService.getMediaListsByUserId(1L);

        assertEquals(mediaLists, result);
        verify(userRepository).existsById(1L);
        verify(mediaListRepository).findByUser_Id(1L);
    }

    @Test
    void getMediaListsByUserId_whenUserDoesNotExist_throwsResourceNotFoundException() {
        when(userRepository.existsById(1L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () ->
            mediaListService.getMediaListsByUserId(1L)
        );

        verify(userRepository).existsById(1L);
        verify(mediaListRepository, never()).findByUser_Id(1L);
    }

    @Test
    void getMediaListsByName_returnsMatchingMediaLists() {
        List<MediaList> mediaLists = List.of(new MediaList());
        when(mediaListRepository.findByNameContainingIgnoreCase("Favorites"))
            .thenReturn(mediaLists);

        List<MediaList> result = mediaListService.getMediaListsByName("Favorites");

        assertEquals(mediaLists, result);
        verify(mediaListRepository).findByNameContainingIgnoreCase("Favorites");
    }

    @Test
    void getMediaListsByUserIdAndName_whenUserExists_returnsMatchingMediaLists() {
        List<MediaList> mediaLists = List.of(new MediaList());
        when(userRepository.existsById(1L)).thenReturn(true);
        when(mediaListRepository.findByUser_IdAndNameContainingIgnoreCase(1L, "Favorites"))
            .thenReturn(mediaLists);

        List<MediaList> result = mediaListService.getMediaListsByUserIdAndName(1L, "Favorites");

        assertEquals(mediaLists, result);
        verify(userRepository).existsById(1L);
        verify(mediaListRepository).findByUser_IdAndNameContainingIgnoreCase(1L, "Favorites");
    }

    @Test
    void getMediaListsByUserIdAndName_whenUserDoesNotExist_throwsResourceNotFoundException() {
        when(userRepository.existsById(1L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () ->
            mediaListService.getMediaListsByUserIdAndName(1L, "Favorites")
        );

        verify(userRepository).existsById(1L);
        verify(mediaListRepository, never())
            .findByUser_IdAndNameContainingIgnoreCase(1L, "Favorites");
    }

    @Test
    void updateMediaList_whenUserIsOwner_updatesAndSavesMediaList() {
        User owner = new User();
        owner.setEmail("owner@example.com");
        MediaList ownerMediaList = new MediaList();
        ownerMediaList.setUser(owner);
        ownerMediaList.setId(1L);
        when(mediaListRepository.findById(1L))
            .thenReturn(Optional.of(ownerMediaList));
        when(mediaListRepository.save(any(MediaList.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));
        MediaList result = mediaListService.updateMediaList(
            1L, 
            "owner@example.com", 
            "updatedName", 
            "updatedDescription"
        );
        assertEquals("updatedName", result.getName());
        assertEquals("updatedDescription", result.getDescription());
        verify(mediaListRepository).save(ownerMediaList);
        verify(mediaListRepository).findById(1L);
    }

    @Test
    void updateMediaList_whenUserIsNotOwner_throwsForbiddenException() {
        User owner = new User();
        owner.setEmail("owner@example.com");
        MediaList ownerMediaList = new MediaList();
        ownerMediaList.setUser(owner);
        ownerMediaList.setId(1L);
        
        when(mediaListRepository.findById(1L)).thenReturn(Optional.of(ownerMediaList));
        assertThrows(ForbiddenException.class, () -> {
            mediaListService.updateMediaList(
                ownerMediaList.getId(), 
                "notCorrect@example.com", 
                "updatedName",
                "updatedDescription"
            );
        });
        verify(mediaListRepository, never()).save(any(MediaList.class));
    }

    @Test
    void updateMediaList_whenMediaListDoesNotExist_throwsResourceNotFoundException() {
        when(mediaListRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () ->
            mediaListService.updateMediaList(
                1L,
                "owner@example.com",
                "updatedName",
                "updatedDescription"
            )
        );

        verify(mediaListRepository).findById(1L);
        verify(mediaListRepository, never()).save(any(MediaList.class));
    }

    @Test
    void addMediaToList_whenUserIsOwner_addsMediaAndSavesMediaList() {
        MediaList ownerList = createOwnedMediaList();
        Media media = createMedia(2L);
        when(mediaListRepository.findById(1L)).thenReturn(Optional.of(ownerList));
        when(mediaRepository.findById(2L)).thenReturn(Optional.of(media));
        when(mediaListRepository.save(ownerList)).thenReturn(ownerList);

        MediaList result = mediaListService.addMediaToList(1L, "owner@example.com", 2L);

        assertEquals(ownerList, result);
        assertTrue(result.getMedia().contains(media));
        verify(mediaListRepository).save(ownerList);
    }

    @Test
    void addMediaToList_whenMediaListDoesNotExist_throwsResourceNotFoundException() {
        when(mediaListRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () ->
            mediaListService.addMediaToList(1L, "owner@example.com", 2L)
        );

        verify(mediaRepository, never()).findById(2L);
        verify(mediaListRepository, never()).save(any(MediaList.class));
    }

    @Test
    void addMediaToList_whenUserIsNotOwner_throwsForbiddenException() {
        MediaList ownerList = createOwnedMediaList();
        when(mediaListRepository.findById(1L)).thenReturn(Optional.of(ownerList));

        assertThrows(ForbiddenException.class, () ->
            mediaListService.addMediaToList(1L, "different@example.com", 2L)
        );

        verify(mediaRepository, never()).findById(2L);
        verify(mediaListRepository, never()).save(any(MediaList.class));
    }

    @Test
    void addMediaToList_whenMediaDoesNotExist_throwsResourceNotFoundException() {
        MediaList ownerList = createOwnedMediaList();
        when(mediaListRepository.findById(1L)).thenReturn(Optional.of(ownerList));
        when(mediaRepository.findById(2L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () ->
            mediaListService.addMediaToList(1L, "owner@example.com", 2L)
        );

        verify(mediaListRepository, never()).save(any(MediaList.class));
    }

    @Test
    void addMediaToList_whenMediaAlreadyExists_throwsDuplicateResourceException() {
        MediaList ownerList = createOwnedMediaList();
        Media media = createMedia(2L);
        ownerList.getMedia().add(media);
        when(mediaListRepository.findById(1L)).thenReturn(Optional.of(ownerList));
        when(mediaRepository.findById(2L)).thenReturn(Optional.of(media));

        assertThrows(DuplicateResourceException.class, () ->
            mediaListService.addMediaToList(1L, "owner@example.com", 2L)
        );

        verify(mediaListRepository, never()).save(any(MediaList.class));
    }

    @Test
    void removeMediaFromList_whenUserIsOwner_removesMediaAndSavesMediaList() {
        MediaList ownerList = createOwnedMediaList();
        Media media = createMedia(2L);
        ownerList.getMedia().add(media);
        when(mediaListRepository.findById(1L)).thenReturn(Optional.of(ownerList));
        when(mediaRepository.findById(2L)).thenReturn(Optional.of(media));
        when(mediaListRepository.save(ownerList)).thenReturn(ownerList);

        MediaList result = mediaListService.removeMediaFromList(1L, "owner@example.com", 2L);

        assertEquals(ownerList, result);
        assertFalse(result.getMedia().contains(media));
        verify(mediaListRepository).save(ownerList);
    }

    @Test
    void removeMediaFromList_whenMediaListDoesNotExist_throwsResourceNotFoundException() {
        when(mediaListRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () ->
            mediaListService.removeMediaFromList(1L, "owner@example.com", 2L)
        );

        verify(mediaRepository, never()).findById(2L);
        verify(mediaListRepository, never()).save(any(MediaList.class));
    }

    @Test
    void removeMediaFromList_whenUserIsNotOwner_throwsForbiddenException() {
        MediaList ownerList = createOwnedMediaList();
        when(mediaListRepository.findById(1L)).thenReturn(Optional.of(ownerList));

        assertThrows(ForbiddenException.class, () ->
            mediaListService.removeMediaFromList(1L, "different@example.com", 2L)
        );

        verify(mediaRepository, never()).findById(2L);
        verify(mediaListRepository, never()).save(any(MediaList.class));
    }

    @Test
    void removeMediaFromList_whenMediaDoesNotExist_throwsResourceNotFoundException() {
        MediaList ownerList = createOwnedMediaList();
        when(mediaListRepository.findById(1L)).thenReturn(Optional.of(ownerList));
        when(mediaRepository.findById(2L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () ->
            mediaListService.removeMediaFromList(1L, "owner@example.com", 2L)
        );

        verify(mediaListRepository, never()).save(any(MediaList.class));
    }

    @Test
    void removeMediaFromList_whenMediaIsNotInList_throwsResourceNotFoundException() {
        MediaList ownerList = createOwnedMediaList();
        Media media = createMedia(2L);
        when(mediaListRepository.findById(1L)).thenReturn(Optional.of(ownerList));
        when(mediaRepository.findById(2L)).thenReturn(Optional.of(media));

        assertThrows(ResourceNotFoundException.class, () ->
            mediaListService.removeMediaFromList(1L, "owner@example.com", 2L)
        );

        verify(mediaListRepository, never()).save(any(MediaList.class));
    }

    @Test
    void deleteMediaList_whenUserIsOwner_deletesMediaList() {
        User owner = new User();
        owner.setEmail("owner@example.com");

        MediaList ownerList = new MediaList();
        ownerList.setId(1L);
        ownerList.setUser(owner);   

        when(mediaListRepository.findById(1L))
            .thenReturn(Optional.of(ownerList));
        mediaListService.deleteMediaList(1L, "owner@example.com");

        verify(mediaListRepository).findById(1L);
        verify(mediaListRepository).delete(ownerList);
    }

    @Test
    void deleteMediaList_whenUserIsNotTheOwner_throwsForbiddenException() {
        User owner = new User();
        owner.setEmail("owner@example.com");

        MediaList ownerList = new MediaList();
        ownerList.setUser(owner);

        when(mediaListRepository.findById(1L))
            .thenReturn(Optional.of(ownerList));
        
        assertThrows(ForbiddenException.class, () -> {
            mediaListService.deleteMediaList(
                1L, 
                "missing@example.com");
        });

        verify(mediaListRepository).findById(1L);
        verify(mediaListRepository, never()).delete(any(MediaList.class));
    }

    @Test
    void deleteMediaList_whenMediaListDoesNotExist_throwsResourceNotFoundException() {
        when(mediaListRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () ->
            mediaListService.deleteMediaList(1L, "owner@example.com")
        );

        verify(mediaListRepository).findById(1L);
        verify(mediaListRepository, never()).delete(any(MediaList.class));
    }

    private MediaList createOwnedMediaList() {
        User owner = new User();
        owner.setEmail("owner@example.com");

        MediaList mediaList = new MediaList();
        mediaList.setId(1L);
        mediaList.setUser(owner);
        return mediaList;
    }

    private Media createMedia(Long id) {
        Media media = new Media();
        media.setId(id);
        return media;
    }

}
