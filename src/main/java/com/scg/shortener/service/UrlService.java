package com.scg.shortener.service;

import com.scg.shortener.dto.UrlSummary;
import com.scg.shortener.dto.request.GetUrlsRequest;
import com.scg.shortener.dto.request.UpdateUrlRequest;
import com.scg.shortener.dto.response.GetUrlsResponse;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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

    public GetUrlsResponse showURL(GetUrlsRequest getUrlsRequest, String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(ExceptionCode.NOT_FOUND_USER_EMAIL));

        Long userId = user.getId();
        Sort sort = switch (getUrlsRequest.getSort()) {
            case NEWEST -> Sort.by(Sort.Direction.DESC, "createdAt");
            case OLDEST -> Sort.by(Sort.Direction.DESC, "createdAt");
            case MOST_CLICKS -> Sort.by(Sort.Direction.ASC, "totalClicks");
            case LEAST_CLICKS -> Sort.by(Sort.Direction.DESC, "totalClicks");
        };
        Pageable pageable = PageRequest.of(getUrlsRequest.getPage().intValue() - 1, getUrlsRequest.getLimit().intValue(), sort);
        Page<UrlMapping> urlMappingsPage = urlMappingRepository.findAllByUserId(userId, pageable);
        List<UrlSummary> urlSummary = urlMappingsPage.stream()
                .map(m -> new UrlSummary(m.getId(), m.getSlug(), m.getTargetUrl(), 0, m.getCreatedAt()))
                .toList();
        return GetUrlsResponse.of(urlMappingsPage, urlSummary);
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