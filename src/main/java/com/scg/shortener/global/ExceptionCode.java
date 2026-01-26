package com.scg.shortener.global;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ExceptionCode {
    INVALID_REQUEST(1000, "요청 형식이 올바르지 않습니다."),

    NOT_FOUND_USER_ID(4000, "유저 id 를 찾을 수 없습니다."),
    NOT_FOUND_URL_ID(4001, "url id 를 찾을 수 없습니다.");

    private final int code;
    private final String message;
}
