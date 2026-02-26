package com.example.deliveats.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum StoreErrorCode implements ErrorCode {

    INVALID_STORE("STORE_000", "유효하지 않은 가게 요청입니다.", HttpStatus.BAD_REQUEST),
    STORE_NOT_FOUND("STORE_001", "가게를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    STORE_PERMISSION_DENIED("STORE_003", "해당 가게의 권한이 없습니다.", HttpStatus.FORBIDDEN);

    private final String code;
    private final String message;
    private final HttpStatus status;

    StoreErrorCode(String code, String message, HttpStatus status) {
        this.code = code;
        this.message = message;
        this.status = status;
    }
}
