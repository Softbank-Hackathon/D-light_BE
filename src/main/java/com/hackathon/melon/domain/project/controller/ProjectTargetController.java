package com.hackathon.melon.domain.project.controller;

import com.hackathon.melon.domain.project.dto.ProjectTargetRequestDto;
import com.hackathon.melon.domain.project.dto.ProjectTargetResponseDto;
import com.hackathon.melon.domain.project.entity.ProjectTarget;
import com.hackathon.melon.domain.project.service.ProjectTargetService;
import com.hackathon.melon.global.common.CustomApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/project-targets")
@RequiredArgsConstructor
public class ProjectTargetController {

    private final ProjectTargetService projectTargetService;

    @PostMapping
    public ResponseEntity<CustomApiResponse<ProjectTargetResponseDto>> createProjectTarget(
            @RequestBody ProjectTargetRequestDto requestDto) {
        ProjectTarget createdTarget = projectTargetService.createProjectTarget(requestDto);
        ProjectTargetResponseDto responseDto = ProjectTargetResponseDto.from(createdTarget);
        return ResponseEntity.ok(CustomApiResponse.onSuccess(responseDto));
    }

    @GetMapping("/diagnostic-all")
    public ResponseEntity<CustomApiResponse<List<ProjectTargetResponseDto>>> getAllProjectTargets() {
        List<ProjectTargetResponseDto> allTargets = projectTargetService.findAll().stream()
                .map(ProjectTargetResponseDto::from)
                .collect(Collectors.toList());
        return ResponseEntity.ok(CustomApiResponse.onSuccess(allTargets));
    }
}
