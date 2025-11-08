package com.hackathon.melon.domain.project.dto;

import com.hackathon.melon.domain.project.entity.ServiceType;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ProjectRequest {
    private String projectName;
    private ServiceType serviceType;
    private String githubRepoUrl;
    private String frameworkType;
    private String defaultBranch;
}