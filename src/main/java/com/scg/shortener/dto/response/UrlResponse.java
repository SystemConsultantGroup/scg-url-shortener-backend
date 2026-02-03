package com.scg.shortener.dto.response;

import com.scg.shortener.dto.UrlMappingDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UrlResponse {
    private List<UrlMappingDto> urlMappings;
}