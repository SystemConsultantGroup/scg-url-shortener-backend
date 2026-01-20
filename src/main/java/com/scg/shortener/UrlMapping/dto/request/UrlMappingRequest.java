package com.scg.shortener.UrlMapping.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UrlMappingRequest {
    private Long id;
    private String slug;
    private String targetUrl;
}
