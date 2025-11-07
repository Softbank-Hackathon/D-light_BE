package com.hackathon.melon.domain.project.dto;

import com.hackathon.melon.domain.project.entity.ProjectService;
import com.hackathon.melon.domain.project.entity.ServiceType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 프로젝트 서비스 응답 DTO
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectServiceResponse {

    private Long id;
    private Long projectId;
    private String serviceName;
    private ServiceType serviceType;
    private String githubRepoUrl;
    private String frameworkType;
    private String defaultBranch;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    /**
     * ProjectService 엔티티를 ProjectServiceResponse DTO로 변환
     */
    public static ProjectServiceResponse from(ProjectService projectService) {
        return ProjectServiceResponse.builder()
                .id(projectService.getId())
                .projectId(projectService.getProject().getId())
                .serviceName(projectService.getServiceName())
                .serviceType(projectService.getServiceType())
                .githubRepoUrl(projectService.getGithubRepoUrl())
                .frameworkType(projectService.getFrameworkType())
                .defaultBranch(projectService.getDefaultBranch())
                .isActive(projectService.getIsActive())
                .createdAt(projectService.getCreatedAt())
                .updatedAt(projectService.getUpdatedAt())
                .build();
    }
}
