package com.scg.shortener.global.error;

import lombok.Builder;
import lombok.Getter;
import org.springframework.http.ResponseEntity;

@Getter
@Builder
public class ErrorResponse {
    private int statusCode;
    private String message;

    public static ResponseEntity<ErrorResponse> build(ExceptionCode exceptionCode) {
        return ResponseEntity
                .status(exceptionCode.getStatusCode().value())
                .body(ErrorResponse
                        .builder()
                        .statusCode(exceptionCode.getStatusCode().value())
                        .message(exceptionCode.getMessage())
                        .build());
    }
}
