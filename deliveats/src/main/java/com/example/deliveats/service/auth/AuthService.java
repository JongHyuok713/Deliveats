package com.example.deliveats.service.auth;

import com.example.deliveats.domain.user.AuthProvider;
import com.example.deliveats.domain.user.Role;
import com.example.deliveats.domain.user.User;
import com.example.deliveats.dto.auth.LoginRequest;
import com.example.deliveats.dto.auth.RegisterRequest;
import com.example.deliveats.exception.CommonErrorCode;
import com.example.deliveats.exception.CustomException;
import com.example.deliveats.exception.OAuthErrorCode;
import com.example.deliveats.exception.UserErrorCode;
import com.example.deliveats.exception.user.UserNotFoundException;
import com.example.deliveats.repository.user.UserRepository;
import com.example.deliveats.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    // 고객 회원가입
    public void registerUser(RegisterRequest req) {
        register(req, Role.USER);
    }

    // 점주 회원가입
    public void registerOwner(RegisterRequest req) {
        register(req, Role.OWNER);
    }

    // 라이더 회원가입
    public void registerRider(RegisterRequest req) {
        register(req, Role.RIDER);
    }

    // 회원가입
    public void register(RegisterRequest req, Role role) {
        if (userRepository.existsByUsername(req.username())) {
            throw new CustomException(UserErrorCode.USER_ALREADY_EXISTS);
        }

        User user = User.builder()
                .username(req.username())
                .password(passwordEncoder.encode(req.password()))
                .role(role)
                .provider(AuthProvider.LOCAL)
                .providerId(req.username())
                .address(req.address())
                .phone(req.phone())
                .build();
        userRepository.save(user);
    }

    // 로그인
    public User authenticate(LoginRequest req) {
        User user = userRepository.findByUsername(req.username())
                .orElseThrow(UserNotFoundException::new);

        if (!passwordEncoder.matches(req.password(), user.getPassword())) {
            throw new CustomException(CommonErrorCode.UNAUTHORIZED, "invalid credentials");
        }
        return user;
    }

    public String issueAccessToken(User user) {
        return jwtTokenProvider.createAccessToken(user.getUsername(), user.getRole().name());
    }

    public String issueRefreshToken(User user) {
        return jwtTokenProvider.createRefreshToken(user.getUsername());
    }

    // 토큰 재발급
    public User authenticateByRefreshToken(String refreshToken) {
        if (refreshToken == null || refreshToken.isBlank()) {
            throw new CustomException(OAuthErrorCode.OAUTH_TOKEN_ERROR, "refresh token is blank");
        }
        if (!jwtTokenProvider.validate(refreshToken)) {
            throw new CustomException(OAuthErrorCode.OAUTH_TOKEN_ERROR, "refresh token is invalid");
        }

        String username = jwtTokenProvider.getUsername(refreshToken);
        return userRepository.findByUsername(username)
                .orElseThrow(UserNotFoundException::new);
    }

}
