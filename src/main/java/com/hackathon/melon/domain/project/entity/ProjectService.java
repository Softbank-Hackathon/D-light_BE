package com.hackathon.melon.domain.project.entity;

import com.hackathon.melon.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 프로젝트 서비스 엔티티
 * 프로젝트 내의 FE/BE 등 배포 단위 서비스를 나타냄
 */
@Entity
@Table(name = "project_services",
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_project_service", columnNames = {"project_id", "service_name"})
    },
    indexes = {
        @Index(name = "idx_project_id", columnList = "project_id"),
        @Index(name = "idx_service_type", columnList = "service_type")
    }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProjectService extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @Column(name = "service_name", nullable = false)
    private String serviceName;

    @Enumerated(EnumType.STRING)
    @Column(name = "service_type", nullable = false)
    private ServiceType serviceType;

    @Column(name = "github_repo_url")
    private String githubRepoUrl;

    @Column(name = "framework_type")
    private String frameworkType;

    @Column(name = "default_branch")
    private String defaultBranch;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive;

    /**
     * 프로젝트 서비스 생성 정적 팩토리 메서드
     */
    public static ProjectService createProjectService(
            Project project,
            String serviceName,
            ServiceType serviceType,
            String githubRepoUrl,
            String frameworkType,
            String defaultBranch
    ) {
        ProjectService projectService = new ProjectService();
        projectService.project = project;
        projectService.serviceName = serviceName;
        projectService.serviceType = serviceType;
        projectService.githubRepoUrl = githubRepoUrl;
        projectService.frameworkType = frameworkType;
        projectService.defaultBranch = defaultBranch;
        projectService.isActive = true;
        return projectService;
    }

    /**
     * 서비스 정보 업데이트
     */
    public void updateService(
            String serviceName,
            String githubRepoUrl,
            String frameworkType,
            String defaultBranch
    ) {
        if (serviceName != null) {
            this.serviceName = serviceName;
        }
        if (githubRepoUrl != null) {
            this.githubRepoUrl = githubRepoUrl;
        }
        if (frameworkType != null) {
            this.frameworkType = frameworkType;
        }
        if (defaultBranch != null) {
            this.defaultBranch = defaultBranch;
        }
    }

    /**
     * 서비스 비활성화
     */
    public void deactivate() {
        this.isActive = false;
    }

    /**
     * 서비스 활성화
     */
    public void activate() {
        this.isActive = true;
    }
}
