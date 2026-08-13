package com.danielabgaryan.mediamatch.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.danielabgaryan.mediamatch.dto.LoginRequest;
import com.danielabgaryan.mediamatch.dto.LoginResponse;
import com.danielabgaryan.mediamatch.service.AuthService;
import com.danielabgaryan.mediamatch.service.JwtService;

import jakarta.validation.Valid;
import com.danielabgaryan.mediamatch.model.User;
@RestController
@RequestMapping("/auth")
public class AuthController {
    private final AuthService authService;
    private final JwtService jwtService;

    public AuthController(AuthService authService, JwtService jwtService) {
        this.authService = authService;
        this.jwtService = jwtService;
    }

    @PostMapping("/login")
    public LoginResponse login(@Valid @RequestBody LoginRequest request) {
        User user = authService.authenticate(request.getEmail(),request.getPassword());

        String token = jwtService.generateToken(user);

        return new LoginResponse(token, user.getId(), user.getUsername());
    }
}
