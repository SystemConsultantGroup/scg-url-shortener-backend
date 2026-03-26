package com.scg.shortener.controller;

import com.scg.shortener.dto.UrlSummary;
import com.scg.shortener.dto.request.UpdateUrlRequest;
import com.scg.shortener.dto.request.UrlMappingRequest;
import com.scg.shortener.dto.response.CreateUrlResponse;
import com.scg.shortener.dto.response.UpdateUrlResponse;
import com.scg.shortener.service.UrlService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class UrlController {
    private final UrlService urlService;

    @GetMapping("/urls")
    public ResponseEntity<List<UrlSummary>> showURL() {
        List<UrlSummary> urlResponse = urlService.showURL();
        if(urlResponse == null) {
            return ResponseEntity.status(401).build();
        }
        return ResponseEntity.ok(urlResponse);
    }

    @PostMapping("/urls")
    public ResponseEntity<CreateUrlResponse> addURL(@RequestBody UrlMappingRequest urlMappingRequest) {
        CreateUrlResponse createUrlResponse = urlService.addURL(urlMappingRequest);
        return ResponseEntity.ok(createUrlResponse);
    }

    @DeleteMapping("/urls/{urlId}")
    public ResponseEntity<CreateUrlResponse> deleteURL(@PathVariable Long urlId) {
        CreateUrlResponse createUrlResponse = urlService.deleteURL(urlId);
        return ResponseEntity.ok(createUrlResponse);
    }

    @PatchMapping("/urls/{urlId}")
    public ResponseEntity<UpdateUrlResponse> modifyURL(@PathVariable Long urlId, @RequestBody UpdateUrlRequest updateUrlRequest) {
        UpdateUrlResponse updateUrlResponse = urlService.modifyURL(urlId, updateUrlRequest);
        return ResponseEntity.ok(updateUrlResponse);
    }
}
