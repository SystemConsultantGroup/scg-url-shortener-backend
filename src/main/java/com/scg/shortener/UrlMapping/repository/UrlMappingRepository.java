package com.scg.shortener.UrlMapping.repository;

import com.scg.shortener.UrlMapping.entity.UrlMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UrlMappingRepository extends JpaRepository<UrlMapping, Long> {
    Optional<UrlMapping> findBySlug(String slug);
    //List<UrlMapping> findAllById(Long id);
    List<UrlMapping> findAllByUserId(@Param("userId") Long userId);
}