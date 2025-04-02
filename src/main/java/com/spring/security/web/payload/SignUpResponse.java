package com.spring.security.web.payload;

public record SignUpResponse(
        String username,
        String password,
        String email
) {
}
