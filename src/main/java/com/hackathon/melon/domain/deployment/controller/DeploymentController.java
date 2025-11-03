package com.hackathon.melon.domain.deployment.controller;

import com.hackathon.melon.domain.deployment.dto.request.DeploymentRequestDto;
import com.hackathon.melon.domain.deployment.service.DeploymentService;
import com.hackathon.melon.global.common.CustomApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Deployment", description = "Deployment API")
@RestController
@RequestMapping("/api/v1/deployments")
@RequiredArgsConstructor

public class DeploymentController {

    private final DeploymentService deploymentService;

    @Operation(summary = "자동화 배포", description = "프로젝트를 자동으로 배포합니다.")
    @PostMapping("/deployment-project")
    public ResponseEntity<CustomApiResponse<String>> deployProject(
            @RequestBody DeploymentRequestDto deploymentRequestDto) {
        try {
            deploymentService.deployProject(deploymentRequestDto);
            return ResponseEntity.ok(CustomApiResponse.onSuccess("프로젝트 배포가 시작되었습니다."));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError()
                    .body(CustomApiResponse.onFailure("프로젝트 배포에 실패했습니다: " , e.getMessage()));
        }
    }


}
