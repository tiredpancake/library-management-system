package com.library.library_management.config;

import com.library.library_management.entity.AppUser;
import com.library.library_management.repository.AppUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final AppUserRepository appUserRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${security.basic.username}")
    private String configuredUsername;

    @Value("${security.basic.password}")
    private String configuredPassword;

    @Override
    public void run(String... args) {
        AppUser user = appUserRepository.findByUsername(configuredUsername).orElseGet(AppUser::new);

        user.setUsername(configuredUsername);

        if (user.getPassword() == null || !passwordEncoder.matches(configuredPassword, user.getPassword())) {
            user.setPassword(passwordEncoder.encode(configuredPassword));
        }

        appUserRepository.save(user);
    }
}
