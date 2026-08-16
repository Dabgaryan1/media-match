package com.danielabgaryan.mediamatch.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.danielabgaryan.mediamatch.exception.DuplicateResourceException;
import com.danielabgaryan.mediamatch.exception.ForbiddenException;
import com.danielabgaryan.mediamatch.exception.ResourceNotFoundException;
import com.danielabgaryan.mediamatch.model.User;
import com.danielabgaryan.mediamatch.repository.UserRepository;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User getUserById(Long userId) {
        return userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    public User getUserByUsername(String userName) {
        return userRepository.findByUsername(userName).orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow(() -> new ResourceNotFoundException("Email not found"));
    }

    public User createUser(String userName, String email, String password) {
        if (userRepository.existsByEmail(email)) {
            throw new DuplicateResourceException("Email already taken");
        }
        if (userRepository.existsByUsername(userName)) {
            throw new DuplicateResourceException("Username already taken");
        }

        User user = new User();
        user.setUsername(userName);
        user.setEmail(email);

        String hashedPassword = passwordEncoder.encode(password);
        user.setPasswordHash(hashedPassword);

        return userRepository.save(user);
    }

    public User updateUser(Long userId, String authenticatedEmail, String userName, String email, String password) {
        User user = getUserById(userId);
        verifyOwnership(user, authenticatedEmail);

        userRepository.findByUsername(userName).filter(existingUser -> !existingUser.getId().equals(userId)).ifPresent(existingUser -> {
            throw new DuplicateResourceException("Username already taken");
        });

        userRepository.findByEmail(email).filter(existingUser -> !existingUser.getId().equals(userId)).ifPresent(existingUser -> {
            throw new DuplicateResourceException("Email already taken");
        });

        user.setUsername(userName);
        user.setEmail(email);

        String hashedPassword = passwordEncoder.encode(password);
        user.setPasswordHash(hashedPassword);

        return userRepository.save(user);
    }
    
    public void deleteUser(Long userId, String authenticatedEmail) {
        User user = getUserById(userId);
        verifyOwnership(user, authenticatedEmail);
        userRepository.delete(user);
    }

    private void verifyOwnership(User user, String authenticatedEmail) {
        if (!user.getEmail().equals(authenticatedEmail)) {
            throw new ForbiddenException("You cannot modify this account");
        }
    }
}