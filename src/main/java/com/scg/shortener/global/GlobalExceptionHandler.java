package com.scg.shortener.global;

import com.scg.shortener.dto.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CustomException.class)
    protected ResponseEntity<ErrorResponse> handleCustomException(ExceptionCode e) {
        log.error("CustomException occurred. code={}, message={}", e.getStatusCode(), e.getStatusCode(), e);
        return ErrorResponse.build(e);
    }

    @ExceptionHandler(Exception.class)
    protected ResponseEntity<ErrorResponse> handleCustomException(Exception e) {
        log.error("Exception occurred. ", e);
        return ErrorResponse.build(ExceptionCode.INTERNAL_SERVER_ERROR);
    }
}
