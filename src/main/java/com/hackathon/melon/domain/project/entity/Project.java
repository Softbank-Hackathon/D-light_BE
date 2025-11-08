package com.hackathon.melon.domain.project.entity;

import com.hackathon.melon.domain.user.entity.User;
import com.hackathon.melon.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "projects", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"id", "project_name"})
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Project extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "project_name")
    private String projectName;

    @Enumerated(EnumType.STRING)
    @Column(name = "service_type")
    private ServiceType serviceType;

    @Column(name = "github_repo_url")
    private String githubRepoUrl;

    @Column(name = "framework_type")
    private String frameworkType;

    @Column(name = "default_branch")
    private String defaultBranch;

    @Column(name = "is_active")
    private boolean isActive;

    @Builder
    public Project(User user, String projectName, ServiceType serviceType, String githubRepoUrl, String frameworkType, String defaultBranch, boolean isActive) {
        this.user = user;
        this.projectName = projectName;
        this.serviceType = serviceType;
        this.githubRepoUrl = githubRepoUrl;
        this.frameworkType = frameworkType;
        this.defaultBranch = defaultBranch;
        this.isActive = isActive;
    }
}