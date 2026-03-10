package com.example.deliveats.domain.user;

import com.example.deliveats.exception.CustomException;
import com.example.deliveats.exception.UserErrorCode;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "users",
        indexes = {
            @Index(name = "idx_users_username", columnList = "username"),
            @Index(name = "idx_users_provider_providerId", columnList = "provider,providerId")
        }
)
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @Column
    private String address;
    @Column
    private String phone;
    
    // 나중에 관리자 승인 확장에 사용할 예정
//    @Enumerated(EnumType.STRING)
//    @Column(nullable = false)
//    private UserStatus status = UserStatus.ACTIVE;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AuthProvider provider = AuthProvider.LOCAL;

    private String providerId;

    private String name;

    protected User(
            String username,
            String password,
            Role role,
            AuthProvider provider,
            String providerId,
            String name,
            String address,
            String phone
    ) {
        this.username = username;
        this.password = password;
        this.role = role;
        this.provider = provider;
        this.providerId = providerId;
        this.name = name;
        this.address = address;
        this.phone = phone;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String username;
        private String password;
        private Role role;
        private AuthProvider provider;
        private String providerId;
        private String name;
        private String address;
        private String phone;

        private Builder() {}

        public Builder username(String username) {
            this.username = username;
            return this;
        }

        public Builder password(String password) {
            this.password = password;
            return this;
        }

        public Builder role(Role role) {
            this.role = role;
            return this;
        }

        public Builder provider(AuthProvider provider) {
            this.provider = provider;
            return this;
        }

        public Builder providerId(String providerId) {
            this.providerId = providerId;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder address(String address) {
            this.address = address;
            return this;
        }

        public Builder phone(String phone) {
            this.phone = phone;
            return this;
        }

        public User build() {
            if (username == null || username.isBlank()) {
                throw new CustomException(UserErrorCode.INVALID_USER, "username is required");
            }
            if (password == null || password.isBlank()) {
                throw new CustomException(UserErrorCode.INVALID_USER,"password is required");
            }
            if (role == null) {
                throw new CustomException(UserErrorCode.INVALID_USER,"role is required");
            }
            if (provider == null) {
                throw new CustomException(UserErrorCode.INVALID_USER,"provider is required");
            }

            return new User(
                    username,
                    password,
                    role,
                    provider,
                    providerId,
                    name,
                    address,
                    phone
            );
        }
    }

    public void updateAddress(String address) {
        this.address = address;
    }

    public void updatePhone(String phone) {
        this.phone = phone;
    }

    public void updatePassword(String encodedPassword) {
        if (encodedPassword == null || encodedPassword.isBlank()) {
            throw new CustomException(UserErrorCode.INVALID_USER, "encodedPassword cannot be blank");
        }
        this.password = encodedPassword;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(() -> "ROLE_" + role.name());
    }

    @Override
    public boolean isAccountNonExpired() { return true; }

    @Override
    public boolean isAccountNonLocked() { return true; }

    @Override
    public boolean isCredentialsNonExpired() { return true; }

    @Override
    public boolean isEnabled() { return true; }
}
