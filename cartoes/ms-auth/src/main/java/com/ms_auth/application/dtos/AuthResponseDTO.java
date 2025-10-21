package com.ms_auth.application.dtos;

public record AuthResponseDTO(
        String token,
        String type,
        Long expiresIn,
        String username
) {}