package com.scg.shortener.global.error;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CustomException.class)
    protected ResponseEntity<ErrorResponse> handleCustomException(CustomException e) {
        log.error("CustomException occurred. code={}, message={}", e.getExceptionCode().getStatusCode(),
                e.getExceptionCode().getMessage(), e);
        return ErrorResponse.build(e.getExceptionCode());
    }

    @ExceptionHandler(Exception.class)
    protected ResponseEntity<ErrorResponse> handleCustomException(Exception e) {
        log.error("Exception occurred. ", e);
        return ErrorResponse.build(ExceptionCode.INTERNAL_SERVER_ERROR);
    }
}
