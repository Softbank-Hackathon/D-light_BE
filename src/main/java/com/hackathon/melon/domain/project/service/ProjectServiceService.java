package com.hackathon.melon.domain.project.service;

import com.hackathon.melon.domain.project.dto.ProjectServiceRequest;
import com.hackathon.melon.domain.project.dto.ProjectServiceResponse;
import com.hackathon.melon.domain.project.entity.Project;
import com.hackathon.melon.domain.project.entity.ProjectService;
import com.hackathon.melon.domain.project.entity.ServiceType;
import com.hackathon.melon.domain.project.repository.ProjectRepository;
import com.hackathon.melon.domain.project.repository.ProjectServiceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 프로젝트 서비스 관리 서비스
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProjectServiceService {

    private final ProjectServiceRepository projectServiceRepository;
    private final ProjectRepository projectRepository;

    /**
     * 프로젝트 서비스 생성
     *
     * @param projectId 프로젝트 ID
     * @param request 서비스 생성 요청
     * @return 생성된 서비스 정보
     */
    @Transactional
    public ProjectServiceResponse createProjectService(Long projectId, ProjectServiceRequest.Create request) {
        log.info("프로젝트 서비스 생성 요청: projectId = {}, serviceName = {}", projectId, request.getServiceName());

        // 프로젝트 존재 확인
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> {
                    log.warn("프로젝트를 찾을 수 없음: projectId = {}", projectId);
                    return new IllegalArgumentException("프로젝트를 찾을 수 없습니다. ID: " + projectId);
                });

        // 중복 서비스 이름 확인
        if (projectServiceRepository.existsByProjectIdAndServiceName(projectId, request.getServiceName())) {
            log.warn("중복된 서비스 이름: projectId = {}, serviceName = {}", projectId, request.getServiceName());
            throw new IllegalArgumentException("이미 존재하는 서비스 이름입니다: " + request.getServiceName());
        }

        // 서비스 생성
        ProjectService projectService = ProjectService.createProjectService(
                project,
                request.getServiceName(),
                request.getServiceType(),
                request.getGithubRepoUrl(),
                request.getFrameworkType(),
                request.getDefaultBranch()
        );

        ProjectService savedService = projectServiceRepository.save(projectService);
        log.info("프로젝트 서비스 생성 완료: id = {}, serviceName = {}", savedService.getId(), savedService.getServiceName());

        return ProjectServiceResponse.from(savedService);
    }

    /**
     * 프로젝트의 모든 서비스 조회
     *
     * @param projectId 프로젝트 ID
     * @return 서비스 목록
     */
    public List<ProjectServiceResponse> getProjectServices(Long projectId) {
        log.info("프로젝트 서비스 목록 조회 요청: projectId = {}", projectId);

        // 프로젝트 존재 확인
        if (!projectRepository.existsById(projectId)) {
            log.warn("프로젝트를 찾을 수 없음: projectId = {}", projectId);
            throw new IllegalArgumentException("프로젝트를 찾을 수 없습니다. ID: " + projectId);
        }

        List<ProjectService> services = projectServiceRepository.findByProjectId(projectId);
        return services.stream()
                .map(ProjectServiceResponse::from)
                .collect(Collectors.toList());
    }

    /**
     * 특정 서비스 조회
     *
     * @param projectId 프로젝트 ID
     * @param serviceId 서비스 ID
     * @return 서비스 정보
     */
    public ProjectServiceResponse getProjectService(Long projectId, Long serviceId) {
        log.info("프로젝트 서비스 조회 요청: projectId = {}, serviceId = {}", projectId, serviceId);

        ProjectService projectService = projectServiceRepository.findById(serviceId)
                .orElseThrow(() -> {
                    log.warn("서비스를 찾을 수 없음: serviceId = {}", serviceId);
                    return new IllegalArgumentException("서비스를 찾을 수 없습니다. ID: " + serviceId);
                });

        // 프로젝트 일치 확인
        if (!projectService.getProject().getId().equals(projectId)) {
            log.warn("프로젝트 불일치: projectId = {}, service.projectId = {}", projectId, projectService.getProject().getId());
            throw new IllegalArgumentException("해당 프로젝트의 서비스가 아닙니다.");
        }

        return ProjectServiceResponse.from(projectService);
    }

    /**
     * 서비스 타입별 조회
     *
     * @param projectId 프로젝트 ID
     * @param serviceType 서비스 타입
     * @return 서비스 목록
     */
    public List<ProjectServiceResponse> getProjectServicesByType(Long projectId, ServiceType serviceType) {
        log.info("서비스 타입별 조회 요청: projectId = {}, serviceType = {}", projectId, serviceType);

        List<ProjectService> services = projectServiceRepository.findByProjectIdAndServiceType(projectId, serviceType);
        return services.stream()
                .map(ProjectServiceResponse::from)
                .collect(Collectors.toList());
    }

    /**
     * 프로젝트 서비스 수정
     *
     * @param projectId 프로젝트 ID
     * @param serviceId 서비스 ID
     * @param request 서비스 수정 요청
     * @return 수정된 서비스 정보
     */
    @Transactional
    public ProjectServiceResponse updateProjectService(Long projectId, Long serviceId, ProjectServiceRequest.Update request) {
        log.info("프로젝트 서비스 수정 요청: projectId = {}, serviceId = {}", projectId, serviceId);

        ProjectService projectService = projectServiceRepository.findById(serviceId)
                .orElseThrow(() -> {
                    log.warn("서비스를 찾을 수 없음: serviceId = {}", serviceId);
                    return new IllegalArgumentException("서비스를 찾을 수 없습니다. ID: " + serviceId);
                });

        // 프로젝트 일치 확인
        if (!projectService.getProject().getId().equals(projectId)) {
            log.warn("프로젝트 불일치: projectId = {}, service.projectId = {}", projectId, projectService.getProject().getId());
            throw new IllegalArgumentException("해당 프로젝트의 서비스가 아닙니다.");
        }

        // 서비스 이름 중복 확인 (변경하는 경우)
        if (request.getServiceName() != null && !request.getServiceName().equals(projectService.getServiceName())) {
            if (projectServiceRepository.existsByProjectIdAndServiceName(projectId, request.getServiceName())) {
                log.warn("중복된 서비스 이름: projectId = {}, serviceName = {}", projectId, request.getServiceName());
                throw new IllegalArgumentException("이미 존재하는 서비스 이름입니다: " + request.getServiceName());
            }
        }

        // 서비스 정보 업데이트
        projectService.updateService(
                request.getServiceName(),
                request.getGithubRepoUrl(),
                request.getFrameworkType(),
                request.getDefaultBranch()
        );

        log.info("프로젝트 서비스 수정 완료: id = {}, serviceName = {}", projectService.getId(), projectService.getServiceName());
        return ProjectServiceResponse.from(projectService);
    }

    /**
     * 프로젝트 서비스 삭제 (비활성화)
     *
     * @param projectId 프로젝트 ID
     * @param serviceId 서비스 ID
     */
    @Transactional
    public void deleteProjectService(Long projectId, Long serviceId) {
        log.info("프로젝트 서비스 삭제 요청: projectId = {}, serviceId = {}", projectId, serviceId);

        ProjectService projectService = projectServiceRepository.findById(serviceId)
                .orElseThrow(() -> {
                    log.warn("서비스를 찾을 수 없음: serviceId = {}", serviceId);
                    return new IllegalArgumentException("서비스를 찾을 수 없습니다. ID: " + serviceId);
                });

        // 프로젝트 일치 확인
        if (!projectService.getProject().getId().equals(projectId)) {
            log.warn("프로젝트 불일치: projectId = {}, service.projectId = {}", projectId, projectService.getProject().getId());
            throw new IllegalArgumentException("해당 프로젝트의 서비스가 아닙니다.");
        }

        // 소프트 삭제 (비활성화)
        projectService.deactivate();
        log.info("프로젝트 서비스 삭제 완료: id = {}", serviceId);
    }

    /**
     * 프로젝트 서비스 활성화
     *
     * @param projectId 프로젝트 ID
     * @param serviceId 서비스 ID
     */
    @Transactional
    public void activateProjectService(Long projectId, Long serviceId) {
        log.info("프로젝트 서비스 활성화 요청: projectId = {}, serviceId = {}", projectId, serviceId);

        ProjectService projectService = projectServiceRepository.findById(serviceId)
                .orElseThrow(() -> {
                    log.warn("서비스를 찾을 수 없음: serviceId = {}", serviceId);
                    return new IllegalArgumentException("서비스를 찾을 수 없습니다. ID: " + serviceId);
                });

        // 프로젝트 일치 확인
        if (!projectService.getProject().getId().equals(projectId)) {
            log.warn("프로젝트 불일치: projectId = {}, service.projectId = {}", projectId, projectService.getProject().getId());
            throw new IllegalArgumentException("해당 프로젝트의 서비스가 아닙니다.");
        }

        projectService.activate();
        log.info("프로젝트 서비스 활성화 완료: id = {}", serviceId);
    }
}
