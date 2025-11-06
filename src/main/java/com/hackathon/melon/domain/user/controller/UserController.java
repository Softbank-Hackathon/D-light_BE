package com.hackathon.melon.domain.user.controller;

import com.hackathon.melon.domain.user.dto.UserResponse;
import com.hackathon.melon.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 사용자 관련 API 컨트롤러
 */
@Slf4j
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * 현재 로그인한 사용자 정보 조회
     *
     * @param principal OAuth2 인증 정보
     * @return 현재 사용자 정보
     */
    @GetMapping("/me")
    public ResponseEntity<UserResponse> getCurrentUser(
            @AuthenticationPrincipal OAuth2User principal) {

        if (principal == null) {
            log.warn("인증되지 않은 사용자의 /me 접근 시도");
            return ResponseEntity.status(401).build();
        }

        // OAuth2User의 attributes에서 GitHub ID 추출
        Long githubId = ((Number) principal.getAttribute("id")).longValue();
        log.info("현재 사용자 정보 조회 요청: GitHub ID = {}", githubId);

        UserResponse user = userService.getCurrentUser(githubId);
        return ResponseEntity.ok(user);
    }

    /**
     * 사용자 ID로 사용자 정보 조회
     *
     * @param userId 사용자 ID
     * @return 사용자 정보
     */
    @GetMapping("/{userId}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable Long userId) {
        log.info("사용자 정보 조회 요청: userId = {}", userId);

        try {
            UserResponse user = userService.getUserById(userId);
            return ResponseEntity.ok(user);
        } catch (IllegalArgumentException e) {
            log.warn("사용자를 찾을 수 없음: userId = {}", userId);
            return ResponseEntity.notFound().build();
        }
    }
}