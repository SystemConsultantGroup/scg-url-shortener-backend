package com.scg.shortener.service;

import com.scg.shortener.dto.UrlMappingDto;
import com.scg.shortener.entity.UrlMapping;
import com.scg.shortener.entity.User;
import com.scg.shortener.global.BadRequestException;
import com.scg.shortener.global.ExceptionCode;
import com.scg.shortener.dto.request.UrlMappingRequest;
import com.scg.shortener.dto.response.UrlResponse;
import com.scg.shortener.repository.UrlMappingRepository;
import com.scg.shortener.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
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

    public UrlResponse showURL() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication == null || !(authentication.getPrincipal() instanceof UserDetails)) {
            return new UrlResponse(null);
        }
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String email = userDetails.getUsername();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BadRequestException(ExceptionCode.NOT_FOUND_USER_EMAIL));

        Long userId = user.getId();
        List<UrlMapping> urlMappings = urlMappingRepository.findAllByUserId(userId);
        List<UrlMappingDto> urlMappingDto = urlMappings.stream()
                .map(m -> new UrlMappingDto(m.getSlug(), m.getTargetUrl(), m.getCreatedAt(), m.getUpdatedAt()))
                .collect(Collectors.toList());
        return new UrlResponse(urlMappingDto);
    }

    public UrlResponse addURL(UrlMappingRequest urlMappingRequest) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication == null || !(authentication.getPrincipal() instanceof UserDetails)) {
            return new UrlResponse(null);
        }
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String email = userDetails.getUsername();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BadRequestException(ExceptionCode.NOT_FOUND_USER_EMAIL));

        String slug = urlMappingRequest.getSlug();
        String targetUrl = urlMappingRequest.getTargetUrl();
        UrlMapping urlMapping = new UrlMapping(user, slug, targetUrl);
        urlMappingRepository.save(urlMapping);
        return new UrlResponse(null);
    }

    public UrlResponse deleteURL(Long urlId) {
        urlMappingRepository.deleteById(urlId);
        return new UrlResponse(null);
    }

    public UrlResponse modifyURL(long urlId, UrlMappingRequest urlMappingRequest) {
        UrlMapping urlMapping = urlMappingRepository.findById(urlId).orElseThrow(
                () -> new BadRequestException(ExceptionCode.NOT_FOUND_URL_ID));
        urlMapping.updateTargetUrl(urlMappingRequest.getTargetUrl());
        urlMapping.updateSlug(urlMappingRequest.getSlug());
        return new UrlResponse(null);
    }
}