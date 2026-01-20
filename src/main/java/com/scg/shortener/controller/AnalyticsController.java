package com.scg.shortener.controller;

import com.scg.shortener.dto.AnalyticsResponse;
import com.scg.shortener.service.AnalyticsService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class AnalyticsController {
    private final AnalyticsService analyticsService;

    private enum Granularity {
        HOUR,
        DAY,
    }

    /**
     * @param granularity the granularity of the analytics (HOUR or DAY)
     * @param timezone    the timezone offset in hours from UTC
     * @param start       the start time in hours since unix epoch
     * @param end         the end time in hours since unix epoch
     */
    @GetMapping("/api/v1/urls/{slug}/analytics")
    public AnalyticsResponse getAnalytics(
            @PathVariable String slug,
            @RequestParam(defaultValue = "HOUR") Granularity granularity,
            @RequestParam(defaultValue = "9") int timezone,
            @RequestParam(defaultValue = "0") int start,
            @RequestParam(defaultValue = "2147483647") int end) {
        List<int[]> stats = analyticsService.getAnalytics(slug, start, end);
        switch (granularity) {
            case HOUR:
                return new AnalyticsResponse(stats.stream().mapToInt(entry -> entry[1]).sum(), stats);
            case DAY:
                Map<Integer, int[]> map = new HashMap<>();
                for (int[] entry : stats) {
                    entry[0] -= timezone;
                    entry[0] /= 24;
                    entry[0] *= 24;
                    map.merge(entry[0], entry, (a, b) -> new int[] {
                            a[0],
                            a[1] + b[1],
                            a[2] + b[2]
                    });
                }
                return new AnalyticsResponse(
                        map.values().stream().mapToInt(entry -> entry[1]).sum(),
                        map.values().stream().toList());
            default:
                throw new RuntimeException("unreachable");
        }
    }
}
