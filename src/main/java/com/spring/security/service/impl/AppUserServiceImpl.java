package com.spring.security.service.impl;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.spring.security.domain.AppUser;
import com.spring.security.repository.UserRepository;
import com.spring.security.service.AppUserService;

@Service
public class AppUserServiceImpl implements AppUserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AppUserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public AppUser save(AppUser appUser) {
        String hashedPassword = passwordEncoder.encode(appUser.getPassword());
        appUser.setPassword(hashedPassword);
        return userRepository.save(appUser);
    }

    @Override
    public boolean userExists(String username) {
        return false;
    }

    @Override
    public boolean existsByEmail(String email) {
        return userRepository.findByEmail(email).isPresent();
    }

    public String trimToken(String token) {
        return token.substring(7);
    }
}