package dev.dwidi.ecommercerabbitmqkafka.security;

import com.auth0.jwt.algorithms.Algorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
public class KeyAlgorithmConfig {

    @Value("${jwt.token.secret}")
    private String secret;

    @Bean
    @Profile({"dev"})
    public Algorithm getKeyAlgorithmLocal() {
        return Algorithm.HMAC256(secret);
    }

    @Bean
    @Profile({"staging", "production"})
    public Algorithm getKeyAlgorithm() {
        return Algorithm.HMAC512(secret);
    }
}

