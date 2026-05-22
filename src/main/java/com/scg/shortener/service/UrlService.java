package com.scg.shortener.service;

import com.scg.shortener.dto.UrlSummary;
import com.scg.shortener.dto.request.UpdateUrlRequest;
import com.scg.shortener.dto.response.UpdateUrlResponse;
import com.scg.shortener.entity.UrlMapping;
import com.scg.shortener.entity.User;
import com.scg.shortener.global.CustomException;
import com.scg.shortener.global.ExceptionCode;
import com.scg.shortener.dto.request.UrlMappingRequest;
import com.scg.shortener.dto.response.CreateUrlResponse;
import com.scg.shortener.repository.UrlMappingRepository;
import com.scg.shortener.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class UrlService {
    @Value("${app.slug-base-url}")
    private String slugBaseUrl;

    private final UrlMappingRepository urlMappingRepository;
    private final UserRepository userRepository;

    public List<UrlSummary> showURL(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(ExceptionCode.NOT_FOUND_USER_EMAIL));

        Long userId = user.getId();
        List<UrlMapping> urlMappings = urlMappingRepository.findAllByUserId(userId);
        List<UrlSummary> urlSummary = urlMappings.stream()
                .map(m -> new UrlSummary(m.getId(), m.getSlug(), m.getTargetUrl(), 0, m.getCreatedAt()))
                .collect(Collectors.toList());
        return urlSummary;
    }

    public CreateUrlResponse addURL(UrlMappingRequest urlMappingRequest, String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(ExceptionCode.NOT_FOUND_USER_EMAIL));

        String slug = urlMappingRequest.getSlug();

        if(urlMappingRepository.existsBySlug(slug)) {
            throw new CustomException(ExceptionCode.ALREADY_EXISTS_SLUG);
        }
        String targetUrl = urlMappingRequest.getTargetUrl();
        UrlMapping urlMapping = new UrlMapping(user, slug, targetUrl);
        urlMappingRepository.save(urlMapping);
        return new CreateUrlResponse(urlMapping.getId(), slugBaseUrl + urlMapping.getSlug(), urlMapping.getCreatedAt());
    }

    public void deleteURL(Long urlId, String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(ExceptionCode.NOT_FOUND_USER_EMAIL));

        UrlMapping urlMapping = urlMappingRepository.findById(urlId)
                .orElseThrow(() -> new CustomException(ExceptionCode.NOT_FOUND_URL_ID));

        if(!urlMapping.getUser().getEmail().equals(user.getEmail())) {
            throw new CustomException(ExceptionCode.NO_PERMISSION);
        }

        urlMappingRepository.delete(urlMapping);
    }

    public UpdateUrlResponse modifyURL(long urlId, UpdateUrlRequest updateUrlRequest, String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(ExceptionCode.NOT_FOUND_USER_EMAIL));

        UrlMapping urlMapping = urlMappingRepository.findById(urlId)
                .orElseThrow(() -> new CustomException(ExceptionCode.NOT_FOUND_URL_ID));

        if(!urlMapping.getUser().getEmail().equals(user.getEmail())) {
            throw new CustomException(ExceptionCode.NO_PERMISSION);
        }

        if(urlMappingRepository.existsBySlugAndIdNot(updateUrlRequest.getSlug(), urlId)) {
            throw new CustomException(ExceptionCode.ALREADY_EXISTS_SLUG);
        }

        if(updateUrlRequest.getTargetUrl() != null)
            urlMapping.updateTargetUrl(updateUrlRequest.getTargetUrl());
        if(updateUrlRequest.getSlug() != null)
            urlMapping.updateSlug(updateUrlRequest.getSlug());
        return new UpdateUrlResponse(urlId, slugBaseUrl + updateUrlRequest.getSlug(), LocalDateTime.now());
    }
}