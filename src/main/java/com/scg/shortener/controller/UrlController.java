package com.scg.shortener.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.scg.shortener.dto.request.GetUrlsRequest;
import com.scg.shortener.dto.request.UpdateUrlRequest;
import com.scg.shortener.dto.request.UrlMappingRequest;
import com.scg.shortener.dto.response.CreateUrlResponse;
import com.scg.shortener.dto.response.GetUrlsResponse;
import com.scg.shortener.dto.response.UpdateUrlResponse;
import com.scg.shortener.global.config.routing.DynamicHostRoute;
import com.scg.shortener.service.UrlService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@DynamicHostRoute("app.api-base-domain")
@PreAuthorize("isAuthenticated()")
public class UrlController {
    private final UrlService urlService;

    @GetMapping("/urls")
    public ResponseEntity<GetUrlsResponse> showURL(
            GetUrlsRequest getUrlsRequest,
            @AuthenticationPrincipal UserDetails userDetails) {
        GetUrlsResponse getUrlsResponse = urlService.showURL(getUrlsRequest, userDetails.getUsername());
        return ResponseEntity.ok(getUrlsResponse);
    }

    @PostMapping("/urls")
    public ResponseEntity<CreateUrlResponse> addURL(
            @RequestBody UrlMappingRequest urlMappingRequest,
            @AuthenticationPrincipal UserDetails userDetails) {
        CreateUrlResponse createUrlResponse = urlService.addURL(urlMappingRequest, userDetails.getUsername());
        return ResponseEntity.ok(createUrlResponse);
    }

    @DeleteMapping("/urls/{urlId}")
    public ResponseEntity<Void> deleteURL(
            @PathVariable Long urlId,
            @AuthenticationPrincipal UserDetails userDetails) {
        urlService.deleteURL(urlId, userDetails.getUsername());
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/urls/{urlId}")
    public ResponseEntity<UpdateUrlResponse> modifyURL(
            @PathVariable Long urlId,
            @RequestBody UpdateUrlRequest updateUrlRequest,
            @AuthenticationPrincipal UserDetails userDetails) {
        UpdateUrlResponse updateUrlResponse = urlService.modifyURL(urlId, updateUrlRequest, userDetails.getUsername());
        return ResponseEntity.ok(updateUrlResponse);
    }
}
