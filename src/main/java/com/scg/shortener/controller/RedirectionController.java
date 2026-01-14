package com.scg.shortener.controller;

import com.scg.shortener.service.RedirectionService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
@RequiredArgsConstructor
public class RedirectionController {
    private final RedirectionService redirectionService;

    @Value("${app.dashboard-url}")
    private String dashboardUrl;

    @GetMapping("/")
    public ResponseEntity<Void> redirectToDashboard() {
        return ResponseEntity.status(HttpStatus.MOVED_PERMANENTLY).location(URI.create(dashboardUrl)).build();
    }

    @GetMapping("/{slug}")
    public ResponseEntity<Void> redirectToTarget(@PathVariable String slug) {
        String targetUrl = redirectionService.getTargetUrl(slug);
        if (targetUrl != null) {
            // we use FOUND instead of MOVED_PERMANENTLY here to open up room for change
            // responses with MOVED_PERMANENTLY are semi-permanently cached by the browser
            return ResponseEntity.status(HttpStatus.FOUND).location(URI.create(targetUrl)).build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
