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
    private Long userId;
    private String env;
    private String roleArn;
    private String externalId;
    private String region;
    private Integer sessionDurationSecs;
    private boolean isDefault;

    public static ProjectTargetResponseDto from(ProjectTarget projectTarget) {
        return ProjectTargetResponseDto.builder()
                .id(projectTarget.getId())
                .userId(projectTarget.getUser().getId())
                .env(projectTarget.getEnv() != null ? projectTarget.getEnv().name() : null)
                .roleArn(projectTarget.getRoleArn())
                .externalId(projectTarget.getExternalId())
                .region(projectTarget.getRegion())
                .sessionDurationSecs(projectTarget.getSessionDurationSecs())
                .isDefault(projectTarget.isDefault())
                .build();
    }
}
