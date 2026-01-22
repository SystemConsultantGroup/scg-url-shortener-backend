package com.scg.shortener.service;

import com.scg.shortener.dto.AnalyticsResponse;
import com.scg.shortener.dto.AnalyticsResponse.DailyStat;
import com.scg.shortener.dto.AnalyticsResponse.HourlyStat;
import com.scg.shortener.entity.Analytics;
import com.scg.shortener.entity.UrlMapping;
import com.scg.shortener.repository.AnalyticsRepository;
import com.scg.shortener.repository.UrlMappingRepositry;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AnalyticsService {
    private final AnalyticsRepository analyticsRepository;
    private final UrlMappingRepositry urlMappingRepositry;
    // k: slug
    // v: high 32 bits = unique visit count, low 32 bits = visit count
    private final AtomicReference<Map<String, AtomicLong>> state = new AtomicReference<>(new ConcurrentHashMap<>());

    public void track(String slug, boolean visited) {
        long delta = 1L + (visited ? 0L : 1L << 32);
        state.get().computeIfAbsent(slug, k -> new AtomicLong()).addAndGet(delta);
    }

    @Scheduled(cron = "* * * * * *")
    public void flush() {
        int hour = (int) (System.currentTimeMillis() / 1000 / 3600);
        state.getAndSet(new ConcurrentHashMap<>()).forEach((slug, atomicData) -> {
            long data = atomicData.get();
            if (data == 0) {
                return;
            }
            int visitCount = (int) (data & 0xFFFFFFFFL);
            int uniqueVisitCount = (int) (data >> 32);
            try {
                long slugId = urlMappingRepositry.findBySlug(slug).orElseThrow().getId();
                analyticsRepository.upsert(slugId, hour, visitCount, uniqueVisitCount);
            } catch (Exception e) {
                log.error("Failed to flush analytics for slug: {}", slug, e);
            }
        });
    }

    public AnalyticsResponse getAnalyticsResponse(String slug) {
        UrlMapping urlMapping = urlMappingRepositry.findBySlug(slug).orElseThrow();
        List<Analytics> analytics = analyticsRepository.findBySlug(urlMapping);

        long totalClicks = analytics.stream().mapToLong(Analytics::getVisitCount).sum();

        List<HourlyStat> hourlyStats = analytics.stream().map(a -> {
            LocalDateTime time = LocalDateTime.ofInstant(
                    Instant.ofEpochSecond((long) a.getHour() * 3600),
                    ZoneId.systemDefault());
            return new HourlyStat(time, a.getVisitCount(), a.getUniqueVisitCount());
        }).collect(Collectors.toList());

        Map<Integer, DailyStat> dailyStatsMap = new HashMap<>();

        for (Analytics a : analytics) {
            LocalDateTime time = LocalDateTime.ofInstant(
                    Instant.ofEpochSecond((long) a.getHour() * 3600),
                    ZoneId.systemDefault());
            int day = (int) time.toLocalDate().toEpochDay();

            dailyStatsMap.compute(day, (k, v) -> {
                if (v == null) {
                    String date = DateTimeFormatter.ISO_LOCAL_DATE.format(time);
                    return new DailyStat(date, a.getVisitCount(), a.getUniqueVisitCount());
                }
                return new DailyStat(v.date(), v.count() + a.getVisitCount(),
                        v.uniqueCount() + a.getUniqueVisitCount());
            });
        }

        List<DailyStat> dailyStats = dailyStatsMap.values().stream()
                .sorted(Comparator.comparing(DailyStat::date))
                .collect(Collectors.toList());

        return new AnalyticsResponse(totalClicks, hourlyStats, dailyStats);
    }

    public List<int[]> getAnalytics(String slug, int start, int end) {
        UrlMapping urlMapping = urlMappingRepositry.findBySlug(slug).orElseThrow();
        List<Analytics> raw = analyticsRepository.findBySlug(urlMapping);
        List<int[]> result = new ArrayList<>(raw.size() + 1);
        for (Analytics a : raw) {
            if (a.getHour() >= start && a.getHour() <= end) {
                result.add(new int[] { a.getHour(), a.getVisitCount(), a.getUniqueVisitCount() });
            }
        }
        int hour = (int) (System.currentTimeMillis() / 1000 / 3600);
        if (hour >= start && hour <= end) {
            AtomicLong val = state.get().get(slug);
            if (val != null) {
                long data = val.get();
                if (data != 0) {
                    result.add(new int[] { hour, (int) (data & 0xFFFFFFFFL), (int) (data >> 32) });
                }
            }
        }
        return result;
    }
}
