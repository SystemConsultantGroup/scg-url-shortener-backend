package com.scg.shortener.repository;

import com.scg.shortener.entity.UrlMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UrlMappingRepositry extends JpaRepository<UrlMapping, Long> {
    Optional<UrlMapping> findBySlug(String slug);
}
