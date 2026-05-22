package com.scg.shortener.dto;

import com.scg.shortener.entity.User;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UserResponseDto {
    private String nickname;
    private String email;

    public UserResponseDto(User entity) {
        this.nickname = entity.getNickname();
        this.email = entity.getEmail();
    }
}