package com.ms_auth.application.usecase;

import com.ms_auth.application.dtos.AuthResponseDTO;
import com.ms_auth.application.dtos.LoginRequestDTO;
import com.ms_auth.application.dtos.UserCreateRequestDTO;
import com.ms_auth.domain.entity.Role;
import com.ms_auth.domain.entity.User;
import com.ms_auth.domain.service.TokenService;
import com.ms_auth.domain.service.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

public class AuthUseCase {
    private final UserService userService;
    private final TokenService tokenService;
    private final PasswordEncoder passwordEncoder;

    public AuthUseCase(UserService userService, TokenService tokenService,
                       PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.tokenService = tokenService;
        this.passwordEncoder = passwordEncoder;
    }

    public AuthResponseDTO login(LoginRequestDTO request) {
        User user = userService.validateUser(request.username(), request.password());
        String token = tokenService.generateToken(user.getUsername());

        return new AuthResponseDTO(
                token,
                "Bearer",
                3600L,
                user.getUsername()
        );
    }

    public AuthResponseDTO register(UserCreateRequestDTO request) {
        User user = new User(
                request.username(),
                request.email(),
                passwordEncoder.encode(request.password()),
                List.of(Role.ROLE_USER)
        );

        User savedUser = userService.createUser(user);
        String token = tokenService.generateToken(savedUser.getUsername());

        return new AuthResponseDTO(
                token,
                "Bearer",
                3600L,
                savedUser.getUsername()
        );
    }
}