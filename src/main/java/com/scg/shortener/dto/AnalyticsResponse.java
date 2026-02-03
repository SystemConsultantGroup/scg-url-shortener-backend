package com.scg.shortener.dto;

import java.time.LocalDateTime;
import java.util.List;

public record AnalyticsResponse(long totalClicks, List<HourlyStat> hourlyStats, List<DailyStat> dailyStats) {
    public record HourlyStat(LocalDateTime time, long count, long uniqueCount) {
    }

    public record DailyStat(String date, long count, long uniqueCount) {
    }
}