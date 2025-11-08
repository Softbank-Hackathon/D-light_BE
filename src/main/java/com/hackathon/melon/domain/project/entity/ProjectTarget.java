package com.hackathon.melon.domain.project.entity;

import com.hackathon.melon.domain.user.entity.User;
import com.hackathon.melon.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "project_targets",
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_user_env", columnNames = {"user_id", "env"})
    },
    indexes = {
        @Index(name = "idx_user_id", columnList = "user_id"),
        @Index(name = "idx_user_default", columnList = "user_id, is_default")
    }
)
public class ProjectTarget extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(name = "env", nullable = true)
    private EnvType env;

    @Column(name = "role_arn")
    private String roleArn;

    @Column(name = "external_id", columnDefinition = "TEXT")
    private String externalId;

    @Column(name = "region")
    private String region;

    @Column(name = "session_duration_secs")
    private Integer sessionDurationSecs;

    @Column(name = "is_default", nullable = false)
    private boolean isDefault;

    @Builder
    public ProjectTarget(User user, EnvType env, String roleArn, String externalId, String region, Integer sessionDurationSecs, boolean isDefault) {
        this.user = user;
        this.env = env;
        this.roleArn = roleArn;
        this.externalId = externalId;
        this.region = region;
        this.sessionDurationSecs = sessionDurationSecs;
        this.isDefault = isDefault;
    }
}
