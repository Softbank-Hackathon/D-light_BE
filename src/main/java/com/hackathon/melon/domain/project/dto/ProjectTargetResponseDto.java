package com.hackathon.melon.domain.project.dto;

import com.hackathon.melon.domain.project.entity.ProjectTarget;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class ProjectTargetResponseDto {
    private Long id;
    private Long projectId;
    private String env;
    private String roleArn;
    private String region;
    private boolean isDefault;

    public static ProjectTargetResponseDto from(ProjectTarget projectTarget) {
        return ProjectTargetResponseDto.builder()
                .id(projectTarget.getId())
                .projectId(projectTarget.getProject().getId())
                .env(projectTarget.getEnv() != null ? projectTarget.getEnv().name() : null)
                .roleArn(projectTarget.getRoleArn())
                .region(projectTarget.getRegion())
                .isDefault(projectTarget.isDefault())
                .build();
    }
}
