package com.scg.shortener.controller;

import com.scg.shortener.dto.AnalyticsResponse;
import com.scg.shortener.entity.UrlMapping;
import com.scg.shortener.repository.UrlMappingRepositry;
import com.scg.shortener.service.AnalyticsService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;

@RestController
@RequiredArgsConstructor
public class AnalyticsController {
    private final UrlMappingRepositry urlMappingRepositry;
    private final AnalyticsService analyticsService;

    @Transactional
    @GetMapping("/urls/{slug}/analytics")
    public AnalyticsResponse getAnalytics(
            @PathVariable String slug,
            Principal principal) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }
        String username = null;
        if (authentication.getPrincipal() instanceof UserDetails details) {
            username = details.getUsername();
        }
        UrlMapping urlMapping = urlMappingRepositry.findBySlug(slug)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "URL not found"));
        if (!urlMapping.getUser().getEmail().equals(username)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Forbidden");
        }
        return analyticsService.getAnalyticsResponse(slug);
    }
}
