package com.scg.shortener.controller;

import com.scg.shortener.dto.AnalyticsResponse;
import com.scg.shortener.entity.UrlMapping;
import com.scg.shortener.repository.UrlMappingRepositry;
import com.scg.shortener.service.AnalyticsService;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;

@RestController
@RequiredArgsConstructor
public class AnalyticsController {
    private final AnalyticsService analyticsService;
    private final UrlMappingRepositry urlMappingRepositry;

    @GetMapping("/api/v1/urls/{slug}/analytics")
    public AnalyticsResponse getAnalytics(
            @PathVariable String slug,
            Principal principal) {
        UrlMapping urlMapping = urlMappingRepositry.findBySlug(slug)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "URL not found"));

        // if (principal == null ||
        // !urlMapping.getUser().getEmail().equals(principal.getName())) {
        // throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Forbidden");
        // }

        return analyticsService.getAnalyticsResponse(slug);
    }
}
