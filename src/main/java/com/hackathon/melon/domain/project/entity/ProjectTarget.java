package com.hackathon.melon.domain.project.entity;

import com.hackathon.melon.domain.user.entity.User;
import com.hackathon.melon.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

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

    // ========== AWS 온보딩 정보 ==========

    @Column(name = "account_id", length = 12)
    private String accountId;  // AWS 계정 ID

    @Column(name = "bucket_name", length = 255)
    private String bucketName;  // S3 버킷 이름

    @Column(name = "stack_id", length = 512, unique = true)
    private String stackId;  // CloudFormation Stack ID (멱등성)

    @Column(name = "correlation_id", length = 255)
    private String correlationId;  // Registration Token (추적용)

    @Column(name = "onboarded_at")
    private LocalDateTime onboardedAt;  // 온보딩 완료 시간

    @Builder
    public ProjectTarget(User user, EnvType env, String roleArn, String externalId, String region,
                        Integer sessionDurationSecs, boolean isDefault,
                        String accountId, String bucketName, String stackId,
                        String correlationId, LocalDateTime onboardedAt) {
        this.user = user;
        this.env = env;
        this.roleArn = roleArn;
        this.externalId = externalId;
        this.region = region;
        this.sessionDurationSecs = sessionDurationSecs;
        this.isDefault = isDefault;
        this.accountId = accountId;
        this.bucketName = bucketName;
        this.stackId = stackId;
        this.correlationId = correlationId;
        this.onboardedAt = onboardedAt;
    }

    // 온보딩 정보 업데이트 (멱등성)
    public void updateOnboardingInfo(String roleArn, String bucketName, String region,
                                    String accountId, String externalId, String stackId) {
        this.roleArn = roleArn;
        this.bucketName = bucketName;
        this.region = region;
        this.accountId = accountId;
        this.externalId = externalId;
        this.stackId = stackId;
        this.onboardedAt = LocalDateTime.now();
    }
}
