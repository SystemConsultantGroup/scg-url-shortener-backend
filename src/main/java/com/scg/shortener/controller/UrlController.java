package com.scg.shortener.controller;

import com.scg.shortener.dto.UrlSummary;
import com.scg.shortener.dto.request.UpdateUrlRequest;
import com.scg.shortener.dto.request.UrlMappingRequest;
import com.scg.shortener.dto.response.CreateUrlResponse;
import com.scg.shortener.dto.response.UpdateUrlResponse;
import com.scg.shortener.service.UrlService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(headers = "Host=${app.api-base-domain}")
@PreAuthorize("isAuthenticated()")
public class UrlController {
    private final UrlService urlService;

    @GetMapping("/urls")
    public ResponseEntity<List<UrlSummary>> showURL(
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        List<UrlSummary> urlResponse = urlService.showURL(userDetails.getUsername());
        return ResponseEntity.ok(urlResponse);
    }

    @PostMapping("/urls")
    public ResponseEntity<CreateUrlResponse> addURL(
            @RequestBody UrlMappingRequest urlMappingRequest,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        CreateUrlResponse createUrlResponse = urlService.addURL(urlMappingRequest, userDetails.getUsername());
        return ResponseEntity.ok(createUrlResponse);
    }

    @DeleteMapping("/urls/{urlId}")
    public ResponseEntity<Void> deleteURL(
            @PathVariable Long urlId,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        urlService.deleteURL(urlId, userDetails.getUsername());
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/urls/{urlId}")
    public ResponseEntity<UpdateUrlResponse> modifyURL(
            @PathVariable Long urlId,
            @RequestBody UpdateUrlRequest updateUrlRequest,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        UpdateUrlResponse updateUrlResponse = urlService.modifyURL(urlId, updateUrlRequest, userDetails.getUsername());
        return ResponseEntity.ok(updateUrlResponse);
    }
}
