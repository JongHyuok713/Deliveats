package com.example.deliveats.config;

import com.example.deliveats.domain.user.AuthProvider;
import com.example.deliveats.domain.user.Role;
import com.example.deliveats.domain.user.User;
import com.example.deliveats.repository.user.UserRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@Profile("dev")
@RequiredArgsConstructor
public class DevAdminInitializer {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @PostConstruct
    public void initAdmin() {

        String adminUsername = "admin@deliveats.com";

        if (userRepository.existsByUsername(adminUsername)) {
            return;
        }

        User admin = User.builder()
                .username(adminUsername)
                .password(passwordEncoder.encode("admin1234"))
                .role(Role.ADMIN)
                .provider(AuthProvider.LOCAL)
                .providerId(adminUsername)
                .name("관리자")
                .build();
        userRepository.save(admin);
        System.out.println("테스트용 관리자 계정 생성 완료");
    }
}
