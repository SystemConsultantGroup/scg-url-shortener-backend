package com.scg.shortener.config.auth;

import com.scg.shortener.domain.user.UserEntity;
import com.scg.shortener.domain.user.UserRepository;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;

@RequiredArgsConstructor
@Service
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final UserRepository userRepository;
    private final HttpSession httpSession;

    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate = new DefaultOAuth2UserService();
        OAuth2User oAuth2User = delegate.loadUser(userRequest);

        // 현재 로그인 진행 중인 서비스 구분 (google, naver 등)
        String registrationId = userRequest.getClientRegistration().getRegistrationId();

        // OAuth2 로그인 진행 시 키가 되는 필드 값 (Primary Key 역할)
        String userNameAttributeName = userRequest.getClientRegistration().getProviderDetails()
                .getUserInfoEndpoint().getUserNameAttributeName();

        // 가져온 OAuth2User의 attribute를 담을 DTO 클래스
        OAuthAttributes attributes = OAuthAttributes.of(registrationId, userNameAttributeName, oAuth2User.getAttributes());

        UserEntity userEntity = saveOrUpdate(attributes);

        return new DefaultOAuth2User(
                Collections.singleton(new SimpleGrantedAuthority(userEntity.getRoleKey())),
                attributes.getAttributes(),
                attributes.getNameAttributeKey());
    }

    /**
     * 이메일을 기준으로 기존 유저 여부를 판단하여 업데이트하거나 신규 생성합니다.
     */
    private UserEntity saveOrUpdate(OAuthAttributes attributes) {
        UserEntity userEntity = userRepository.findByEmail(attributes.getEmail())
                .map(entity -> entity.update(attributes.getName(), attributes.getPicture()))
                .orElse(attributes.toEntity());

        return userRepository.save(userEntity);
    }
}