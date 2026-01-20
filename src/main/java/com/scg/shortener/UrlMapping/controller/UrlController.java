package com.scg.shortener.UrlMapping.controller;

import com.scg.shortener.UrlMapping.dto.request.UrlMappingRequest;
import com.scg.shortener.UrlMapping.dto.response.UserResponse;
import com.scg.shortener.UrlMapping.service.UrlService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class UrlController {
    private final UrlService urlService;

    @GetMapping("/urls")
    public ResponseEntity<UserResponse> showURL() {
        Long userId = 1L; // 추후 인증정보에서 가져올 예정
        UserResponse userResponse = urlService.showURL(userId);
        return ResponseEntity.ok(userResponse);
    }

    @PostMapping("/urls")
    public ResponseEntity<UserResponse> addURL(@RequestBody UrlMappingRequest urlMappingRequest) {
        UserResponse userResponse = urlService.addURL(urlMappingRequest);
        return ResponseEntity.ok(userResponse);
    }

    @DeleteMapping("/urls/{urlId}")
    public ResponseEntity<UserResponse> deleteURL(@PathVariable Long urlId) {
        UserResponse userResponse = urlService.deleteURL(urlId);
        return ResponseEntity.ok(userResponse);
    }

    @PatchMapping("/urls/{urlId}")
    public ResponseEntity<UserResponse> modifyURL(@PathVariable Long urlId, @RequestBody UrlMappingRequest urlMappingRequest) {
        UserResponse userResponse = urlService.modifyURL(urlId, urlMappingRequest);
        return ResponseEntity.ok(userResponse);
    }
}
