package com.hackathon.melon.global.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * 테스트용 홈 컨트롤러
 */
@RestController
public class HomeController {

    @GetMapping("/")
    public Map<String, Object> home(@AuthenticationPrincipal OAuth2User principal) {
        Map<String, Object> response = new HashMap<>();

        if (principal == null) {
            response.put("status", "unauthorized");
            response.put("message", "로그인이 필요합니다.");
            response.put("loginUrl", "/oauth2/authorization/github");
        } else {
            response.put("status", "success");
            response.put("message", "로그인 성공!");
            response.put("user", Map.of(
                "githubId", principal.getAttribute("id"),
                "login", principal.getAttribute("login"),
                "avatarUrl", principal.getAttribute("avatar_url"),
                "profileUrl", principal.getAttribute("html_url")
            ));
            response.put("apis", Map.of(
                "currentUser", "/api/users/me",
                "logout", "/logout"
            ));
        }

        return response;
    }
}