package com.ms_auth.presentation.controller;

import com.ms_auth.application.dtos.AuthResponseDTO;
import com.ms_auth.application.dtos.LoginRequestDTO;
import com.ms_auth.application.dtos.UserCreateRequestDTO;
import com.ms_auth.application.usecase.AuthUseCase;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthUseCase authUseCase;

    public AuthController(AuthUseCase authUseCase) {
        this.authUseCase = authUseCase;
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(@Valid @RequestBody LoginRequestDTO request) {
        AuthResponseDTO response = authUseCase.login(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponseDTO> register(@Valid @RequestBody UserCreateRequestDTO request) {
        AuthResponseDTO response = authUseCase.register(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/validate")
    public ResponseEntity<Map<String, Object>> validate(Authentication authentication) {
        return ResponseEntity.ok(Map.of(
                "message", "Token is valid",
                "username", authentication.getName(),
                "authorities", authentication.getAuthorities()
        ));
    }

    @GetMapping("/me")
    public ResponseEntity<Map<String, Object>> getCurrentUser(Authentication authentication) {
        return ResponseEntity.ok(Map.of(
                "username", authentication.getName(),
                "authorities", authentication.getAuthorities()
        ));
    }
}