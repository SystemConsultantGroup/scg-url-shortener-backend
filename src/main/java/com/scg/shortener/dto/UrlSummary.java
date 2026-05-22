package com.scg.shortener.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UrlSummary {
    private Long urlId;
    private String slug;
    private String targetUrl;
    private long totalClicks = 0;
    private LocalDateTime createdAt;
}
