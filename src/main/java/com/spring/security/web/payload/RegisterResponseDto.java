package com.spring.security.web.payload;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.spring.security.domain.Authority;

import io.swagger.v3.oas.annotations.media.Schema;

public class RegisterResponseDto {

    @Schema(description = "The username of the application user.")
    private String username;
    @Schema(description = "The password of the application user.")
    @JsonIgnore
    private String password;
    @Schema(description = "The email of the application user.")
    private String email;
    @Schema(description = "The roles of the application user.")
    private final List<String> authorities = new ArrayList<>();

    public RegisterResponseDto() {
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<String> getAuthorities() {
        return authorities;
    }

    public void setAuthorities(List<Authority> authorities) {
        for (Authority authority : authorities) {
            this.authorities.add(authority.getAuthorityName());
        }
    }
}