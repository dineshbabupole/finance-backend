package com.dinesh.finance.config;

import com.dinesh.finance.model.*;
import com.dinesh.finance.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {

        // create default admin if not exists
        if (userRepository.findByEmail("admin@zorvyn.com").isEmpty()) {
            userRepository.save(User.builder()
                    .name("Admin User")
                    .email("admin@zorvyn.com")
                    .password(passwordEncoder.encode("admin123"))
                    .role(Role.ADMIN)
                    .active(true)
                    .build());
            System.out.println("✅ Admin created");
        }

        // create default analyst if not exists
        if (userRepository.findByEmail("analyst@zorvyn.com").isEmpty()) {
            userRepository.save(User.builder()
                    .name("Analyst User")
                    .email("analyst@zorvyn.com")
                    .password(passwordEncoder.encode("analyst123"))
                    .role(Role.ANALYST)
                    .active(true)
                    .build());
            System.out.println("✅ Analyst created");
        }

        // create default viewer if not exists
        if (userRepository.findByEmail("viewer@zorvyn.com").isEmpty()) {
            userRepository.save(User.builder()
                    .name("Viewer User")
                    .email("viewer@zorvyn.com")
                    .password(passwordEncoder.encode("viewer123"))
                    .role(Role.VIEWER)
                    .active(true)
                    .build());
            System.out.println("✅ Viewer created");
        }
    }
}