package com.danielabgaryan.mediamatch.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.danielabgaryan.mediamatch.exception.DuplicateResourceException;
import com.danielabgaryan.mediamatch.exception.ForbiddenException;
import com.danielabgaryan.mediamatch.exception.InvalidRequestException;
import com.danielabgaryan.mediamatch.exception.ResourceNotFoundException;
import com.danielabgaryan.mediamatch.model.User;
import com.danielabgaryan.mediamatch.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @Test
    void getUserById_whenUserExists_returnsUser() {
        User user = createUser("owner", "owner@example.com", "hash");
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        assertSame(user, userService.getUserById(1L));
        verify(userRepository).findById(1L);
    }

    @Test
    void getUserById_whenUserDoesNotExist_throwsResourceNotFoundException() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> userService.getUserById(1L));
    }

    @Test
    void getUserByUsername_whenUserExists_returnsUser() {
        User user = createUser("owner", "owner@example.com", "hash");
        when(userRepository.findByUsername("owner")).thenReturn(Optional.of(user));

        assertSame(user, userService.getUserByUsername("owner"));
    }

    @Test
    void getUserByUsername_whenUserDoesNotExist_throwsResourceNotFoundException() {
        when(userRepository.findByUsername("missing")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> userService.getUserByUsername("missing"));
    }

    @Test
    void getUserByEmail_whenUserExists_returnsUser() {
        User user = createUser("owner", "owner@example.com", "hash");
        when(userRepository.findByEmail("owner@example.com")).thenReturn(Optional.of(user));

        assertSame(user, userService.getUserByEmail("owner@example.com"));
    }

    @Test
    void getUserByEmail_whenUserDoesNotExist_throwsResourceNotFoundException() {
        when(userRepository.findByEmail("missing@example.com")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
            () -> userService.getUserByEmail("missing@example.com"));
    }

    @Test
    void createUser_whenDetailsAreAvailable_encodesPasswordAndSavesUser() {
        when(userRepository.existsByEmail("owner@example.com")).thenReturn(false);
        when(userRepository.existsByUsername("owner")).thenReturn(false);
        when(passwordEncoder.encode("raw-password")).thenReturn("hashed-password");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User result = userService.createUser("owner", "owner@example.com", "raw-password");

        assertEquals("owner", result.getUsername());
        assertEquals("owner@example.com", result.getEmail());
        assertEquals("hashed-password", result.getPasswordHash());
        verify(passwordEncoder).encode("raw-password");
        verify(userRepository).save(result);
    }

    @Test
    void createUser_whenEmailExists_throwsDuplicateResourceException() {
        when(userRepository.existsByEmail("owner@example.com")).thenReturn(true);

        assertThrows(DuplicateResourceException.class,
            () -> userService.createUser("owner", "owner@example.com", "password"));
        verify(userRepository, never()).existsByUsername(any(String.class));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void createUser_whenUsernameExists_throwsDuplicateResourceException() {
        when(userRepository.existsByEmail("owner@example.com")).thenReturn(false);
        when(userRepository.existsByUsername("owner")).thenReturn(true);

        assertThrows(DuplicateResourceException.class,
            () -> userService.createUser("owner", "owner@example.com", "password"));
        verify(passwordEncoder, never()).encode(any(String.class));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void updateUser_whenUserIsOwner_updatesProfileWithoutChangingPassword() {
        User user = createUser("old-name", "owner@example.com", "existing-hash");
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.findByUsername("new-name")).thenReturn(Optional.empty());
        when(userRepository.findByEmail("new@example.com")).thenReturn(Optional.empty());
        when(userRepository.save(user)).thenReturn(user);

        User result = userService.updateUser(1L, "owner@example.com", "new-name", "new@example.com");

        assertEquals("new-name", result.getUsername());
        assertEquals("new@example.com", result.getEmail());
        assertEquals("existing-hash", result.getPasswordHash());
        verify(passwordEncoder, never()).encode(any(String.class));
        verify(userRepository).save(user);
    }

    @Test
    void updateUser_whenUserIsNotOwner_throwsForbiddenException() {
        User user = createUser("owner", "owner@example.com", "hash");
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        assertThrows(ForbiddenException.class,
            () -> userService.updateUser(1L, "other@example.com", "new-name", "new@example.com"));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void updateUser_whenUsernameBelongsToAnotherUser_throwsDuplicateResourceException() {
        User user = createUser("owner", "owner@example.com", "hash");
        User otherUser = mock(User.class);
        when(otherUser.getId()).thenReturn(2L);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.findByUsername("taken-name")).thenReturn(Optional.of(otherUser));

        assertThrows(DuplicateResourceException.class,
            () -> userService.updateUser(1L, "owner@example.com", "taken-name", "new@example.com"));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void updateUser_whenEmailBelongsToAnotherUser_throwsDuplicateResourceException() {
        User user = createUser("owner", "owner@example.com", "hash");
        User otherUser = mock(User.class);
        when(otherUser.getId()).thenReturn(2L);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.findByUsername("new-name")).thenReturn(Optional.empty());
        when(userRepository.findByEmail("taken@example.com")).thenReturn(Optional.of(otherUser));

        assertThrows(DuplicateResourceException.class,
            () -> userService.updateUser(1L, "owner@example.com", "new-name", "taken@example.com"));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void updatePassword_whenCurrentPasswordMatches_encodesAndSavesNewPassword() {
        User user = createUser("owner", "owner@example.com", "old-hash");
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("current-password", "old-hash")).thenReturn(true);
        when(passwordEncoder.encode("new-password")).thenReturn("new-hash");
        when(userRepository.save(user)).thenReturn(user);

        User result = userService.updatePassword(
            1L, "owner@example.com", "current-password", "new-password");

        assertEquals("new-hash", result.getPasswordHash());
        verify(passwordEncoder).matches("current-password", "old-hash");
        verify(passwordEncoder).encode("new-password");
        verify(userRepository).save(user);
    }

    @Test
    void updatePassword_whenCurrentPasswordDoesNotMatch_throwsInvalidRequestException() {
        User user = createUser("owner", "owner@example.com", "old-hash");
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrong-password", "old-hash")).thenReturn(false);

        assertThrows(InvalidRequestException.class,
            () -> userService.updatePassword(1L, "owner@example.com", "wrong-password", "new-password"));
        assertEquals("old-hash", user.getPasswordHash());
        verify(passwordEncoder, never()).encode(any(String.class));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void updatePassword_whenUserIsNotOwner_throwsForbiddenException() {
        User user = createUser("owner", "owner@example.com", "old-hash");
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        assertThrows(ForbiddenException.class,
            () -> userService.updatePassword(1L, "other@example.com", "current-password", "new-password"));
        verify(passwordEncoder, never()).matches(any(String.class), any(String.class));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void deleteUser_whenUserIsOwner_deletesUser() {
        User user = createUser("owner", "owner@example.com", "hash");
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        userService.deleteUser(1L, "owner@example.com");

        verify(userRepository).delete(user);
    }

    @Test
    void deleteUser_whenUserIsNotOwner_throwsForbiddenException() {
        User user = createUser("owner", "owner@example.com", "hash");
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        assertThrows(ForbiddenException.class,
            () -> userService.deleteUser(1L, "other@example.com"));
        verify(userRepository, never()).delete(any(User.class));
    }

    private User createUser(String username, String email, String passwordHash) {
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPasswordHash(passwordHash);
        return user;
    }
}
