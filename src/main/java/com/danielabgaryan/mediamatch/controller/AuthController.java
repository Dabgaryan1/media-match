package com.danielabgaryan.mediamatch.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.danielabgaryan.mediamatch.dto.LoginRequest;
import com.danielabgaryan.mediamatch.service.AuthService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public String login(@Valid @RequestBody LoginRequest request) {
        return authService.authenticate(request.getEmail(), request.getPassword());
    }
}
