package com.hackathon.melon.domain.project.dto;

import com.hackathon.melon.domain.project.entity.ServiceType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 프로젝트 서비스 생성/수정 요청 DTO
 */
public class ProjectServiceRequest {

    /**
     * 프로젝트 서비스 생성 요청 DTO
     */
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Create {
        @NotBlank(message = "서비스 이름은 필수 입력 항목입니다.")
        @Schema(description = "서비스 이름", example = "frontend")
        private String serviceName;

        @NotNull(message = "서비스 타입은 필수 입력 항목입니다.")
        @Schema(description = "서비스 타입", example = "FE")
        private ServiceType serviceType;

        @Schema(description = "GitHub 저장소 URL", example = "https://github.com/user/repo")
        private String githubRepoUrl;

        @Schema(description = "프레임워크 타입", example = "react")
        private String frameworkType;

        @Schema(description = "기본 브랜치", example = "main")
        private String defaultBranch;
    }

    /**
     * 프로젝트 서비스 수정 요청 DTO
     */
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Update {
        @Schema(description = "서비스 이름", example = "frontend")
        private String serviceName;

        @Schema(description = "GitHub 저장소 URL", example = "https://github.com/user/repo")
        private String githubRepoUrl;

        @Schema(description = "프레임워크 타입", example = "react")
        private String frameworkType;

        @Schema(description = "기본 브랜치", example = "main")
        private String defaultBranch;
    }
}
