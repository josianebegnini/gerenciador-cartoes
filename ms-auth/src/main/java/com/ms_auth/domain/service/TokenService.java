package com.ms_auth.domain.service;

public interface TokenService {
    String generateToken(String username);
    boolean validateToken(String token);
    String extractUsername(String token);
}