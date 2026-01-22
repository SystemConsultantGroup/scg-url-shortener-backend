package com.scg.shortener.config.auth.jwt;

import com.scg.shortener.domain.user.Role;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;

@RequiredArgsConstructor
@Component
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtTokenProvider tokenProvider;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        // 1. 인증된 사용자 정보 가져오기
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        Map<String, Object> attributes = oAuth2User.getAttributes();

        String email = (String) attributes.get("email");
        String role = Role.USER.getKey(); // 기본 권한

        // 2. JWT 토큰 생성
        String token = tokenProvider.createToken(email, role);

        // 3. HTTP-only 쿠키 생성
        Cookie cookie = new Cookie("accessToken", token);
        cookie.setHttpOnly(true); // 자바스크립트 접근 불가 (보안)
        cookie.setSecure(false);  // 로컬(http) 환경이므로 false. 배포(https) 시 true로 변경 필요
        cookie.setPath("/");      // 모든 경로에서 쿠키 접근 가능
        cookie.setMaxAge(60 * 60 * 24); // 24시간 (초 단위)

        // 4. 응답 헤더에 쿠키 추가
        response.addCookie(cookie);

        // 5. 메인 화면으로 리다이렉트
        getRedirectStrategy().sendRedirect(request, response, "/");
    }
}