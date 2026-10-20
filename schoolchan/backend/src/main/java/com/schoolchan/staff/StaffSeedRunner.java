package com.schoolchan.staff;

import com.schoolchan.config.SchoolChanProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class StaffSeedRunner implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(StaffSeedRunner.class);

    private final StaffUserRepository repository;
    private final SchoolChanProperties properties;
    private final PasswordEncoder passwordEncoder;

    public StaffSeedRunner(
            StaffUserRepository repository, SchoolChanProperties properties, PasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.properties = properties;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(ApplicationArguments args) {
        String username = properties.getAdminUsername();
        if (repository.findByUsername(username).isPresent()) {
            return;
        }
        StaffUser admin = new StaffUser();
        admin.setId(UUID.randomUUID());
        admin.setUsername(username);
        admin.setPasswordHash(passwordEncoder.encode(properties.getAdminPassword()));
        admin.setRole("ADMIN");
        repository.save(admin);
        log.info("Seeded staff admin user {}", username);
    }
}
