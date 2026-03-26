package com.scg.shortener.service;

import com.scg.shortener.dto.UrlSummary;
import com.scg.shortener.dto.request.UpdateUrlRequest;
import com.scg.shortener.dto.response.UpdateUrlResponse;
import com.scg.shortener.entity.UrlMapping;
import com.scg.shortener.entity.User;
import com.scg.shortener.global.BadRequestException;
import com.scg.shortener.global.ExceptionCode;
import com.scg.shortener.dto.request.UrlMappingRequest;
import com.scg.shortener.dto.response.CreateUrlResponse;
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

    public List<UrlSummary> showURL() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication == null || !(authentication.getPrincipal() instanceof UserDetails)) {
            return null;
        }
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String email = userDetails.getUsername();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BadRequestException(ExceptionCode.NOT_FOUND_USER_EMAIL));

        Long userId = user.getId();
        List<UrlMapping> urlMappings = urlMappingRepository.findAllByUserId(userId);
        List<UrlSummary> urlSummary = urlMappings.stream()
                .map(m -> new UrlSummary(m.getId(), m.getSlug(), m.getTargetUrl(), 0, m.getCreatedAt()))
                .collect(Collectors.toList());
        return urlSummary;
    }

    public CreateUrlResponse addURL(UrlMappingRequest urlMappingRequest) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication == null || !(authentication.getPrincipal() instanceof UserDetails)) {
            throw new BadRequestException(ExceptionCode.NOT_FOUND_USER_ID);
        }
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String email = userDetails.getUsername();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BadRequestException(ExceptionCode.NOT_FOUND_USER_EMAIL));

        String slug = urlMappingRequest.getSlug();

        if(urlMappingRepository.existsBySlug(slug)) {
            throw new BadRequestException(ExceptionCode.ALREADY_EXISTS_SLUG);
        }
        String targetUrl = urlMappingRequest.getTargetUrl();
        UrlMapping urlMapping = new UrlMapping(user, slug, targetUrl);
        urlMappingRepository.save(urlMapping);
        return new CreateUrlResponse(urlMapping.getId(), "https://scg.sh/" + urlMapping.getSlug(), urlMapping.getCreatedAt());
    }

    public CreateUrlResponse deleteURL(Long urlId) {
        urlMappingRepository.deleteById(urlId);
        return new CreateUrlResponse(null, null, null);
    }

    public UpdateUrlResponse modifyURL(long urlId, UpdateUrlRequest updateUrlRequest) {
        UrlMapping urlMapping = urlMappingRepository.findById(urlId).orElseThrow(
                () -> new BadRequestException(ExceptionCode.NOT_FOUND_URL_ID));
        urlMapping.updateTargetUrl(updateUrlRequest.getTargetUrl());
        urlMapping.updateSlug(updateUrlRequest.getSlug());
        return new UpdateUrlResponse(urlId, "https://scg.sh/" + updateUrlRequest.getSlug(), urlMapping.getUpdatedAt());
    }
}