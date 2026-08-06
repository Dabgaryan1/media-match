package com.danielabgaryan.mediamatch.service;

import org.springframework.stereotype.Service;
import com.danielabgaryan.mediamatch.model.User;
import com.danielabgaryan.mediamatch.repository.UserRepository;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User getUserById(Long userId) {
        return userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
    }

    public User getUserByUsername(String userName) {
        return userRepository.findByUsername(userName).orElseThrow(() -> new RuntimeException("User not found"));
    }

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("Email not found"));
    }

    public User createUser(String userName, String email, String passwordHash) {
        if (userRepository.existsByEmail(email)) {
            throw new RuntimeException("Email already taken");
        }
        if (userRepository.existsByUsername(userName)) {
            throw new RuntimeException("Username already taken");
        }

        User user = new User();
        user.setUsername(userName);
        user.setEmail(email);
        user.setPasswordHash(passwordHash);

        return userRepository.save(user);
    }

    public User updateUser(Long userId, String userName, String email, String passwordHash) {
        User user = getUserById(userId);

        userRepository.findByUsername(userName).filter(existingUser -> !existingUser.getId().equals(userId)).ifPresent(existingUser -> {
            throw new RuntimeException("Username already taken");
        });

        userRepository.findByEmail(email).filter(existingUser -> !existingUser.getId().equals(userId)).ifPresent(existingUser -> {
            throw new RuntimeException("Email already taken");
        });

        user.setUsername(userName);
        user.setEmail(email);
        user.setPasswordHash(passwordHash);

        return userRepository.save(user);
    }
    
    public void deleteUser(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new RuntimeException("User not found");
        }
        userRepository.deleteById(userId);
    }
}