package com.scg.shortener.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CreateUrlResponse {
    private Long urlId;
    private String shortUrl;
    private LocalDateTime createdAt;
}