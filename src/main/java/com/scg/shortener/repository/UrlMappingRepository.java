package com.scg.shortener.repository;

import com.scg.shortener.entity.UrlMapping;
import com.scg.shortener.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UrlMappingRepository extends JpaRepository<UrlMapping, Long> {
    List<UrlMapping> findAllByUserId(Long userId);
    boolean existsBySlug(String slug);
    boolean existsBySlugAndIdNot(String slug, Long id);
    Optional<UrlMapping> findBySlug(String slug);
}