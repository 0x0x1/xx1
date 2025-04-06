package com.spring.security.web.payload;

import com.spring.security.web.annotation.ValidEmail;

public record RegisterRequestDto(
        String username,
        String password,
        @ValidEmail
        String email,
        String authorityName
) {
    @Override
    public String username() {
        return username;
    }

    @Override
    public String password() {
        return password;
    }

    @Override
    public String email() {
        return email;
    }

    @Override
    public String authorityName() {
        return authorityName;
    }
}
