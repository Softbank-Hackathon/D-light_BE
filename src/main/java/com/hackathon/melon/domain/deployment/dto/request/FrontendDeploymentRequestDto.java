package com.hackathon.melon.domain.deployment.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Map;


@Getter
@NoArgsConstructor
public class FrontendDeploymentRequestDto { // 프론트엔드 배포 전용 DTO
    @NotBlank(message = "GitHub 저장소 URL은 필수 입력 항목입니다.")
    @Schema(description = "GitHub 저장소 URL", example = "https://github.com/user/repository")
    private String githubRepositoryUrl;

//    @NotBlank(message = "프레임워크 유형은 필수 입력 항목입니다.") // 상관없이 npm run build 할거라 필요없음
//    @Schema(description = "프레임워크 또는 프로젝트 유형", example = "fronted = Vanila JS, React, Vue.js, Angular, Svelte || backend = springboot, django, nodejs ")
//    private String frameworkType;

    @Schema(description="빌드 시 주입할 환경변수")
    private Map<String, String> env;

    @Schema(description = "깃허브 브랜치 이름", example = "main")
    @NotBlank(message = "깃허브 브랜치 이름은 필수 입력 항목입니다.")
    private String branch;

    @NotBlank(message = "AWS 리전은 필수 입력 항목입니다.")
    @Schema(description = "배포할 AWS 리전", example = "ap-northeast-2")
    private String region;

    @NotBlank(message = "프로젝트 이름은 필수 입력 항목입니다.")
    @Schema(description = "배포할 프로젝트 이름", example = "autodeploy-frontend-demo")
    private String projectName;

    @NotBlank(message = "IAM Role ARN은 필수 입력 항목입니다.")
    @Schema(description = "AWS IAM Role ARN", example = "arn:aws:iam::123456789012:role/autodeploy-12345")
    private String roleArn;

    @NotBlank(message = "External ID는 필수 입력 항목입니다.")
    @Schema(description = "AWS External ID", example = "autodeploy-123")
    private String externalId;
}
