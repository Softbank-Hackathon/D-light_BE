package com.hackathon.melon.domain.deployment.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Map;


@Getter
@NoArgsConstructor
public class FrontendDeploymentRequestDto { // 프론트엔드 배포 전용 DTO

    @NotNull(message = "프로젝트 ID는 필수 입력 항목입니다.")
    @Schema(description = "배포할 프로젝트의 ID", example = "1")
    private Long projectId;

    @Schema(description="빌드 시 주입할 환경변수")
    private Map<String, String> env;
}
