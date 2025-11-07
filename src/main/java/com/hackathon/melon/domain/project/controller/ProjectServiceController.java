package com.hackathon.melon.domain.project.controller;

import com.hackathon.melon.domain.project.dto.ProjectServiceRequest;
import com.hackathon.melon.domain.project.dto.ProjectServiceResponse;
import com.hackathon.melon.domain.project.entity.ServiceType;
import com.hackathon.melon.domain.project.service.ProjectServiceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 프로젝트 서비스 API 컨트롤러
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/projects/{projectId}/services")
@RequiredArgsConstructor
@Tag(name = "Project Service", description = "프로젝트 서비스 관리 API")
public class ProjectServiceController {

    private final ProjectServiceService projectServiceService;

    /**
     * 프로젝트 서비스 생성
     *
     * @param projectId 프로젝트 ID
     * @param request 서비스 생성 요청
     * @return 생성된 서비스 정보
     */
    @Operation(summary = "프로젝트 서비스 생성", description = "프로젝트에 새로운 서비스(FE/BE)를 추가합니다.")
    @PostMapping
    public ResponseEntity<ProjectServiceResponse> createProjectService(
            @PathVariable Long projectId,
            @Valid @RequestBody ProjectServiceRequest.Create request) {

        log.info("프로젝트 서비스 생성 API 호출: projectId = {}, serviceName = {}", projectId, request.getServiceName());

        try {
            ProjectServiceResponse response = projectServiceService.createProjectService(projectId, request);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            log.warn("프로젝트 서비스 생성 실패: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("프로젝트 서비스 생성 실패", e);
            return ResponseEntity.status(500).build();
        }
    }

    /**
     * 프로젝트의 모든 서비스 조회
     *
     * @param projectId 프로젝트 ID
     * @return 서비스 목록
     */
    @Operation(summary = "프로젝트 서비스 목록 조회", description = "프로젝트의 모든 서비스를 조회합니다.")
    @GetMapping
    public ResponseEntity<List<ProjectServiceResponse>> getProjectServices(@PathVariable Long projectId) {

        log.info("프로젝트 서비스 목록 조회 API 호출: projectId = {}", projectId);

        try {
            List<ProjectServiceResponse> response = projectServiceService.getProjectServices(projectId);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            log.warn("프로젝트를 찾을 수 없음: projectId = {}", projectId);
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error("프로젝트 서비스 목록 조회 실패: projectId = {}", projectId, e);
            return ResponseEntity.status(500).build();
        }
    }

    /**
     * 특정 서비스 조회
     *
     * @param projectId 프로젝트 ID
     * @param serviceId 서비스 ID
     * @return 서비스 정보
     */
    @Operation(summary = "프로젝트 서비스 조회", description = "특정 서비스의 상세 정보를 조회합니다.")
    @GetMapping("/{serviceId}")
    public ResponseEntity<ProjectServiceResponse> getProjectService(
            @PathVariable Long projectId,
            @PathVariable Long serviceId) {

        log.info("프로젝트 서비스 조회 API 호출: projectId = {}, serviceId = {}", projectId, serviceId);

        try {
            ProjectServiceResponse response = projectServiceService.getProjectService(projectId, serviceId);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            log.warn("서비스를 찾을 수 없음: projectId = {}, serviceId = {}", projectId, serviceId);
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error("프로젝트 서비스 조회 실패: projectId = {}, serviceId = {}", projectId, serviceId, e);
            return ResponseEntity.status(500).build();
        }
    }

    /**
     * 서비스 타입별 조회
     *
     * @param projectId 프로젝트 ID
     * @param serviceType 서비스 타입 (FE, BE)
     * @return 서비스 목록
     */
    @Operation(summary = "서비스 타입별 조회", description = "특정 타입(FE/BE)의 서비스만 조회합니다.")
    @GetMapping("/type/{serviceType}")
    public ResponseEntity<List<ProjectServiceResponse>> getProjectServicesByType(
            @PathVariable Long projectId,
            @PathVariable ServiceType serviceType) {

        log.info("서비스 타입별 조회 API 호출: projectId = {}, serviceType = {}", projectId, serviceType);

        try {
            List<ProjectServiceResponse> response = projectServiceService.getProjectServicesByType(projectId, serviceType);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("서비스 타입별 조회 실패: projectId = {}, serviceType = {}", projectId, serviceType, e);
            return ResponseEntity.status(500).build();
        }
    }

    /**
     * 프로젝트 서비스 수정
     *
     * @param projectId 프로젝트 ID
     * @param serviceId 서비스 ID
     * @param request 서비스 수정 요청
     * @return 수정된 서비스 정보
     */
    @Operation(summary = "프로젝트 서비스 수정", description = "서비스 정보를 수정합니다.")
    @PutMapping("/{serviceId}")
    public ResponseEntity<ProjectServiceResponse> updateProjectService(
            @PathVariable Long projectId,
            @PathVariable Long serviceId,
            @Valid @RequestBody ProjectServiceRequest.Update request) {

        log.info("프로젝트 서비스 수정 API 호출: projectId = {}, serviceId = {}", projectId, serviceId);

        try {
            ProjectServiceResponse response = projectServiceService.updateProjectService(projectId, serviceId, request);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            log.warn("프로젝트 서비스 수정 실패: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("프로젝트 서비스 수정 실패: projectId = {}, serviceId = {}", projectId, serviceId, e);
            return ResponseEntity.status(500).build();
        }
    }

    /**
     * 프로젝트 서비스 삭제 (비활성화)
     *
     * @param projectId 프로젝트 ID
     * @param serviceId 서비스 ID
     * @return 삭제 결과
     */
    @Operation(summary = "프로젝트 서비스 삭제", description = "서비스를 비활성화합니다 (소프트 삭제).")
    @DeleteMapping("/{serviceId}")
    public ResponseEntity<Void> deleteProjectService(
            @PathVariable Long projectId,
            @PathVariable Long serviceId) {

        log.info("프로젝트 서비스 삭제 API 호출: projectId = {}, serviceId = {}", projectId, serviceId);

        try {
            projectServiceService.deleteProjectService(projectId, serviceId);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            log.warn("프로젝트 서비스 삭제 실패: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error("프로젝트 서비스 삭제 실패: projectId = {}, serviceId = {}", projectId, serviceId, e);
            return ResponseEntity.status(500).build();
        }
    }

    /**
     * 프로젝트 서비스 활성화
     *
     * @param projectId 프로젝트 ID
     * @param serviceId 서비스 ID
     * @return 활성화 결과
     */
    @Operation(summary = "프로젝트 서비스 활성화", description = "비활성화된 서비스를 다시 활성화합니다.")
    @PatchMapping("/{serviceId}/activate")
    public ResponseEntity<Void> activateProjectService(
            @PathVariable Long projectId,
            @PathVariable Long serviceId) {

        log.info("프로젝트 서비스 활성화 API 호출: projectId = {}, serviceId = {}", projectId, serviceId);

        try {
            projectServiceService.activateProjectService(projectId, serviceId);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            log.warn("프로젝트 서비스 활성화 실패: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error("프로젝트 서비스 활성화 실패: projectId = {}, serviceId = {}", projectId, serviceId, e);
            return ResponseEntity.status(500).build();
        }
    }
}
