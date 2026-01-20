package com.scg.shortener.repository;

import com.scg.shortener.entity.Analytics;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface AnalyticsRepository extends JpaRepository<Analytics, Analytics.AnalyticsId> {
    @Query("""
            SELECT a.hour AS hour,
                    a.visitCount AS visitCount,
                    a.uniqueVisitCount AS uniqueVisitCount
            FROM Analytics a
            WHERE a.slug.id = :slug
                AND a.hour BETWEEN :start AND :end
            """)
    List<Object[]> findBySlugAndHourBetween(
            @Param("slug") long slug,
            @Param("start") int start,
            @Param("end") int end);

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
