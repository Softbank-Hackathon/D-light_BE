package com.hackathon.melon.domain.project.controller;

import com.hackathon.melon.domain.project.dto.ProjectRequest;
import com.hackathon.melon.domain.project.dto.ProjectResponse;
import com.hackathon.melon.domain.project.service.ProjectService;
import com.hackathon.melon.domain.user.entity.User;
import com.hackathon.melon.domain.user.repository.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

/**
 * 프로젝트 API 컨트롤러
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/projects")
@RequiredArgsConstructor
@Tag(name = "Project", description = "프로젝트 관리 API")
public class ProjectController {

    private final ProjectService projectService;
    private final UserRepository userRepository;

    /**
     * 프로젝트 생성
     *
     * @param principal OAuth2 인증 정보
     * @param request 프로젝트 생성 요청
     * @return 생성된 프로젝트 정보
     */
    @Operation(summary = "프로젝트 생성", description = "새로운 프로젝트를 생성합니다.")
    @PostMapping
    public ResponseEntity<ProjectResponse> createProject(
            @AuthenticationPrincipal OAuth2User principal,
            @Valid @RequestBody ProjectRequest request) {

        if (principal == null) {
            log.warn("인증되지 않은 사용자의 프로젝트 생성 시도");
            return ResponseEntity.status(401).build();
        }

        Long githubId = ((Number) principal.getAttribute("id")).longValue();
        log.info("프로젝트 생성 API 호출: githubId = {}, name = {}", githubId, request.getName());

        try {
            User user = userRepository.findByGithubId(githubId)
                    .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

            ProjectResponse response = projectService.createProject(user, request);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            log.error("사용자를 찾을 수 없음: githubId = {}", githubId);
            return ResponseEntity.status(404).build();
        } catch (Exception e) {
            log.error("프로젝트 생성 실패", e);
            return ResponseEntity.status(500).build();
        }
    }

    /**
     * 프로젝트 조회
     *
     * @param projectId 프로젝트 ID
     * @return 프로젝트 정보
     */
    @Operation(summary = "프로젝트 조회", description = "프로젝트 ID로 프로젝트 정보를 조회합니다.")
    @GetMapping("/{projectId}")
    public ResponseEntity<ProjectResponse> getProject(@PathVariable Long projectId) {

        log.info("프로젝트 조회 API 호출: projectId = {}", projectId);

        try {
            ProjectResponse response = projectService.getProject(projectId);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            log.warn("프로젝트를 찾을 수 없음: projectId = {}", projectId);
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error("프로젝트 조회 실패: projectId = {}", projectId, e);
            return ResponseEntity.status(500).build();
        }
    }
}