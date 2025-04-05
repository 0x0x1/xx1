package com.spring.security.web.payload;

import io.swagger.v3.oas.annotations.media.Schema;

public class LoginResponseDto {

    @Schema(description = "The username of the application user.")
    private String username;
    @Schema(description = "The email of the application user.")
    private String email;
    @Schema(description = "JWT token")
    private String token;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}