package com.scg.shortener.repository;

import com.scg.shortener.entity.URL;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UrlRepository extends JpaRepository<URL, Integer> {
    Optional<URL> findBySlug(String slug);
}
