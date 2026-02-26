package com.example.deliveats.security;

import com.example.deliveats.domain.user.Role;
import com.example.deliveats.domain.user.User;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Getter // JWT 인증 및 로그인 사용자 세션 유지 시 사용
public class CustomUserDetails implements UserDetails {

    private final Long id;
    private final String username;
    private final String password;
    private final Role role;
    private final String address;
    private final String phone;

    private final User user;

    public CustomUserDetails(User user) {
        this.user = user;
        this.id = user.getId();
        this.username = user.getUsername();
        this.password = user.getPassword();
        this.role = user.getRole();
        this.address = user.getAddress();
        this.phone = user.getPhone();
    }

    @Override // 권한 반환
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()));
    }

    @Override // 계정 만료 여부 (true = 만료 안됨)
    public boolean isAccountNonExpired() { return true; }

    @Override // 계정 잠금 여부 (true = 잠기지 않음)
    public boolean isAccountNonLocked() { return true; }

    @Override // 비밀번호 만료 여부
    public boolean isCredentialsNonExpired() { return true; }

    @Override // 계정 활성화 여부
    public boolean isEnabled() { return true; }

    // 유틸
    public boolean hasRole(Role role) {
        return this.role == role;
    }

    // 추가 메서드
    public Long getId() { return user.getId(); }
    public String getAddress() { return user.getAddress(); }
    public String getPhone() { return user.getPhone(); }
    public User getUser() { return user; }
}
