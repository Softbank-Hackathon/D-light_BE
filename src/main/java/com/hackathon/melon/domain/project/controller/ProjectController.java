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
            @AuthenticationPrincipal User user,
            @Valid @RequestBody ProjectRequest request) {

        if (user == null) {
            log.warn("인증되지 않은 사용자의 프로젝트 생성 시도");
            return ResponseEntity.status(401).body(CustomApiResponse.onFailure("인증되지 않은 사용자입니다.", null));
        }

        log.info("프로젝트 생성 API 호출: userId = {}, name = {}", user.getId(), request.getProjectName());
        Project createdProject = projectService.createProject(user, request);
        return ResponseEntity.ok(CustomApiResponse.onSuccess(ProjectResponse.from(createdProject)));
    }

    @Operation(summary = "모든 프로젝트 조회", description = "인증된 사용자의 모든 프로젝트를 조회합니다.")
    @GetMapping
    public ResponseEntity<CustomApiResponse<List<ProjectResponse>>> getProjects(@AuthenticationPrincipal User user) {
        if (user == null) {
            log.warn("인증되지 않은 사용자의 프로젝트 목록 조회 시도");
            return ResponseEntity.status(401).body(CustomApiResponse.onFailure("인증되지 않은 사용자입니다.", null));
        }
        List<ProjectResponse> projects = projectService.getProjects(user.getId());
        return ResponseEntity.ok(CustomApiResponse.onSuccess(projects));
    }
}