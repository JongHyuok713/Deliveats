package com.example.deliveats.repository.user;

import com.example.deliveats.domain.user.AuthProvider;
import com.example.deliveats.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    boolean existsByUsername(String username);

    Optional<User> findByProviderAndProviderId(AuthProvider provider, String providerId);
}
