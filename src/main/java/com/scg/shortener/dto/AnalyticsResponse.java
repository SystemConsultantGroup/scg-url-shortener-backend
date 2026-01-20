package com.scg.shortener.dto;

import java.util.List;

public record AnalyticsResponse(
        // total number of visits in the period
        int total,
        // [hours since unix epoch, visitCount, uniqueVisitCount][]
        List<int[]> stats) {
}
