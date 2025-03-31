package com.spring.security.persistence;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.spring.security.domain.AppUser;

@Repository
public interface Persistence extends JpaRepository<AppUser, Long> {
    AppUser findByUsername(String username);
    Optional<AppUser> findByEmail(String email);
}
