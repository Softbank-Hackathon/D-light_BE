package com.hackathon.melon.domain.auth.handler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;

/**
 * OAuth2 로그인 실패 시 처리하는 핸들러
 * 프론트엔드 에러 페이지로 리다이렉트하며 에러 정보를 전달
 */
@Slf4j
@Component
public class OAuth2AuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {

    @Value("${app.oauth2.redirect-uri:http://localhost:3000}")
    private String frontendUrl;

    @Override
    public void onAuthenticationFailure(HttpServletRequest request,
                                       HttpServletResponse response,
                                       AuthenticationException exception) throws IOException, ServletException {

        String targetUrl = determineTargetUrl(exception);

        if (response.isCommitted()) {
            log.debug("Response has already been committed. Unable to redirect to " + targetUrl);
            return;
        }

        log.error("OAuth2 login failed: {}", exception.getMessage());
        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }

    private String determineTargetUrl(AuthenticationException exception) {
        // 프론트엔드 에러 페이지로 리다이렉트
        // 에러 정보를 쿼리 파라미터로 전달
        return UriComponentsBuilder.fromUriString(frontendUrl + "/login")
                .queryParam("error", "true")
                .queryParam("message", exception.getLocalizedMessage())
                .build()
                .encode()  // URL 인코딩 추가
                .toUriString();
    }
}