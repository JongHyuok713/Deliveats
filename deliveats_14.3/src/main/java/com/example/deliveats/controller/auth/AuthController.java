package com.example.deliveats.controller.auth;

import com.example.deliveats.domain.user.User;
import com.example.deliveats.dto.auth.LoginRequest;
import com.example.deliveats.dto.auth.RegisterRequest;
import com.example.deliveats.dto.auth.TokenResponse;
import com.example.deliveats.security.CookieTokenService;
import com.example.deliveats.service.auth.AuthService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final CookieTokenService cookieTokenService;

    @PostMapping("/register/user")
    public ResponseEntity<?> registerUser(@RequestBody RegisterRequest req) {
        authService.registerUser(req);
        return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "회원가입이 완료되었습니다."
        ));
    }

    @PostMapping("/register/owner")
    public ResponseEntity<?> registerOwner(@RequestBody RegisterRequest req) {
        authService.registerOwner(req);
        return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "점주 회원가입이 완료되었습니다."
        ));
    }

    @PostMapping("/register/rider")
    public ResponseEntity<?> registerRider(@RequestBody RegisterRequest req) {
        authService.registerRider(req);
        return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "라이더 회원가입이 완료되었습니다."
        ));
    }

    // 로그인
    @PostMapping("/login")
    public TokenResponse login(@RequestBody LoginRequest req,
                               HttpServletRequest httpReq,
                               HttpServletResponse httpRes) {
        User user = authService.authenticate(req);

        String accessToken = authService.issueAccessToken(user);
        String refreshToken = authService.issueRefreshToken(user);

        boolean secure = httpReq.isSecure();
        cookieTokenService.setRefreshTokenCookie(httpRes, refreshToken, secure);

        return new TokenResponse(accessToken);
    }

    // 토큰 재발급
    @PostMapping("/refresh")
    public TokenResponse refresh(HttpServletRequest httpReq,
                                 HttpServletResponse httpRes) {
        String refreshToken = readCookie(httpReq, CookieTokenService.REFRESH_COOKIE_NAME);
        User user = authService.authenticateByRefreshToken(refreshToken);

        String newAccessToken = authService.issueAccessToken(user);
        String newRefreshToken = authService.issueRefreshToken(user);

        boolean secure = httpReq.isSecure();
        cookieTokenService.setRefreshTokenCookie(httpRes, newRefreshToken, secure);

        return new TokenResponse(newAccessToken);
    }

    // 로그아웃
    @PostMapping("/logout")
    public void logout(HttpServletRequest httpReq, HttpServletResponse httpRes) {
        boolean secure = httpReq.isSecure();
        cookieTokenService.clearRefreshTokenCookie(httpRes, secure);
    }

    private String readCookie(HttpServletRequest req, String name) {
        Cookie[] cookies = req.getCookies();
        if (cookies == null) return null;
        for (Cookie c : cookies) {
            if (name.equals(c.getName())) return c.getValue();
        }
        return null;
    }
}
