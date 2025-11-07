package com.hackathon.melon.domain.project.dto;

import com.hackathon.melon.domain.project.entity.Project;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 프로젝트 응답 DTO
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectResponse {

    private Long id;
    private Long userId;
    private String name;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    /**
     * Project 엔티티를 ProjectResponse DTO로 변환
     */
    public static ProjectResponse from(Project project) {
        return ProjectResponse.builder()
                .id(project.getId())
                .userId(project.getUser().getId())
                .name(project.getName())
                .isActive(project.getIsActive())
                .createdAt(project.getCreatedAt())
                .updatedAt(project.getUpdatedAt())
                .build();
    }
}