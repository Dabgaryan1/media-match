package com.danielabgaryan.mediamatch.service;

import org.springframework.stereotype.Service;
import com.danielabgaryan.mediamatch.repository.MediaListRepository;
import com.danielabgaryan.mediamatch.repository.UserRepository;
import com.danielabgaryan.mediamatch.model.MediaList;
import com.danielabgaryan.mediamatch.model.User;
import com.danielabgaryan.mediamatch.repository.MediaRepository;
import com.danielabgaryan.mediamatch.exception.DuplicateResourceException;
import com.danielabgaryan.mediamatch.exception.ForbiddenException;
import com.danielabgaryan.mediamatch.exception.ResourceNotFoundException;
import com.danielabgaryan.mediamatch.model.Media;
import java.util.List;

@Service
public class MediaListService {
    private final MediaListRepository mediaListRepository;
    private final UserRepository userRepository;
    private final MediaRepository mediaRepository;

    public MediaListService(MediaListRepository mediaListRepository, UserRepository userRepository, MediaRepository mediaRepository) {
        this.mediaListRepository = mediaListRepository;
        this.userRepository = userRepository;
        this.mediaRepository = mediaRepository;
    }


    public MediaList createMediaList(String email, String name, String description) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new ResourceNotFoundException("User not found"));

        MediaList mediaList = new MediaList(
            user,
            name,
            description
        );
        
        return mediaListRepository.save(mediaList);
    }

    public List<MediaList> getMediaListsByUserId(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User not found");
        }

        return mediaListRepository.findByUser_Id(userId);
    }

    public List<MediaList> getMediaListsByName(String name) {
        return mediaListRepository.findByNameContainingIgnoreCase(name);
    }

    public List<MediaList> getMediaListsByUserIdAndName(Long userId, String name) {
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User not found");
        }

        return mediaListRepository.findByUser_IdAndNameContainingIgnoreCase(userId, name);
    }

    public MediaList getMediaListById(Long mediaListId) {
        return mediaListRepository.findById(mediaListId).orElseThrow(() -> new ResourceNotFoundException("Media list not found"));
    }

    public MediaList addMediaToList(Long mediaListId, String email, Long mediaId) {
        MediaList mediaList = getMediaListById(mediaListId);

        verifyOwnership(mediaList, email);
        
        Media media = mediaRepository.findById(mediaId).orElseThrow(() -> new ResourceNotFoundException("Media not found"));

        boolean added = mediaList.getMedia().add(media);
        if (!added) {
            throw new DuplicateResourceException("Media already exists in the list");
        }

        return mediaListRepository.save(mediaList);
    }

    public MediaList removeMediaFromList(Long mediaListId, String email, Long mediaId) {
        MediaList mediaList = getMediaListById(mediaListId);

        verifyOwnership(mediaList, email);
        
        Media media = mediaRepository.findById(mediaId).orElseThrow(() -> new ResourceNotFoundException("Media not found"));

        boolean removed = mediaList.getMedia().remove(media);
        if (!removed) {
            throw new ResourceNotFoundException("Media not found in the list");
        }

        return mediaListRepository.save(mediaList);
    }

    public void deleteMediaList(Long mediaListId, String email) {
        MediaList mediaList = getMediaListById(mediaListId);
        verifyOwnership(mediaList, email);

        mediaListRepository.delete(mediaList);
    }

    public MediaList updateMediaList(Long mediaListId, String email, String name, String description) {
        MediaList mediaList = getMediaListById(mediaListId);
        verifyOwnership(mediaList, email);

        mediaList.setName(name);
        mediaList.setDescription(description);

        return mediaListRepository.save(mediaList);
    }

    private void verifyOwnership(MediaList mediaList, String email) {
        if (!mediaList.getUser().getEmail().equals(email)) {
            throw new ForbiddenException("You do not own this media list");
        }
    }
}
