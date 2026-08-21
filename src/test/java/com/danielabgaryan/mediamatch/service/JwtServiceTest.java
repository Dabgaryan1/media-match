package com.danielabgaryan.mediamatch.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Duration;
import java.time.Instant;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;

import com.danielabgaryan.mediamatch.model.User;

@ExtendWith(MockitoExtension.class)
public class JwtServiceTest {
    @Mock
    private JwtEncoder jwtEncoder;

    @InjectMocks
    private JwtService jwtService;

    @Test
    void generateToken_buildsExpectedClaimsAndReturnsEncodedToken() {
        User user = mock(User.class);
        Jwt jwt = mock(Jwt.class);
        when(user.getId()).thenReturn(1L);
        when(user.getEmail()).thenReturn("owner@example.com");
        when(user.getUsername()).thenReturn("owner");
        when(jwt.getTokenValue()).thenReturn("encoded-token");
        when(jwtEncoder.encode(org.mockito.ArgumentMatchers.any(JwtEncoderParameters.class)))
            .thenReturn(jwt);

        String result = jwtService.generateToken(user);

        assertEquals("encoded-token", result);

        ArgumentCaptor<JwtEncoderParameters> captor =
            ArgumentCaptor.forClass(JwtEncoderParameters.class);
        verify(jwtEncoder).encode(captor.capture());
        JwtClaimsSet claims = captor.getValue().getClaims();

        assertEquals("owner@example.com", claims.getSubject());
        assertEquals(Long.valueOf(1L), claims.getClaim("userId"));
        assertEquals("owner", claims.getClaim("username"));
        assertNotNull(claims.getIssuedAt());
        assertNotNull(claims.getExpiresAt());
        assertEquals(Duration.ofHours(1), Duration.between(claims.getIssuedAt(), claims.getExpiresAt()));
        assertTrue(!claims.getIssuedAt().isAfter(Instant.now()));
    }
}
