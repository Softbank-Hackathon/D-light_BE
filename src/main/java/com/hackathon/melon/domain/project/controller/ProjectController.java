package com.hackathon.melon.domain.project.controller;

import com.hackathon.melon.domain.project.dto.ProjectRequest;
import com.hackathon.melon.domain.project.dto.ProjectResponse;
import com.hackathon.melon.domain.project.entity.Project;
import com.hackathon.melon.domain.project.service.ProjectService;
import com.hackathon.melon.domain.user.entity.User;
import com.hackathon.melon.domain.user.repository.UserRepository;
import com.hackathon.melon.global.common.CustomApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/projects")
@RequiredArgsConstructor
@Tag(name = "Project", description = "프로젝트 관리 API")
public class ProjectController {

    private final ProjectService projectService;
    private final UserRepository userRepository;

    @Operation(summary = "프로젝트 생성", description = "새로운 프로젝트를 생성합니다.")
    @PostMapping
    public ResponseEntity<CustomApiResponse<ProjectResponse>> createProject(
            @AuthenticationPrincipal OAuth2User oauth2User,
            @Valid @RequestBody ProjectRequest request) {

        if (oauth2User == null) {
            log.warn("인증되지 않은 사용자의 프로젝트 생성 시도");
            return ResponseEntity.status(401).body(CustomApiResponse.onFailure("인증되지 않은 사용자입니다.", null));
        }

        // OAuth2User에서 githubId 추출
        Long githubId = ((Number) oauth2User.getAttributes().get("id")).longValue();

        // githubId로 User 조회
        User user = userRepository.findByGithubId(githubId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        log.info("프로젝트 생성 API 호출: userId = {}, name = {}", user.getId(), request.getProjectName());
        Project createdProject = projectService.createProject(user, request);
        return ResponseEntity.ok(CustomApiResponse.onSuccess(ProjectResponse.from(createdProject)));
    }

    @Operation(summary = "모든 프로젝트 조회", description = "인증된 사용자의 모든 프로젝트를 조회합니다.")
    @GetMapping
    public ResponseEntity<CustomApiResponse<List<ProjectResponse>>> getProjects(@AuthenticationPrincipal OAuth2User oauth2User) {
        if (oauth2User == null) {
            log.warn("인증되지 않은 사용자의 프로젝트 목록 조회 시도");
            return ResponseEntity.status(401).body(CustomApiResponse.onFailure("인증되지 않은 사용자입니다.", null));
        }

        // OAuth2User에서 githubId 추출
        Long githubId = ((Number) oauth2User.getAttributes().get("id")).longValue();

        // githubId로 User 조회
        User user = userRepository.findByGithubId(githubId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        List<ProjectResponse> projects = projectService.getProjects(user.getId());
        return ResponseEntity.ok(CustomApiResponse.onSuccess(projects));
    }
}