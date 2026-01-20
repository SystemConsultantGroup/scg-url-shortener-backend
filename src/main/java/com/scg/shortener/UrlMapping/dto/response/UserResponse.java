package com.scg.shortener.UrlMapping.dto.response;

import com.scg.shortener.UrlMapping.dto.UrlMappingDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {
    private Long id;
    private List<UrlMappingDto> urlMappings;
}