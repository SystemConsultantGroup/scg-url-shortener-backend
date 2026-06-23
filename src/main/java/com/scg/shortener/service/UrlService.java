package com.scg.shortener.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.scg.shortener.dto.request.GetUrlsRequest;
import com.scg.shortener.dto.request.UpdateUrlRequest;
import com.scg.shortener.dto.request.UrlMappingRequest;
import com.scg.shortener.dto.response.CreateUrlResponse;
import com.scg.shortener.dto.response.GetUrlsResponse;
import com.scg.shortener.dto.response.UpdateUrlResponse;
import com.scg.shortener.dto.response.UrlSummary;
import com.scg.shortener.entity.UrlMapping;
import com.scg.shortener.entity.User;
import com.scg.shortener.global.error.CustomException;
import com.scg.shortener.global.error.ExceptionCode;
import com.scg.shortener.repository.AnalyticsRepository;
import com.scg.shortener.repository.UrlMappingRepository;
import com.scg.shortener.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class UrlService {
    @Value("${app.slug-base-url}")
    private String slugBaseUrl;

    private final UrlMappingRepository urlMappingRepository;
    private final UserRepository userRepository;
    private final AnalyticsRepository analyticsRepository;

    public GetUrlsResponse showURL(GetUrlsRequest getUrlsRequest, String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(ExceptionCode.NOT_FOUND_USER_EMAIL));

        Long userId = user.getId();

        GetUrlsRequest.SortBy sortBy = getUrlsRequest.getSort();
        if (sortBy == null) {
            sortBy = GetUrlsRequest.SortBy.NEWEST;
        }

        int pageNum = (getUrlsRequest.getPage() == null || getUrlsRequest.getPage() < 1)
                ? 0
                : getUrlsRequest.getPage().intValue() - 1;
        int limitNum = (getUrlsRequest.getLimit() == null || getUrlsRequest.getLimit() < 1)
                ? 10
                : getUrlsRequest.getLimit().intValue();

        Page<UrlMapping> urlMappingsPage;

        if (sortBy == GetUrlsRequest.SortBy.MOST_CLICKS) {
            Pageable pageable = PageRequest.of(pageNum, limitNum);
            urlMappingsPage = urlMappingRepository.findAllByUserIdOrderByTotalVisitCountDesc(userId, pageable);
        } else if (sortBy == GetUrlsRequest.SortBy.LEAST_CLICKS) {
            Pageable pageable = PageRequest.of(pageNum, limitNum);
            urlMappingsPage = urlMappingRepository.findAllByUserIdOrderByTotalVisitCountAsc(userId, pageable);
        } else {
            Sort sort = switch (sortBy) {
                case NEWEST -> Sort.by(Sort.Direction.DESC, "createdAt");
                case OLDEST -> Sort.by(Sort.Direction.ASC, "createdAt");
                default -> Sort.by(Sort.Direction.DESC, "createdAt");
            };
            Pageable pageable = PageRequest.of(pageNum, limitNum, sort);
            urlMappingsPage = urlMappingRepository.findAllByUserId(userId, pageable);
        }

        List<UrlSummary> urlSummary = urlMappingsPage.stream()
                .map(m -> new UrlSummary(
                        m.getId(),
                        m.getSlug(),
                        m.getTargetUrl(),
                        m.getTotalVisitCount(),
                        m.getCreatedAt()))
                .toList();
        return GetUrlsResponse.of(urlMappingsPage, urlSummary);
    }

    public CreateUrlResponse addURL(UrlMappingRequest urlMappingRequest, String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(ExceptionCode.NOT_FOUND_USER_EMAIL));

        String slug = urlMappingRequest.getSlug();

        if (urlMappingRepository.existsBySlug(slug)) {
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

        if (!urlMapping.getUser().getEmail().equals(user.getEmail())) {
            throw new CustomException(ExceptionCode.NO_PERMISSION);
        }

        analyticsRepository.deleteBySlug(urlMapping);
        urlMappingRepository.delete(urlMapping);
    }

    public UpdateUrlResponse modifyURL(long urlId, UpdateUrlRequest updateUrlRequest, String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(ExceptionCode.NOT_FOUND_USER_EMAIL));

        UrlMapping urlMapping = urlMappingRepository.findById(urlId)
                .orElseThrow(() -> new CustomException(ExceptionCode.NOT_FOUND_URL_ID));

        if (!urlMapping.getUser().getEmail().equals(user.getEmail())) {
            throw new CustomException(ExceptionCode.NO_PERMISSION);
        }

        if (urlMappingRepository.existsBySlugAndIdNot(updateUrlRequest.getSlug(), urlId)) {
            throw new CustomException(ExceptionCode.ALREADY_EXISTS_SLUG);
        }

        if (updateUrlRequest.getTargetUrl() != null)
            urlMapping.updateTargetUrl(updateUrlRequest.getTargetUrl());
        if (updateUrlRequest.getSlug() != null)
            urlMapping.updateSlug(updateUrlRequest.getSlug());
        return new UpdateUrlResponse(urlId, slugBaseUrl + updateUrlRequest.getSlug(), LocalDateTime.now());
    }
}