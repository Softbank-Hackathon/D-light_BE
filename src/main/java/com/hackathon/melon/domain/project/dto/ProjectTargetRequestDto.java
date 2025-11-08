package com.hackathon.melon.domain.project.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.hackathon.melon.domain.project.entity.EnvType;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ProjectTargetRequestDto {
    private Long projectId;
    private EnvType env;
    private String roleArn;
    private String externalId;
    private String region;
    private Integer sessionDurationSecs;

    @JsonAlias("default")
    private boolean isDefault;
}
