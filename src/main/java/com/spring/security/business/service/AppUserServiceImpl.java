package com.spring.security.business.service;

import org.springframework.stereotype.Service;

import com.spring.security.domain.AppUser;
import com.spring.security.repository.UserRepository;

@Service
public class AppUserServiceImpl implements AppUserService {

    private final UserRepository userRepository;

    public AppUserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }


    @Override
    public AppUser save(AppUser appUser) {
        return userRepository.save(appUser);
    }

    @Override
    public boolean userExists(String username) {
        return false;
    }

    @Override
    public int existsByEmail(String email) {
        return userRepository.findByEmail(email).isPresent() ? 409 : 202;
    }


}
