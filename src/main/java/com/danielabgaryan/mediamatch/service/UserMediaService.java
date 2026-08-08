package com.danielabgaryan.mediamatch.service;

import org.springframework.stereotype.Service;
import com.danielabgaryan.mediamatch.repository.UserMediaRepository;
import com.danielabgaryan.mediamatch.repository.UserRepository;
import com.danielabgaryan.mediamatch.repository.MediaRepository;
import com.danielabgaryan.mediamatch.model.UserMedia;
import com.danielabgaryan.mediamatch.model.User;
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

    public UserMedia addMediaToUser(Long userId, Long mediaId, Status status) {
        User user = getUserOrThrow(userId);

        Media media = mediaRepository.findById(mediaId).orElseThrow(() -> new RuntimeException("Media not found"));

        if (userMediaRepository.existsByUser_IdAndMedia_Id(userId, mediaId)) {
            throw new RuntimeException("Media is already in user's library");
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
        return userMediaRepository.findById(userMediaId).orElseThrow(() -> new RuntimeException("User media not found"));
    }

    public UserMedia updateStatus(Long userMediaId, Status status) {
        UserMedia userMedia = getUserMediaById(userMediaId);
        userMedia.setStatus(status);

        return userMediaRepository.save(userMedia);
    }

    public UserMedia updateRating(Long userMediaId, Integer rating) {
        if (rating != null && (rating < 1 || rating > 5)) {
            throw new RuntimeException("Rating must be between 1 and 5");
        }
        UserMedia userMedia = getUserMediaById(userMediaId);
        userMedia.setRating(rating);

        return userMediaRepository.save(userMedia);
    }

    public UserMedia updateFavorite(Long userMediaId, boolean favorite) {
        UserMedia userMedia = getUserMediaById(userMediaId);
        userMedia.setFavorite(favorite);

        return userMediaRepository.save(userMedia);
    }

    public void removeMediaFromUser(Long userMediaId) {
        UserMedia userMedia = getUserMediaById(userMediaId);
        userMediaRepository.delete(userMedia);
    }
    
    public List<UserMedia> getUserFavorites(Long userId) {
        getUserOrThrow(userId);
        return userMediaRepository.findByUser_IdAndFavoriteTrue(userId);
    }
    
    //helper method to check if user exists
    private User getUserOrThrow(Long userId) {
        return userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
    }
}
