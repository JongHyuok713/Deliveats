package com.example.deliveats.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ErrorResponseDto {

    private final String errorCode; // code
    private final String message;   // 기본 message
    private final String detail;    // 상세 메시지(옵션)

    // CustomException → ErrorResponseDto 변환
    public static ErrorResponseDto res(final CustomException ex) {
        return new ErrorResponseDto(
                ex.getErrorCode().getCode(),
                ex.getErrorCode().getMessage(),
                ex.getDetail()
        );
    }

    // ErrorCode + 상세 메시지 직접 지정
    public static ErrorResponseDto res(final ErrorCode errorCode, final String detail) {
        return new ErrorResponseDto(
                errorCode.getCode(),
                errorCode.getMessage(),
                detail
        );
    }

    // 예상치 못한 예외 → 내부 오류 응답
    public static ErrorResponseDto internal(final Exception ex) {
        return new ErrorResponseDto(
                CommonErrorCode.INTERNAL_ERROR.getCode(),
                CommonErrorCode.INTERNAL_ERROR.getMessage(),
                ex.getMessage()
        );
    }
}

