package com.scg.shortener.UrlMapping.service;

import com.scg.shortener.UrlMapping.dto.UrlMappingDto;
import com.scg.shortener.UrlMapping.entity.UrlMapping;
import com.scg.shortener.User.entity.User;
import com.scg.shortener.global.BadRequestException;
import com.scg.shortener.global.ExceptionCode;
import com.scg.shortener.UrlMapping.dto.request.UrlMappingRequest;
import com.scg.shortener.UrlMapping.dto.response.UrlResponse;
import com.scg.shortener.UrlMapping.repository.UrlMappingRepository;
import com.scg.shortener.UrlMapping.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class UrlService {
    private final UrlMappingRepository urlMappingRepository;
    private final UserRepository userRepository;

    public UrlResponse showURL(Long id) {
        List<UrlMapping> urlMappings = urlMappingRepository.findAllByUserId(id);
        List<UrlMappingDto> urlMappingDto = urlMappings.stream()
                .map(m -> new UrlMappingDto(m.getSlug(), m.getTargetUrl(), m.getCreatedAt(), m.getUpdatedAt()))
                .collect(Collectors.toList());
        return new UrlResponse(id, urlMappingDto);
    }

    public UrlResponse addURL(UrlMappingRequest urlMappingRequest) {
        User user = userRepository.findById(urlMappingRequest.getId())
                .orElseThrow(() -> new BadRequestException(ExceptionCode.NOT_FOUND_USER_ID));
        String slug = urlMappingRequest.getSlug();
        String targetUrl = urlMappingRequest.getTargetUrl();
        UrlMapping urlMapping = new UrlMapping(user, slug, targetUrl);
        urlMappingRepository.save(urlMapping);
        return new UrlResponse(null, null);
    }

    public UrlResponse deleteURL(Long urlId) {
        urlMappingRepository.deleteById(urlId);
        return new UrlResponse(null, null);
    }

    public UrlResponse modifyURL(long urlId, UrlMappingRequest urlMappingRequest) {
        UrlMapping urlMapping = urlMappingRepository.findById(urlId).orElseThrow(
                () -> new BadRequestException(ExceptionCode.NOT_FOUND_URL_ID));
        urlMapping.updateTargetUrl(urlMappingRequest.getTargetUrl());
        urlMapping.updateSlug(urlMappingRequest.getSlug());
        return new UrlResponse(null, null);
    }
}