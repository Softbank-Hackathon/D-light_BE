package com.hackathon.melon.domain.project.service;

import com.hackathon.melon.domain.project.dto.ProjectRequest;
import com.hackathon.melon.domain.project.dto.ProjectResponse;
import com.hackathon.melon.domain.project.entity.Project;
import com.hackathon.melon.domain.project.repository.ProjectRepository;
import com.hackathon.melon.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 프로젝트 서비스
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProjectService {

    private final ProjectRepository projectRepository;

    /**
     * 프로젝트 생성
     *
     * @param user 프로젝트를 생성하는 사용자
     * @param request 프로젝트 생성 요청
     * @return 생성된 프로젝트 정보
     */
    @Transactional
    public ProjectResponse createProject(User user, ProjectRequest request) {
        log.info("프로젝트 생성 요청: userId = {}, name = {}", user.getId(), request.getName());

        Project project = Project.createProject(user, request.getName());
        Project savedProject = projectRepository.save(project);

        log.info("프로젝트 생성 완료: id = {}, name = {}", savedProject.getId(), savedProject.getName());
        return ProjectResponse.from(savedProject);
    }

    /**
     * 프로젝트 조회
     *
     * @param projectId 프로젝트 ID
     * @return 프로젝트 정보
     */
    public ProjectResponse getProject(Long projectId) {
        log.info("프로젝트 조회 요청: projectId = {}", projectId);

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> {
                    log.warn("프로젝트를 찾을 수 없음: projectId = {}", projectId);
                    return new IllegalArgumentException("프로젝트를 찾을 수 없습니다. ID: " + projectId);
                });

        return ProjectResponse.from(project);
    }
}
