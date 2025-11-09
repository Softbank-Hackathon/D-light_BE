package com.hackathon.melon.global.config.swagger;

import com.hackathon.melon.domain.auth.handler.OAuth2AuthenticationFailureHandler;
import com.hackathon.melon.domain.auth.handler.OAuth2AuthenticationSuccessHandler;
import com.hackathon.melon.domain.auth.service.CustomOAuth2UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.http.HttpMethod;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final CustomOAuth2UserService customOAuth2UserService;
    private final OAuth2AuthenticationSuccessHandler oAuth2SuccessHandler;
    private final OAuth2AuthenticationFailureHandler oAuth2FailureHandler;

    @Value("${app.oauth2.redirect-uri:http://localhost:3000}")
    private String frontendUrl;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        // CORS 설정
        http.cors(cors -> cors.configurationSource(corsConfigurationSource()));

        // CSRF 비활성화 (프론트엔드 분리 환경)
        http.csrf(csrf -> csrf.disable());

        // SameSite 쿠키 설정 완화 (HTTPS ↔ HTTP 크로스 도메인 허용)
        http.sessionManagement(session -> session
                .sessionFixation().changeSessionId()
        );

        // 인증/인가 설정
        http.authorizeHttpRequests(auth -> auth

                // 서버-서버 콜백: 세션 없이 허용 (POST/OPTIONS)
                .requestMatchers(HttpMethod.POST, "/api/v1/auth/cfn-callback").permitAll()
                .requestMatchers(HttpMethod.OPTIONS, "/api/v1/auth/cfn-callback").permitAll()

                // 공개 경로 (인증 불필요)
                .requestMatchers("/", "/health", "/error").permitAll()
                // OAuth2 로그인 관련 경로는 Spring Security가 자동 처리
                .requestMatchers("/oauth2/**", "/login/**").permitAll()
                // API 경로는 인증 필요
                .requestMatchers("/api/**").authenticated()
                // swagger 열려라
                .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
                // 그 외 모든 요청은 인증 필요
                .anyRequest().authenticated()
        );

        // OAuth2 로그인 설정
        http.oauth2Login(oauth -> oauth
                .userInfoEndpoint(u -> u.userService(customOAuth2UserService))
                .successHandler(oAuth2SuccessHandler)
                .failureHandler(oAuth2FailureHandler)
        );

        // 로그아웃 설정
        http.logout(l -> l.logoutSuccessUrl(frontendUrl));

        return http.build();
    }

    /**
     * CORS 설정
     * 프론트엔드(3000포트)에서 백엔드(8080포트)로 쿠키를 포함한 요청 가능하도록 설정
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // 허용할 출처 (프론트엔드 URL + 백엔드 자신)
        configuration.setAllowedOrigins(List.of(
                "https://dlite.vercel.app",   // 프론트엔드 (프로덕션)
                "https://dlite.vercel.app/",
                "http://54.180.117.76:8080",  // 백엔드 자신 (Swagger OAuth2 로그인용)
                "http://localhost:8080",      // 로컬 백엔드
                "http://localhost:3000"       // 로컬 프론트엔드
        ));

        // 허용할 HTTP 메서드
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));

        // 허용할 헤더
        configuration.setAllowedHeaders(Arrays.asList("*"));

        // 쿠키를 포함한 요청 허용 (세션 기반 인증에 필수)
        configuration.setAllowCredentials(true);

        // preflight 요청 캐시 시간 (초)
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
