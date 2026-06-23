package com.scg.shortener.repository;

import com.scg.shortener.entity.UrlMapping;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface UrlMappingRepository extends JpaRepository<UrlMapping, Long> {
    Page<UrlMapping> findAllByUserId(Long userId, Pageable pageable);

    Page<UrlMapping> findAllByUserIdOrderByTotalVisitCountDesc(Long userId, Pageable pageable);

    Page<UrlMapping> findAllByUserIdOrderByTotalVisitCountAsc(Long userId, Pageable pageable);

    @Modifying
    @Transactional
    @Query(nativeQuery = true, value = """
            UPDATE url_mapping 
            SET total_visit_count = total_visit_count + :visitCount,
                total_unique_visit_count = total_unique_visit_count + :uniqueVisitCount
            WHERE id = :id
            """)
    void incrementVisitCounts(
            @Param("id") long id,
            @Param("visitCount") int visitCount,
            @Param("uniqueVisitCount") int uniqueVisitCount);

    boolean existsBySlug(String slug);
    boolean existsBySlugAndIdNot(String slug, Long id);
    Optional<UrlMapping> findBySlug(String slug);
}