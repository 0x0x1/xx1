package com.spring.security.business.service;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.spring.security.domain.AppUser;
import com.spring.security.persistence.Persistence;

@Service
public class AppUserServiceImpl implements AppUserService {

    private final Persistence persistence;

    public AppUserServiceImpl(Persistence persistence) {
        this.persistence = persistence;
    }


    @Override
    public AppUser save(AppUser appUser) {
        return persistence.save(appUser);
    }

    @Override
    public boolean userExists(String username) {
        return false;
    }

    @Override
    public int existsByEmail(String email) {
        return persistence.findByEmail(email).isPresent() ? 409 : 202;
    }


}
