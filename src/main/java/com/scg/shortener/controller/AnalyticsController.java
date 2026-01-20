package com.scg.shortener.controller;

import com.scg.shortener.service.AnalyticsService;

import java.util.List;
import java.util.Optional;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class AnalyticsController {
    private final AnalyticsService analyticsService;

    // returns [hour, visitCount, uniqueVisitCount][]
    // where hour is in hours since unix epoch
    @GetMapping("/api/v1/urls/{slug}/analytics")
    public List<int[]> getAnalytics(
            @PathVariable String slug,
            @RequestParam(required = false) Integer start,
            @RequestParam(required = false) Integer end) {
        int s = Optional.ofNullable(start).orElse(0);
        int e = Optional.ofNullable(end).orElse(Integer.MAX_VALUE);
        return analyticsService.getAnalytics(slug, s, e);
    }
}
