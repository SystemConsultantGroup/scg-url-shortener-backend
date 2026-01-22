package com.scg.shortener.dto;

import com.scg.shortener.domain.user.UserEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UserResponseDto {
    private String name;
    private String email;
    private String picture;
    private String role;

    public UserResponseDto(UserEntity entity) {
        this.name = entity.getName();
        this.email = entity.getEmail();
        this.picture = entity.getPicture();
        this.role = entity.getRole().getKey();
    }
}