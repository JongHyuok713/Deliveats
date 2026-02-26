package com.example.deliveats.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum OAuthErrorCode implements ErrorCode {

    OAUTH_TOKEN_ERROR("OAUTH_001", "OAuth 토큰 발급 실패", HttpStatus.UNAUTHORIZED),
    OAUTH_USERINFO_ERROR("OAUTH_002", "OAuth 사용자 정보 조회 실패", HttpStatus.UNAUTHORIZED);

    private final String code;
    private final String message;
    private final HttpStatus status;

    OAuthErrorCode(String code, String message, HttpStatus status) {
        this.code = code;
        this.message = message;
        this.status = status;
    }
}
