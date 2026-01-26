package com.scg.shortener.UrlMapping.repository;

import com.scg.shortener.UrlMapping.entity.UrlMapping;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UrlMappingRepository extends JpaRepository<UrlMapping, Long> {
    List<UrlMapping> findAllByUserId(Long userId);
}