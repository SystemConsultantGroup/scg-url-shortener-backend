package com.scg.shortener.User.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "users")
@EntityListeners(AuditingEntityListener.class)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false, unique = true)
    private String nickname;

    @Column
    private String picture;

    @Column
    private String provider;

    @Column
    private String providerId;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @Builder
    public User(String email, String nickname, String picture, String provider, String providerId) {
        this.email = email;
        this.nickname = nickname;
        this.picture = picture;
        this.provider = provider;
        this.providerId = providerId;
    }

    public User update(String nickname, String picture) {
        this.nickname = nickname;
        this.picture = picture;
        return this;
    }
}