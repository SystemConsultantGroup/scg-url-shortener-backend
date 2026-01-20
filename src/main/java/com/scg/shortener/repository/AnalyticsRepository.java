package com.scg.shortener.repository;

import com.scg.shortener.entity.Analytics;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AnalyticsRepository extends JpaRepository<Analytics, Analytics.AnalyticsId> {
    @Query("SELECT a.hour, a.visitCount, a.uniqueVisitCount FROM Analytics a WHERE a.slug = :slug AND a.hour BETWEEN :start AND :end")
    List<Object[]> findBySlugAndHourBetween(
            @Param("slug") String slug,
            @Param("start") int start,
            @Param("end") int end);
}
