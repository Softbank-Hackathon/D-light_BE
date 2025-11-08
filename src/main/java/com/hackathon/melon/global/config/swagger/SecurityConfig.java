package com.hackathon.melon.global.config.swagger;

import com.hackathon.melon.domain.auth.handler.OAuth2AuthenticationFailureHandler;
import com.hackathon.melon.domain.auth.handler.OAuth2AuthenticationSuccessHandler;
import com.hackathon.melon.domain.auth.service.CustomOAuth2UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod; // ✅ 스프링 HttpMethod
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomOAuth2UserService customOAuth2UserService;
    private final OAuth2AuthenticationSuccessHandler oAuth2SuccessHandler;
    private final OAuth2AuthenticationFailureHandler oAuth2FailureHandler;

    @Value("${app.oauth2.redirect-uri:http://localhost:3000}")
    private String frontendUrl;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // CORS
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                // CSRF: 전역 비활성 대신, 콜백만 예외로 두는 패턴 권장
                .csrf(csrf -> csrf
                        .ignoringRequestMatchers("/api/v1/auth/cfn-callback",
                                "/swagger-ui/**", "/v3/api-docs/**") // 필요 시
                )

                // 인가 규칙은 한 번에!
                .authorizeHttpRequests(auth -> auth
                        // 서버-서버 콜백: 세션 없이 허용 (POST/OPTIONS)
                        .requestMatchers(HttpMethod.POST, "/api/v1/auth/cfn-callback").permitAll()
                        .requestMatchers(HttpMethod.OPTIONS, "/api/v1/auth/cfn-callback").permitAll()

                        // 헬스/오류/스웨거
                        .requestMatchers("/", "/health", "/error").permitAll()
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()

                        // OAuth2 엔드포인트
                        .requestMatchers("/oauth2/**", "/login/**").permitAll()

                        // 그 외 API는 인증 필요
                        .requestMatchers("/api/**").authenticated()
                        .anyRequest().authenticated()
                )

                // OAuth2 로그인
                .oauth2Login(oauth -> oauth
                        .userInfoEndpoint(u -> u.userService(customOAuth2UserService))
                        .successHandler(oAuth2SuccessHandler)
                        .failureHandler(oAuth2FailureHandler)
                )

                // 로그아웃
                .logout(l -> l.logoutSuccessUrl(frontendUrl));

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        var cfg = new CorsConfiguration();
        // credentials=true 이면 정확한 오리진 명시 (와일드카드 X)
        cfg.setAllowedOrigins(List.of(
                frontendUrl,               // 예: http://localhost:3000
                "http://localhost:3000"    // 로컬 고정값 보강
        ));
        cfg.setAllowedMethods(List.of("GET","POST","PUT","DELETE","PATCH","OPTIONS"));
        cfg.setAllowedHeaders(List.of("*"));
        cfg.setAllowCredentials(true);
        cfg.setMaxAge(3600L);

        var source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", cfg);
        return source;
    }
}
