package com.scg.shortener.dto.response;

import com.scg.shortener.entity.User;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class GetUserResponse {
    private String nickname;
    private String email;

    public GetUserResponse(User entity) {
        this.nickname = entity.getNickname();
        this.email = entity.getEmail();
    }
}