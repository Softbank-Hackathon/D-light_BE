package com.hackathon.melon.domain.deployment.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Map;


@Getter
@NoArgsConstructor
public class FrontendDeploymentRequestDto { // 프론트엔드 배포 전용 DTO

    // project_service 에서 고유 serviceId 받아서 로드하면 project service
    @NotNull(message = "서비스 ID는 필수 입력 항목입니다.")
    @Schema(description = "배포할 서비스의 ID", example = "1")
    private Long serviceId;

    @Schema(description="빌드 시 주입할 환경변수")
    private Map<String, String> env;

    @NotBlank(message = "AWS 리전은 필수 입력 항목입니다.")
    @Schema(description = "배포할 AWS 리전", example = "ap-northeast-2")
    private String region;

    @NotBlank(message = "IAM Role ARN은 필수 입력 항목입니다.")
    @Schema(description = "AWS IAM Role ARN", example = "arn:aws:iam::123456789012:role/autodeploy-12345")
    private String roleArn;

    @NotBlank(message = "External ID는 필수 입력 항목입니다.")
    @Schema(description = "AWS External ID", example = "autodeploy-123")
    private String externalId;
}
