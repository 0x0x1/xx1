package com.spring.security.web.payload;

public record UserDTO(
        String username,
        String password,
        String email
) {
}
