package com.scg.shortener.service;

import com.scg.shortener.entity.URL;
import com.scg.shortener.repository.UrlRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RedirectionService {
    private final UrlRepository urlRepository;

    // todo: add cache eviction in the create, update, delete endpoints
    @Cacheable(value = "urls", key = "#slug")
    public String getTargetUrl(String slug) {
        return urlRepository.findBySlug(slug).map(URL::getTargetUrl).orElse(null);
    }
}
