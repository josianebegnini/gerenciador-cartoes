package com.ms_auth.config;

import com.ms_auth.application.usecase.AuthUseCase;
import com.ms_auth.domain.service.TokenService;
import com.ms_auth.domain.service.UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class AuthUseCaseConfig {

    @Bean
    public AuthUseCase authUseCase(UserService userService,
                                   TokenService tokenService,
                                   PasswordEncoder passwordEncoder) {
        return new AuthUseCase(userService, tokenService, passwordEncoder);
    }
}