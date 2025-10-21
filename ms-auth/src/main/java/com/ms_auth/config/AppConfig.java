package com.ms_auth.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

    @Bean
    public String jwtSecret() {
        return "mySecretKeyForJWTGenerationThatIsAtLeast32BytesLong";
    }
}