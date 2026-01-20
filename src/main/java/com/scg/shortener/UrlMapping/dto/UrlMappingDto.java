package com.scg.shortener.UrlMapping.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UrlMappingDto {
    private String slug;
    private String targetUrl;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
