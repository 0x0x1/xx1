package com.spring.security.web.utility.mapper;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.spring.security.domain.User;
import com.spring.security.domain.Authority;
import com.spring.security.web.payload.RegisterResponseDto;

/**
 * Mapper Implementation
 */
@Component
public class MapperImpl implements Mapper<User, RegisterResponseDto> {

    @Override
    public RegisterResponseDto toDto(User domain) {
        if (domain == null) {
            throw new RuntimeException("Domain is null");
        }

        var signUpResponse = new RegisterResponseDto();
        signUpResponse.setUsername(domain.getUsername());
        signUpResponse.setPassword(domain.getPassword());
        signUpResponse.setEmail(domain.getEmail());
        signUpResponse.setAuthorities(domain.getAuthorities());

        return signUpResponse;
    }

    @Override
    public User toDomain(RegisterResponseDto dto) {
        if (dto == null) {
            throw new RuntimeException("SignUpResponse is null");
        }

        List<Authority> authorities = dto.getAuthorities().stream()
                .map(Authority::new)
                .collect(Collectors.toList());

        return new User.Builder()
                .setUsername(dto.getUsername())
                .setEmail(dto.getEmail())
                .setPassword(dto.getPassword())
                .setAuthorities(authorities)
                .build();
    }
}
