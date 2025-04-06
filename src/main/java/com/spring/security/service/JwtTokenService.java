package com.spring.security.service;

import org.springframework.security.core.Authentication;

public interface JwtTokenService {
    String generateToken(Authentication authentication);
    boolean isTokenValid(String token);
    String extractUsername(String token);
    String trimToken(String token, String prefix);
}
