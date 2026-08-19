package com.danielabgaryan.mediamatch.service;

import org.springframework.stereotype.Service;
import com.danielabgaryan.mediamatch.repository.UserMediaRepository;
import com.danielabgaryan.mediamatch.repository.UserRepository;
import com.danielabgaryan.mediamatch.repository.MediaRepository;
import com.danielabgaryan.mediamatch.model.UserMedia;
import com.danielabgaryan.mediamatch.model.User;
import com.danielabgaryan.mediamatch.exception.DuplicateResourceException;
import com.danielabgaryan.mediamatch.exception.ForbiddenException;
import com.danielabgaryan.mediamatch.exception.ResourceNotFoundException;
import com.danielabgaryan.mediamatch.model.Media;
import com.danielabgaryan.mediamatch.model.Status;
import java.util.List;

@Service
public class UserMediaService {
    private final UserMediaRepository userMediaRepository;
    private final UserRepository userRepository;
    private final MediaRepository mediaRepository;

    public UserMediaService(UserMediaRepository userMediaRepository, UserRepository userRepository, MediaRepository mediaRepository) {
        this.userMediaRepository = userMediaRepository;
        this.userRepository = userRepository;
        this.mediaRepository = mediaRepository;
    }

    public UserMedia addMediaToUser(String email, Long mediaId, Status status) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        Media media = mediaRepository.findById(mediaId).orElseThrow(() -> new ResourceNotFoundException("Media not found"));

        if (userMediaRepository.existsByUser_IdAndMedia_Id(user.getId(), mediaId)) {
            throw new DuplicateResourceException("Media is already in user's library");
        }

        UserMedia userMedia = new UserMedia();
        userMedia.setUser(user);
        userMedia.setMedia(media);
        userMedia.setStatus(status);

        return userMediaRepository.save(userMedia);
    }
    
    public List<UserMedia> getUserLibrary(Long userId) {
        getUserOrThrow(userId);

        return userMediaRepository.findByUser_Id(userId);
    }

    public UserMedia getUserMediaById(Long userMediaId) {
        return userMediaRepository.findById(userMediaId).orElseThrow(() -> new ResourceNotFoundException("User media not found"));
    }

    public UserMedia updateStatus(Long userMediaId, String email, Status status) {
        UserMedia userMedia = getUserMediaById(userMediaId);
        verifyOwnership(userMedia, email);
        userMedia.setStatus(status);

        return userMediaRepository.save(userMedia);
    }

    public UserMedia updateRating(Long userMediaId, String email, Integer rating) {
        UserMedia userMedia = getUserMediaById(userMediaId);
        verifyOwnership(userMedia, email);

        userMedia.setRating(rating);

        return userMediaRepository.save(userMedia);
    }

    public UserMedia updateFavorite(Long userMediaId, String email, boolean favorite) {
        UserMedia userMedia = getUserMediaById(userMediaId);
        verifyOwnership(userMedia, email);
        userMedia.setFavorite(favorite);

        return userMediaRepository.save(userMedia);
    }

    public void removeMediaFromUser(Long userMediaId, String email) {
        UserMedia userMedia = getUserMediaById(userMediaId);
        verifyOwnership(userMedia, email);
        userMediaRepository.delete(userMedia);
    }
    
    public List<UserMedia> getUserFavorites(Long userId) {
        getUserOrThrow(userId);
        return userMediaRepository.findByUser_IdAndFavoriteTrue(userId);
    }
    
    //helper method to check if user exists
    private User getUserOrThrow(Long userId) {
        return userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    private void verifyOwnership(UserMedia userMedia, String email) {
        if (!userMedia.getUser().getEmail().equals(email)) {
            throw new ForbiddenException("You do not own this user media");
        }
    }
}
