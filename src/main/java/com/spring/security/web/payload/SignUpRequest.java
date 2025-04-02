package com.spring.security.web.payload;

import com.spring.security.web.annotation.ValidEmail;

public record SignUpRequest(
        String username,
        String password,
        @ValidEmail
        String email
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
}
