package com.hackathon.melon.domain.auth.onboarding.entity;

import com.hackathon.melon.domain.user.entity.User;
import com.hackathon.melon.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "onboarding_records",
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_stack_id", columnNames = "stack_id")
    },
    indexes = {
        @Index(name = "idx_user_id", columnList = "user_id"),
        @Index(name = "idx_account_id", columnList = "account_id")
    }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OnboardingRecord extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "account_id", nullable = false, length = 12)
    private String accountId;

    @Column(name = "role_arn", nullable = false)
    private String roleArn;

    @Column(name = "bucket_name", nullable = false)
    private String bucketName;

    @Column(name = "region", nullable = false, length = 30)
    private String region;

    @Column(name = "external_id", nullable = false, length = 8)
    private String externalId;

    @Column(name = "stack_id", nullable = false, unique = true)
    private String stackId;

    @Column(name = "correlation_id", length = 128)
    private String correlationId;

    @Builder
    public OnboardingRecord(User user, String accountId, String roleArn, String bucketName,
                           String region, String externalId, String stackId, String correlationId) {
        this.user = user;
        this.accountId = accountId;
        this.roleArn = roleArn;
        this.bucketName = bucketName;
        this.region = region;
        this.externalId = externalId;
        this.stackId = stackId;
        this.correlationId = correlationId;
    }

    public void updateRecord(String roleArn, String bucketName, String region) {
        this.roleArn = roleArn;
        this.bucketName = bucketName;
        this.region = region;
    }
}