package com.hackathon.melon.domain.project.entity;

import com.hackathon.melon.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "project_targets", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"project_id", "env"})
})
public class ProjectTarget extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @Enumerated(EnumType.STRING)
    @Column(nullable = true)
    private EnvType env;

    @Column(name = "role_arn")
    private String roleArn;

    @Column(name = "external_id", columnDefinition = "TEXT")
    private String externalId;

    private String region;

    @Column(name = "session_duration_secs")
    private Integer sessionDurationSecs;

    @Column(name = "is_default")
    private boolean isDefault;

    @Builder
    public ProjectTarget(Project project, EnvType env, String roleArn, String externalId, String region, Integer sessionDurationSecs, boolean isDefault) {
        this.project = project;
        this.env = env;
        this.roleArn = roleArn;
        this.externalId = externalId;
        this.region = region;
        this.sessionDurationSecs = sessionDurationSecs;
        this.isDefault = isDefault;
    }
}
