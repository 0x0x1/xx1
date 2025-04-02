package com.spring.security.business.service;

import com.spring.security.domain.AppUser;

public interface AppUserService {

    AppUser save(AppUser appUser);
    boolean userExists(String username);
    boolean existsByEmail(String email);
}
