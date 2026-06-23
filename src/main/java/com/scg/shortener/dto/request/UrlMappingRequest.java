package com.scg.shortener.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
public class UrlMappingRequest {
    private String targetUrl;
    private String slug;
}
