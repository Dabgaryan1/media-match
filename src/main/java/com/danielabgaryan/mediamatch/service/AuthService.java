package com.danielabgaryan.mediamatch.service;

import com.danielabgaryan.mediamatch.model.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.danielabgaryan.mediamatch.exception.InvalidRequestException;
import com.danielabgaryan.mediamatch.exception.ResourceNotFoundException;
import com.danielabgaryan.mediamatch.repository.UserRepository;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private JwtService jwtService;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public String authenticate(String email, String password) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (!passwordEncoder.matches(password, user.getPasswordHash())) {
            throw new InvalidRequestException("Invalid credentials");
        }
        return jwtService.generateToken(user);
    }
}
