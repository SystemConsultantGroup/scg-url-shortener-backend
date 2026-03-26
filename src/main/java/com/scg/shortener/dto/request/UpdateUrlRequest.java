package com.scg.shortener.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUrlRequest {
    private String targetUrl;
    private String slug;
}
