package com.example.deliveats.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum MenuErrorCode implements ErrorCode {

    INVALID_MENU("MENU_000", "유효하지 않은 메뉴 요청입니다.", HttpStatus.BAD_REQUEST),
    MENU_NOT_FOUND("MENU_001", "메뉴를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    MENU_UNAVAILABLE("MENU_003", "현재 주문할 수 없는 메뉴입니다.", HttpStatus.BAD_REQUEST);

    private final String code;
    private final String message;
    private final HttpStatus status;

    MenuErrorCode(String code, String message, HttpStatus status) {
        this.code = code;
        this.message = message;
        this.status = status;
    }
}
