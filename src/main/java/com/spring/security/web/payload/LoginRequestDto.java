package com.spring.security.web.payload;

import org.springframework.util.Assert;

import io.swagger.v3.oas.annotations.media.Schema;

public class LoginRequestDto {

    @Schema(description = "The username of the application user.")
    private final String username;
    @Schema(description = "The password of the application user.")
    private final String password;

    public LoginRequestDto(String username, String password) {
        Assert.isTrue(username != null && !username.isEmpty(), "Cannot pass null or empty values to constructor");
        Assert.isTrue(password != null && !password.isEmpty(), "Cannot pass null or empty values to constructor");
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
