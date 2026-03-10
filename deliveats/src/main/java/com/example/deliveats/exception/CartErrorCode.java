package com.example.deliveats.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum CartErrorCode implements ErrorCode {

    INVALID_CART("CART_000", "유효하지 않은 장바구니 요청입니다.", HttpStatus.BAD_REQUEST),
    CART_NOT_FOUND("CART_001", "장바구니를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    CART_ITEM_NOT_FOUND("CART_002", "장바구니 항목을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    CART_STORE_MISMATCH("CART_004", "다른 가게의 상품은 담을 수 없습니다.", HttpStatus.BAD_REQUEST);

    private final String code;
    private final String message;
    private final HttpStatus status;

    CartErrorCode(String code, String message, HttpStatus status) {
        this.code = code;
        this.message = message;
        this.status = status;
    }
}
