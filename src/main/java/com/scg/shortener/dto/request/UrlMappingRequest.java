package com.scg.shortener.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UrlMappingRequest {
    private String slug;
    private String targetUrl;
}
