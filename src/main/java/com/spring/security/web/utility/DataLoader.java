package com.spring.security.web.utility;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.spring.security.domain.Authority;
import com.spring.security.repository.AuthorityRepository;

@Component
public class DataLoader implements CommandLineRunner {

    private final AuthorityRepository authorityRepository;

    public DataLoader(AuthorityRepository authorityRepository) {
        this.authorityRepository = authorityRepository;
    }

    @Override
    public void run(String... args) {
        if (authorityRepository.count() == 0) {
            var admin = new Authority();
            admin.setAuthorityName("ADMIN");

            var user = new Authority();
            user.setAuthorityName("USER");

            authorityRepository.save(admin);
            authorityRepository.save(user);
        }
    }
}
