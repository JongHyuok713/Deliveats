package com.example.deliveats.security;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

@Component
public class CookieTokenService {

    public static final String REFRESH_COOKIE_NAME = "refreshToken";

    @Value("${jwt.refresh-token-validity-seconds}")
    private long refreshTtlSeconds;

    // 개발(Local http)환경 에서는 secure=false
    // 운영(https)환경 에서는 secure=true
    public void setRefreshTokenCookie(HttpServletResponse response, String refreshToken, boolean secure) {
        ResponseCookie cookie = ResponseCookie.from(REFRESH_COOKIE_NAME, refreshToken)
                .httpOnly(true)
                .secure(secure)
                .path("/")
                .sameSite("Lax")
                .maxAge(refreshTtlSeconds)
                .build();
        response.addHeader("Set-Cookie", cookie.toString());
    }

    public void clearRefreshTokenCookie(HttpServletResponse response, boolean secure) {
        ResponseCookie cookie = ResponseCookie.from(REFRESH_COOKIE_NAME, "")
                .httpOnly(true)
                .secure(secure)
                .path("/")
                .sameSite("Lax")
                .maxAge(0)
                .build();
        response.addHeader("Set-Cookie", cookie.toString());
    }
}
