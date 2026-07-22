package com.scg.shortener.global.error;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ExceptionCode {
    INVALID_REQUEST(HttpStatus.BAD_REQUEST, "요청 형식이 올바르지 않습니다."),

    NOT_FOUND_USER_ID(HttpStatus.NOT_FOUND, "유저 id 를 찾을 수 없습니다."),
    NOT_FOUND_URL_ID(HttpStatus.NOT_FOUND, "url id 를 찾을 수 없습니다."),
    NOT_FOUND_USER_EMAIL(HttpStatus.NOT_FOUND, "유저 email 을 찾을 수 없습니다."),
    ALREADY_EXISTS_SLUG(HttpStatus.CONFLICT, "이미 존재하는 slug 입니다."),
    NO_PERMISSION(HttpStatus.FORBIDDEN, "권한이 없습니다."),

    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 오류");

    private final HttpStatus statusCode;
    private final String message;
}
