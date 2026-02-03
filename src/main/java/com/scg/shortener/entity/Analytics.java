package com.scg.shortener.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Entity
@Getter
@NoArgsConstructor
@IdClass(Analytics.AnalyticsId.class)
public class Analytics {
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AnalyticsId implements Serializable {
        private UrlMapping slug;
        private int hour;
    }

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private UrlMapping slug;

    @Id
    @Column(nullable = false)
    private int hour;

    @Column(nullable = false)
    private int visitCount = 0;

    @Column(nullable = false)
    private int uniqueVisitCount = 0;
}
