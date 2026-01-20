package com.scg.shortener.service;

import com.scg.shortener.entity.UrlMapping;
import com.scg.shortener.repository.UrlMappingRepositry;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RedirectionService {
    private final UrlMappingRepositry urlRepository;

    // todo: add cache eviction in the create, update, delete endpoints
    @Cacheable(value = "urls", key = "#slug")
    public String getTargetUrl(String slug) {
        return urlRepository.findBySlug(slug).map(UrlMapping::getTargetUrl).orElse(null);
    }
}
