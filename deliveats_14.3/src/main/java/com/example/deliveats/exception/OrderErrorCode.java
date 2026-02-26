package com.example.deliveats.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum OrderErrorCode implements ErrorCode {

    INVALID_ORDER("ORDER_000", "유효하지 않은 주문 요청입니다.", HttpStatus.BAD_REQUEST),
    ORDER_NOT_FOUND("ORDER_001", "주문을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),

    INVALID_ORDER_STATUS("ORDER_002", "주문 상태가 올바르지 않습니다.", HttpStatus.BAD_REQUEST),
    ORDER_ALREADY_COMPLETED("ORDER_003", "이미 완료된 주문입니다.", HttpStatus.BAD_REQUEST);

    private final String code;
    private final String message;
    private final HttpStatus status;

    OrderErrorCode(String code, String message, HttpStatus status) {
        this.code = code;
        this.message = message;
        this.status = status;
    }
}
