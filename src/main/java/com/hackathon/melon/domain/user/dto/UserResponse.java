package com.hackathon.melon.domain.user.dto;

import com.hackathon.melon.domain.user.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 사용자 정보 응답 DTO
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {
    private Long id;
    private Long githubId;
    private String login;
    private String profileUrl;
    private String email;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    /**
     * User 엔티티를 UserResponse DTO로 변환
     */
    public static UserResponse from(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .githubId(user.getGithubId())
                .login(user.getLogin())
                .profileUrl(user.getProfileUrl())
                .email(user.getEmail())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }
}