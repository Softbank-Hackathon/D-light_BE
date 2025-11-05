package com.hackathon.melon.global.config.swagger;

import com.hackathon.melon.domain.auth.service.CustomOAuth2UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final CustomOAuth2UserService customOAuth2UserService;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
//        http.csrf(csrf -> csrf.disable());
//        http.authorizeHttpRequests(auth -> auth
//                .requestMatchers("/", "/health", "/error").permitAll()
//                .anyRequest().authenticated()
//        );
        http.oauth2Login(oauth -> oauth
                .userInfoEndpoint(u -> u.userService(customOAuth2UserService))
                // .successHandler(oAuth2SuccessHandler)   // TODO: 구현 필요
                // .failureHandler(oAuth2FailureHandler)   // TODO: 구현 필요
        );
        //TODO: login 후에 화면 어디로 갈지 api 설정 필요
        http.logout(l -> l.logoutSuccessUrl("/"));
        return http.build();
    }
}
