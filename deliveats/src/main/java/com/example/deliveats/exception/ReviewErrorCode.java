package com.example.deliveats.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ReviewErrorCode implements ErrorCode {

    INVALID_REVIEW("REVIEW_000", "유효하지 않은 리뷰 요청입니다.", HttpStatus.BAD_REQUEST),
    REVIEW_ALREADY_EXISTS("REVIEW_001", "이미 작성된 리뷰입니다.", HttpStatus.CONFLICT),
    REVIEW_RATING_ERROR("REVIEW_002", "잘못된 별점입니다.", HttpStatus.CONFLICT);

    private final String code;
    private final String message;
    private final HttpStatus status;

    ReviewErrorCode(String code, String message, HttpStatus status) {
        this.code = code;
        this.message = message;
        this.status = status;
    }
}
