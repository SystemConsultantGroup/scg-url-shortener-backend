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
@Setter
@NoArgsConstructor
@IdClass(Analytics.AnalyticsId.class)
@Table(name = "analytics")
public class Analytics {
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AnalyticsId implements Serializable {
        private String slug;
        private int hour;
    }

    @Id
    @Column(nullable = false, length = 100)
    private String slug;

    @Id
    @Column(nullable = false)
    private int hour;

    @Column(name = "visit_count", nullable = false)
    private int visitCount = 0;

    @Column(name = "unique_visit_count", nullable = false)
    private int uniqueVisitCount = 0;

}
