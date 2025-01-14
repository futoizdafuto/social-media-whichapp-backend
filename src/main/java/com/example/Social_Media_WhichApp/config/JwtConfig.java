package com.example.Social_Media_WhichApp.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JwtConfig {

    @Value("${jwt.secret.key}")
    private String jwtSecretKey;

    public String getJwtSecretKey() {
        return jwtSecretKey;
    }
}

