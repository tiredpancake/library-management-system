package com.library.library_management.config;


import com.library.library_management.entity.AppUser;
import com.library.library_management.repository.AppUserRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final AppUserRepository appUserRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {

        if (appUserRepository.findByUsername("admin").isEmpty()) {
            AppUser user = new AppUser();
            user.setUsername("admin");
            user.setPassword(passwordEncoder.encode("test123"));
            appUserRepository.save(user);
        }
    }
}