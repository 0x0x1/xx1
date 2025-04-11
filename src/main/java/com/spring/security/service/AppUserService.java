package com.spring.security.service;

import com.spring.security.domain.User;

public interface AppUserService {

    User save(User user);
    boolean userExists(String username);
    boolean existsByEmail(String email);
}
