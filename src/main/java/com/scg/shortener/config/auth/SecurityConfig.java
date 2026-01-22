package com.scg.shortener.config.auth;

import com.scg.shortener.config.auth.jwt.JwtAuthenticationFilter;
import com.scg.shortener.config.auth.jwt.JwtTokenProvider;
import com.scg.shortener.config.auth.jwt.OAuth2SuccessHandler;
import com.scg.shortener.domain.user.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@RequiredArgsConstructor
@EnableWebSecurity
@Configuration
public class SecurityConfig {

    private final CustomOAuth2UserService customOAuth2UserService;
    private final OAuth2SuccessHandler oAuth2SuccessHandler;
    private final JwtTokenProvider jwtTokenProvider;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // 1. CSRF 설정 (문자열로 예외 처리)
                .csrf(csrf -> csrf
                        .ignoringRequestMatchers("/h2-console/**")
                        .disable() // REST API는 보통 CSRF를 끕니다
                )

                // 2. 헤더 설정 (H2 콘솔 깨짐 방지)
                .headers(headers -> headers
                        .frameOptions(frameOptions -> frameOptions.disable())
                )

                // 3. 세션 끄기 (STATELESS)
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                // 4. URL 권한 관리 (AntPathRequestMatcher 없이 문자열 나열)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/",
                                "/css/**",
                                "/images/**",
                                "/js/**",
                                "/profile",
                                "/api/v1/auth/**"
                        ).permitAll()
                        .requestMatchers("/api/v1/**").hasRole(Role.USER.name())
                        .anyRequest().authenticated()
                )

                // 5. OAuth2 로그인 설정
                .oauth2Login(oauth2 -> oauth2
                        .authorizationEndpoint(endpoint -> endpoint
                                .baseUri("/api/v1/auth")
                        )
                        .userInfoEndpoint(userInfo -> userInfo
                                .userService(customOAuth2UserService)
                        )
                        .successHandler(oAuth2SuccessHandler)
                )

                .logout(logout -> logout
                        .logoutUrl("/logout")            // 1. 로그아웃 요청 주소
                        .logoutSuccessUrl("/")           // 2. 로그아웃 성공 시 리다이렉트 주소 (메인으로)
                        .deleteCookies("accessToken")    // 3. ★중요★ JWT 쿠키 삭제
                        .invalidateHttpSession(true)     // 4. 세션 무효화 (Stateless라도 안전장치로)
                        .clearAuthentication(true)       // 5. 인증 정보 제거
                )

                // 6. JWT 필터 추가
                .addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}