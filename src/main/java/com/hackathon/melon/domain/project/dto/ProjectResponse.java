package com.hackathon.melon.domain.project.dto;

import com.hackathon.melon.domain.project.entity.Project;
import com.hackathon.melon.domain.project.entity.ServiceType;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ProjectResponse {
    private Long id;
    private String projectName;
    private ServiceType serviceType;
    private String githubRepoUrl;
    private String frameworkType;
    private String defaultBranch;
    private boolean isActive;

    public static ProjectResponse from(Project project) {
        return ProjectResponse.builder()
                .id(project.getId())
                .projectName(project.getProjectName())
                .serviceType(project.getServiceType())
                .githubRepoUrl(project.getGithubRepoUrl())
                .frameworkType(project.getFrameworkType())
                .defaultBranch(project.getDefaultBranch())
                .isActive(project.isActive())
                .build();
    }
}