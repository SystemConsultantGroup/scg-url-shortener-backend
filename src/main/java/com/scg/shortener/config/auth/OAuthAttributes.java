package com.scg.shortener.config.auth;

import com.scg.shortener.domain.user.Role;
import com.scg.shortener.domain.user.UserEntity;
import lombok.Builder;
import lombok.Getter;

import java.util.Map;

@Getter
public class OAuthAttributes {
    private Map<String, Object> attributes;
    private String nameAttributeKey;
    private String name;
    private String email;
    private String picture;
    private String provider;
    private String providerId;

    @Builder
    public OAuthAttributes(Map<String, Object> attributes, String nameAttributeKey,
                           String name, String email, String picture,
                           String provider, String providerId) {
        this.attributes = attributes;
        this.nameAttributeKey = nameAttributeKey;
        this.name = name;
        this.email = email;
        this.picture = picture;
        this.provider = provider;
        this.providerId = providerId;
    }

    public static OAuthAttributes of(String registrationId, String userNameAttributeName, Map<String, Object> attributes) {
        return ofGoogle(registrationId, userNameAttributeName, attributes);
    }

    private static OAuthAttributes ofGoogle(String registrationId, String userNameAttributeName, Map<String, Object> attributes) {
        return OAuthAttributes.builder()
                .name((String) attributes.get("name"))
                .email((String) attributes.get("email"))
                .picture((String) attributes.get("picture"))
                .provider(registrationId)
                .providerId((String) attributes.get("sub"))
                .attributes(attributes)
                .nameAttributeKey(userNameAttributeName)
                .build();
    }

    public UserEntity toEntity() {
        return UserEntity.builder()
                .name(name)
                .email(email)
                .picture(picture)
                .role(Role.GUEST)
                .provider(provider)
                .providerId(providerId)
                .build();
    }
}