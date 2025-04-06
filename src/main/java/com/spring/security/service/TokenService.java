package com.spring.security.service;

import org.springframework.security.core.Authentication;

public interface TokenService {
    String generateToken(Authentication authentication);
    boolean isTokenValid(String token);
    String extractUsername(String token);
}
