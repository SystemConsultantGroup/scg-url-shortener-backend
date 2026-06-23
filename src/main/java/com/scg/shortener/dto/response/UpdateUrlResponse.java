package com.scg.shortener.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class UpdateUrlResponse {
    private Long urlId;
    private String shortenedUrl;
    private LocalDateTime updateAt;
}
