package com.danielabgaryan.mediamatch.service;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.danielabgaryan.mediamatch.exception.ForbiddenException;
import com.danielabgaryan.mediamatch.model.MediaList;
import com.danielabgaryan.mediamatch.model.User;
import com.danielabgaryan.mediamatch.repository.MediaListRepository;
import com.danielabgaryan.mediamatch.repository.MediaRepository;
import com.danielabgaryan.mediamatch.repository.UserRepository;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.never;
import static org.mockito.ArgumentMatchers.any;
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
    void updateMediaList_whenUserIsNotOwner_throwsForbiddenException() {
        //Arrange
        User owner = new User();
        owner.setEmail("owner@example.com");

        MediaList ownerMediaList = new MediaList();
        ownerMediaList.setUser(owner);
        ownerMediaList.setId(1L);

        when(mediaListRepository.findById(1L)).thenReturn(Optional.of(ownerMediaList));

        //act and assert
        assertThrows(ForbiddenException.class, () -> {
            mediaListService.updateMediaList(
                ownerMediaList.getId(), 
                "notCorrect@example.com", 
                "updatedName",
                "updatedDescription"
            );
        });

        //verify
        verify(mediaListRepository, never()).save(any(MediaList.class));
    }

    @Test
    void updateMediaList_whenUserIsOwner_updatesAndSavesMediaList() {
        //arrange
        User owner = new User();
        owner.setEmail("owner@example.com");

        MediaList ownerMediaList = new MediaList();
        ownerMediaList.setUser(owner);
        ownerMediaList.setId(1L);

        when(mediaListRepository.findById(1L))
            .thenReturn(Optional.of(ownerMediaList));

        when(mediaListRepository.save(any(MediaList.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));


        //act and assert
        MediaList result = mediaListService.updateMediaList(
            1L, 
            "owner@example.com", 
            "updatedName", 
            "updatedDescription"
        );

        assertEquals("updatedName", result.getName());
        assertEquals("updatedDescription", result.getDescription());

        //verify
        verify(mediaListRepository).save(ownerMediaList);
    }

    
}
