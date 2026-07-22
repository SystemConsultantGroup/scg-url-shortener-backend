package com.scg.shortener.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.scg.shortener.entity.Analytics;
import com.scg.shortener.entity.UrlMapping;

public interface AnalyticsRepository extends JpaRepository<Analytics, Analytics.AnalyticsId> {
    List<Analytics> findBySlug(UrlMapping slug);

    @Modifying
    @Transactional
    @Query("DELETE FROM Analytics a WHERE a.slug = :slug")
    void deleteBySlug(@Param("slug") UrlMapping slug);

    @Modifying
    @Transactional
    @Query(nativeQuery = true, value = """
            INSERT INTO analytics (slug_id, hour, visit_count, unique_visit_count)
            VALUES (:slug, :hour, :visitCount, :uniqueVisitCount)
            ON DUPLICATE KEY UPDATE
                visit_count = visit_count + :visitCount,
                unique_visit_count = unique_visit_count + :uniqueVisitCount
            """)
    void upsert(
            @Param("slug") long slug,
            @Param("hour") int hour,
            @Param("visitCount") int visitCount,
            @Param("uniqueVisitCount") int uniqueVisitCount);
}
