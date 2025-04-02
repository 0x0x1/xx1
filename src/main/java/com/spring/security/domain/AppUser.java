package com.spring.security.domain;

import java.util.List;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;

import com.spring.security.web.annotation.ValidEmail;

@Entity
public class AppUser {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;
    private String username;
    private String password;
    private String email;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "app_user_id")
    private List<Authority> authorities;

    public AppUser(Builder builder) {
        this.id = builder.id;
        this.username = builder.username;
        this.password = builder.password;
        this.email = builder.email;
        this.authorities = builder.authorities;
    }

    public AppUser() {}

    public static class Builder {
        private Long id;
        private String username;
        private String password;
        private String email;
        private List<Authority> authorities;

        public Builder setId(Long id) {
            this.id = id;
            return this;
        }

        public Builder setUsername(String username) {
            this.username = username;
            return this;
        }

        public Builder setPassword(String password) {
            this.password = password;
            return this;
        }

        public Builder setEmail(String email) {
            this.email = email;
            return this;
        }
        public Builder setAuthorities(List<Authority> authorities) {
            this.authorities = authorities;
            return this;
        }

        public AppUser build() {
            if (username == null || password == null || email == null) {
                throw new IllegalStateException("Missing required fields to create User");
            }
            return new AppUser(this);
        }
    }

    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getEmail() {
        return email;
    }

    public List<Authority> getAuthorities() {
        return authorities;
    }
}