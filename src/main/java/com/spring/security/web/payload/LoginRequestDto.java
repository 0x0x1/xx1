package com.spring.security.web.payload;

import io.swagger.v3.oas.annotations.media.Schema;

public class LoginRequestDto {

    @Schema(description = "The username of the application user.")
    private final String username;
    @Schema(description = "The password of the application user.")
    private final String password;

    public LoginRequestDto(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
}
