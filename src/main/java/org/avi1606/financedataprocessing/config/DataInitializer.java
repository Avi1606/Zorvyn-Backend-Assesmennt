package org.avi1606.financedataprocessing.config;

import lombok.extern.slf4j.Slf4j;
import org.avi1606.financedataprocessing.enums.Role;
import org.avi1606.financedataprocessing.enums.UserStatus;
import org.avi1606.financedataprocessing.model.User;
import org.avi1606.financedataprocessing.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (!userRepository.existsByEmail("admin@finance.com")) {
            User admin = User.builder()
                    .name("System Admin")
                    .email("admin@finance.com")
                    .password(passwordEncoder.encode("admin123"))
                    .role(Role.ADMIN)
                    .status(UserStatus.ACTIVE)
                    .build();
            userRepository.save(admin);
            log.info("Default admin created: admin@finance.com / admin123");
        }
    }
}

