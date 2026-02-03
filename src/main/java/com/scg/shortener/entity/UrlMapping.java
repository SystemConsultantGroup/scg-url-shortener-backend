package com.scg.shortener.entity;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
@Getter
public class UrlMapping {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(unique = true, nullable = false)
    private String slug;

    @Column(length = 2048, nullable = false)
    private String targetUrl;

    @CreatedDate
    @Column(updatable = false, nullable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    @Builder
    public UrlMapping(User user, String slug, String targetUrl) {
        this.user = user;
        this.slug = slug;
        this.targetUrl = targetUrl;
    }

    public void updateTargetUrl(String targetUrl) {
        this.targetUrl = targetUrl;
    }

    public void updateSlug(String slug) {
        this.slug = slug;
    }
}