package com.danielabgaryan.mediamatch.config;

import javax.crypto.spec.SecretKeySpec;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.beans.factory.annotation.Value;
import javax.crypto.SecretKey;

@Configuration
public class JwtConfig {
    @Value("${JWT_SECRET}")
    private String secret;

    private SecretKey getSecretKey() {
        return new SecretKeySpec(secret.getBytes(), "HmacSHA256");
    }

    //creates the encoder used to sign JWT tokens with our secret key
    @Bean
    public JwtEncoder jwtEncoder() {
        return NimbusJwtEncoder.withSecretKey(getSecretKey()).build();
    }

    //creates the decoder used to verify JWT tokens
    @Bean
    public JwtDecoder jwtDecoder() {
        return NimbusJwtDecoder.withSecretKey(getSecretKey()).build();
    }

}
