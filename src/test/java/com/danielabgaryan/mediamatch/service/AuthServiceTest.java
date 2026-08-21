package com.danielabgaryan.mediamatch.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
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

import com.danielabgaryan.mediamatch.exception.InvalidRequestException;
import com.danielabgaryan.mediamatch.model.User;
import com.danielabgaryan.mediamatch.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthService authService;

    @Test
    void authenticate_whenCredentialsAreValid_returnsUser() {
        User user = new User();
        user.setPasswordHash("stored-hash");
        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("password", "stored-hash")).thenReturn(true);

        User result = authService.authenticate("user@example.com", "password");

        assertSame(user, result);
        verify(passwordEncoder).matches("password", "stored-hash");
    }

    @Test
    void authenticate_whenEmailDoesNotExist_throwsGenericInvalidRequestException() {
        when(userRepository.findByEmail("missing@example.com")).thenReturn(Optional.empty());

        InvalidRequestException exception = assertThrows(InvalidRequestException.class,
            () -> authService.authenticate("missing@example.com", "password"));

        assertEquals("Invalid email or password", exception.getMessage());
        verify(passwordEncoder, never()).matches("password", null);
    }

    @Test
    void authenticate_whenPasswordIsWrong_throwsGenericInvalidRequestException() {
        User user = new User();
        user.setPasswordHash("stored-hash");
        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrong-password", "stored-hash")).thenReturn(false);

        InvalidRequestException exception = assertThrows(InvalidRequestException.class,
            () -> authService.authenticate("user@example.com", "wrong-password"));

        assertEquals("Invalid email or password", exception.getMessage());
    }
}
