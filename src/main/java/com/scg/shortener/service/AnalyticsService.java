package com.scg.shortener.service;

import com.scg.shortener.repository.AnalyticsRepository;
import com.scg.shortener.repository.UrlMappingRepositry;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

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

    public List<int[]> getAnalytics(String slug, int start, int end) {
        long slugId = urlMappingRepositry.findBySlug(slug).orElseThrow().getId();
        List<Object[]> raw = analyticsRepository.findBySlugAndHourBetween(slugId, start, end);
        List<int[]> result = new ArrayList<>(raw.size() + 1);
        for (Object[] row : raw) {
            result.add(new int[] { (int) row[0], (int) row[1], (int) row[2] });
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
