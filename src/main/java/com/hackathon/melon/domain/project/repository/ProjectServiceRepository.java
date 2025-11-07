package com.hackathon.melon.domain.project.repository;

import com.hackathon.melon.domain.project.entity.Project;
import com.hackathon.melon.domain.project.entity.ProjectService;
import com.hackathon.melon.domain.project.entity.ServiceType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 프로젝트 서비스 리포지토리
 */
@Repository
public interface ProjectServiceRepository extends JpaRepository<ProjectService, Long> {

    /**
     * 프로젝트 ID로 모든 서비스 조회
     */
    List<ProjectService> findByProjectId(Long projectId);

    /**
     * 프로젝트 ID와 활성 상태로 서비스 조회
     */
    List<ProjectService> findByProjectIdAndIsActive(Long projectId, Boolean isActive);

    /**
     * 프로젝트와 서비스 이름으로 서비스 조회
     */
    Optional<ProjectService> findByProjectAndServiceName(Project project, String serviceName);

    /**
     * 프로젝트 ID와 서비스 이름으로 서비스 조회
     */
    Optional<ProjectService> findByProjectIdAndServiceName(Long projectId, String serviceName);

    /**
     * 프로젝트 ID와 서비스 타입으로 서비스 조회
     */
    List<ProjectService> findByProjectIdAndServiceType(Long projectId, ServiceType serviceType);

    /**
     * 프로젝트 ID와 서비스 이름으로 존재 여부 확인
     */
    boolean existsByProjectIdAndServiceName(Long projectId, String serviceName);

    /**
     * 프로젝트로 서비스 개수 조회
     */
    long countByProject(Project project);

    /**
     * 프로젝트 ID로 서비스 개수 조회
     */
    long countByProjectId(Long projectId);
}
