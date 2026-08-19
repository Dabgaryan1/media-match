package com.danielabgaryan.mediamatch.controller;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.danielabgaryan.mediamatch.dto.CreateUserRequest;
import com.danielabgaryan.mediamatch.dto.UpdatePasswordRequest;
import com.danielabgaryan.mediamatch.dto.UpdateUserRequest;
import com.danielabgaryan.mediamatch.dto.UserResponse;
import com.danielabgaryan.mediamatch.exception.ForbiddenException;
import com.danielabgaryan.mediamatch.model.User;
import com.danielabgaryan.mediamatch.service.UserService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public UserResponse createUser(@Valid @RequestBody CreateUserRequest request) {
        User user = userService.createUser(
            request.getUserName(),
            request.getEmail(),
            request.getPassword()
        );

        return toUserResponse(user);
    }

    @GetMapping("/{id}")
    public UserResponse getUserById(@PathVariable Long id) {
        return toUserResponse(userService.getUserById(id));
    }
    @GetMapping("/username/{userName}")
    public UserResponse getUserByUsername(@PathVariable String userName) {
        return toUserResponse(userService.getUserByUsername(userName));
    }

    @GetMapping("/email/{email}")
    public UserResponse getUserByEmail(@PathVariable String email, Authentication authentication) {
        if (!authentication.getName().equals(email)) {
            throw new ForbiddenException("You cannot access this user's email lookup");
        }
        return toUserResponse(userService.getUserByEmail(email));
    }

    @PutMapping("/{id}")
    public UserResponse updateUser(@PathVariable Long id, @Valid @RequestBody UpdateUserRequest request, Authentication authentication) {
        String authenticatedEmail = authentication.getName();
        User user = userService.updateUser(
            id,
            authenticatedEmail,
            request.getUsername(),
            request.getEmail()
        );

        return toUserResponse(user);
    }

    @PutMapping("/{id}/password")
    public UserResponse updatePassword(@PathVariable Long id, @Valid @RequestBody UpdatePasswordRequest request, Authentication authentication) {
        String authenticatedEmail = authentication.getName();

        User user = userService.updatePassword(
            id,
            authenticatedEmail,
            request.getCurrentPassword(),
            request.getNewPassword()
        );

        return toUserResponse(user);
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable Long id, Authentication authentication) {
        String authenticatedEmail = authentication.getName();
        userService.deleteUser(id, authenticatedEmail);
    }

    //helper function to convert User to UserResponse
    private UserResponse toUserResponse(User user) {
        return new UserResponse(user.getId(), user.getUsername(), user.getEmail(), user.getBio(), user.getProfilePictureUrl());
    }
}
