package com.hackathon.melon.domain.deployment.controller;

import com.hackathon.melon.domain.deployment.dto.request.DeploymentRequestDto;
import com.hackathon.melon.domain.deployment.dto.request.FrontendDeploymentRequestDto;
import com.hackathon.melon.domain.deployment.service.DeploymentService;
import com.hackathon.melon.global.common.CustomApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
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
    /*
    1. 프론트만 배포를 원하는 경우
        - 프론트엔드 깃허브 url과 프론트 env만 받고 S3 버킷에 빌드 파일 업로드 후 배포(우리 서버에서 빌드) ex) React면 npm run build 후 빌드 파일 S3에 업로드
    2. 백엔드만 배포를 원하는 경우
        - 백엔드 깃허브 url만 받고 백엔드 env만 받고 EC2에 코드 복제 후 사용자의 EC2 안에서 각 프레임워크에 맞게 스크립트로 배포 ex) Spring Boot면
        ec2에 jdk 17 설치 후 gradle build, java -jar 로 실행
    3. 프론트 + 백엔드 배포를 원하는 경우
        - 각각의 url과 env를 받고 먼저 백엔드를 배포한 프론트의 env에 백엔드 IP를 넣어준 후 프론트를 배포


    이 외에 디벨롭 옵션들
    - aws, region, iam role, external id 로그인 + s3나 ec2 생성을 외부 큐나 큐로 처리해 get방식으로 배포 상태 확인 가능하게 (웹소켓 까지는 아니더라도 됨)
    - 배포 로그 확인 가능하게 (배포 상태 확인 api와 통합 가능)
    - 배포 취소 기능 (큐에 들어가있을 때만 가능)
    - 배포 내역 확인 기능 (배포 시간, 상태, 로그 등)
    - 배포 스케줄링 기능 (특정 시간에 배포 예약)
    - 배포 알림 기능 (배포 완료 시 이메일이나 슬랙 등으로 알림)
    - 배포 롤백 기능 (이전 버전으로 쉽게 롤백)
    - 멀티 리전 배포 기능 (여러 AWS 리전에 동시에 배포)
     */
    @Operation(summary = "자동화 배포", description = "프로젝트를 자동으로 배포합니다.")
    @PostMapping("/deployment-project")
    public ResponseEntity<CustomApiResponse<String>> deployProject(
            @Valid @RequestBody DeploymentRequestDto deploymentRequestDto) {
        try {
            deploymentService.deployProject(deploymentRequestDto);
            return ResponseEntity.ok(CustomApiResponse.onSuccess("프로젝트 배포가 시작되었습니다."));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError()
                    .body(CustomApiResponse.onFailure("프로젝트 배포에 실패했습니다: " , e.getMessage()));
        }
    }
    @Operation(summary = "프론트만 자동화 배포", description = "프론트엔드 프로젝트를 자동으로 배포합니다.")
    @PostMapping("deploy-frontend")
    public ResponseEntity<CustomApiResponse<String>> deployFrontend(
            @Valid @RequestBody FrontendDeploymentRequestDto frontendDeploymentRequestDto) {
        try {
            String url = deploymentService.deployFrontend(frontendDeploymentRequestDto);
            return ResponseEntity.ok(CustomApiResponse.onSuccess(url));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError()
                    .body(CustomApiResponse.onFailure("프론트엔드 프로젝트 배포에 실패했습니다: " , e.getMessage()));
        }
    }



}
