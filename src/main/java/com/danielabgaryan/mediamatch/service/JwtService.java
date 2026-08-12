package com.danielabgaryan.mediamatch.service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import com.danielabgaryan.mediamatch.model.User;

@Service
public class JwtService {
    private final JwtEncoder jwtEncoder;

    public JwtService(JwtEncoder jwtEncoder) {
        this.jwtEncoder = jwtEncoder;
    }

    // Creates a JWT containing the user's identity and expiration time
    public String generateToken(User user) {
        Instant now = Instant.now();

        JwtClaimsSet claims = JwtClaimsSet.builder()
                                .subject(user.getEmail())
                                .issuedAt(now)
                                .expiresAt(now.plus(1, ChronoUnit.HOURS))
                                .claim("userId", user.getId())
                                .claim("username", user.getUsername())
                                .build();
        
        return jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
    }
}
