package com.scg.shortener.repository;

import com.scg.shortener.entity.UrlMapping;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UrlMappingRepository extends JpaRepository<UrlMapping, Long> {
    List<UrlMapping> findAllByUserId(Long userId);
}