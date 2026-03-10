package com.example.deliveats.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum DeliveryErrorCode implements ErrorCode {

    INVALID_DELIVERY("DELIVERY_000", "유효하지 않은 배달 요청입니다.", HttpStatus.BAD_REQUEST),
    DELIVERY_NOT_FOUND("DELIVERY_001", "배달 정보를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    DELIVERY_INVALID_STATUS("DELIVERY_002", "유효하지 않은 배달 상태입니다.", HttpStatus.BAD_REQUEST);

    private final String code;
    private final String message;
    private final HttpStatus status;

    DeliveryErrorCode(String code, String message, HttpStatus status) {
        this.code = code;
        this.message = message;
        this.status = status;
    }
}
