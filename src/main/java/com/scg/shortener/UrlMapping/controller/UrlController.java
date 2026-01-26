package com.scg.shortener.UrlMapping.controller;

import com.scg.shortener.UrlMapping.dto.request.UrlMappingRequest;
import com.scg.shortener.UrlMapping.dto.response.UrlResponse;
import com.scg.shortener.UrlMapping.service.UrlService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class UrlController {
    private final UrlService urlService;

    @GetMapping("/urls")
    public ResponseEntity<UrlResponse> showURL() {
        Long userId = 1L; // 추후 인증정보에서 가져올 예정
        UrlResponse urlResponse = urlService.showURL(userId);
        return ResponseEntity.ok(urlResponse);
    }

    @PostMapping("/urls")
    public ResponseEntity<UrlResponse> addURL(@RequestBody UrlMappingRequest urlMappingRequest) {
        UrlResponse urlResponse = urlService.addURL(urlMappingRequest);
        return ResponseEntity.ok(urlResponse);
    }

    @DeleteMapping("/urls/{urlId}")
    public ResponseEntity<UrlResponse> deleteURL(@PathVariable Long urlId) {
        UrlResponse urlResponse = urlService.deleteURL(urlId);
        return ResponseEntity.ok(urlResponse);
    }

    @PatchMapping("/urls/{urlId}")
    public ResponseEntity<UrlResponse> modifyURL(@PathVariable Long urlId, @RequestBody UrlMappingRequest urlMappingRequest) {
        UrlResponse urlResponse = urlService.modifyURL(urlId, urlMappingRequest);
        return ResponseEntity.ok(urlResponse);
    }
}
