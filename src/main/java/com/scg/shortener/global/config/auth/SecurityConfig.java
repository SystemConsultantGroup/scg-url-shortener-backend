package com.scg.shortener.global.config.auth;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.scg.shortener.global.config.AppProperties;
import com.scg.shortener.global.config.auth.jwt.JwtAuthenticationFilter;
import com.scg.shortener.global.config.auth.jwt.JwtTokenProvider;
import com.scg.shortener.global.config.auth.jwt.OAuth2SuccessHandler;
import com.scg.shortener.global.error.ErrorResponse;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import tools.jackson.databind.ObjectMapper;

@RequiredArgsConstructor
@EnableWebSecurity
@EnableMethodSecurity
@Configuration
@EnableConfigurationProperties(AppProperties.class) // 중요: 이 설정을 활성화해야 함
public class SecurityConfig {

        private final AppProperties appProperties;

        public void configureSecurity() {
                List<String> origins = appProperties.allowedOrigins();
        }

        private final CustomOAuth2UserService customOAuth2UserService;
        private final OAuth2SuccessHandler oAuth2SuccessHandler;
        private final JwtTokenProvider jwtTokenProvider;

        @Bean
        public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
                http
                                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                                .csrf(AbstractHttpConfigurer::disable)
                                .formLogin(AbstractHttpConfigurer::disable)
                                .httpBasic(AbstractHttpConfigurer::disable)

                                .sessionManagement(session -> session
                                                .sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                                .exceptionHandling(ex -> ex
                                                .authenticationEntryPoint(new JsonAuthenticationEntryPoint()))

                                .authorizeHttpRequests(auth -> auth
                                                .anyRequest().permitAll())

                                .oauth2Login(oauth2 -> oauth2
                                                .authorizationEndpoint(endpoint -> endpoint
                                                                .baseUri("/auth"))
                                                .userInfoEndpoint(userInfo -> userInfo
                                                                .userService(customOAuth2UserService))
                                                .successHandler(oAuth2SuccessHandler))

                                .addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider),
                                                UsernamePasswordAuthenticationFilter.class);

                return http.build();
        }

        @Bean
        public CorsConfigurationSource corsConfigurationSource() {
                CorsConfiguration configuration = new CorsConfiguration();
                configuration.setAllowCredentials(true);
                configuration.setAllowedOrigins(appProperties.allowedOrigins());
                configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
                configuration.setAllowedHeaders(List.of("*"));
                configuration.setMaxAge(3600L);

                UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
                source.registerCorsConfiguration("/**", configuration);
                return source;
        }

        private static class JsonAuthenticationEntryPoint implements AuthenticationEntryPoint {
                private static final ObjectMapper objectMapper = new ObjectMapper();

                @Override
                public void commence(HttpServletRequest request, HttpServletResponse response,
                                AuthenticationException authException) throws IOException {
                        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                        response.setCharacterEncoding(StandardCharsets.UTF_8.name());

                        ErrorResponse errorResponse = ErrorResponse.builder()
                                        .statusCode(HttpServletResponse.SC_UNAUTHORIZED)
                                        .message("인증이 필요합니다.")
                                        .build();

                        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
                }
        }
}