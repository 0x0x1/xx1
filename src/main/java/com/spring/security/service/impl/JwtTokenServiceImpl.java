package com.spring.security.service.impl;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.stream.Collectors;

import org.springframework.security.core.Authentication;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.stereotype.Service;

import com.spring.security.service.JwtTokenService;

@Service
public class JwtTokenServiceImpl implements JwtTokenService {

    private final JwtEncoder encoder;
    private final JwtDecoder decoder;
    private final UserDetailsService userDetailsService;

    public JwtTokenServiceImpl(JwtEncoder encoder, JwtDecoder decoder, UserDetailsService userDetailsService) {
        this.encoder = encoder;
        this.decoder = decoder;
        this.userDetailsService = userDetailsService;
    }

    @Override
    public String generateToken(Authentication authentication) {
        Instant now = Instant.now();
        String scope = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(" "));
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("self")
                .issuedAt(now)
                .expiresAt(now.plus(1, ChronoUnit.HOURS))
                .subject(authentication.getName())
                .claim("scope", scope)
                .build();
        return this.encoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
    }

    @Override
    public boolean isTokenValid(String token) {
        Jwt jwt = decoder.decode(token);
        String username = jwt.getSubject();
        Instant expiresAt = jwt.getExpiresAt();

        if (username == null || expiresAt == null) {
            return false;
        }
        String registeredUser = userDetailsService.loadUserByUsername(username).getUsername();
        return username.equals(registeredUser) && expiresAt.isAfter(Instant.now());
    }

    @Override
    public String extractUsername(String token) {
        return decoder.decode(token).getSubject();
    }

    @Override
    public String trimToken(String token, String prefix) {
        if (token == null || !token.startsWith(prefix)) {
            throw new JwtException("An error occurred while attempting to extract the Jwt: Malformed token");
        }
        return token.substring(7);
    }
}