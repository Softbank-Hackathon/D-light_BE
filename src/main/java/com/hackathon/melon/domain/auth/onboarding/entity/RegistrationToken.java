package com.hackathon.melon.domain.auth.onboarding.entity;

import com.hackathon.melon.domain.user.entity.User;
import com.hackathon.melon.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "registration_tokens", indexes = {
        @Index(name = "idx_token", columnList = "token"),
        @Index(name = "idx_user_id", columnList = "user_id")
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RegistrationToken extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "token", nullable = false, unique = true, length = 128)
    private String token;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private TokenStatus status;

    @Column(name = "used_at")
    private LocalDateTime usedAt;

    @Builder
    public RegistrationToken(String token, User user, LocalDateTime expiresAt, TokenStatus status) {
        this.token = token;
        this.user = user;
        this.expiresAt = expiresAt;
        this.status = status;
    }

    public void markAsUsed() {
        this.status = TokenStatus.USED;
        this.usedAt = LocalDateTime.now();
    }

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiresAt);
    }

    public boolean isValid() {
        return status == TokenStatus.ACTIVE && !isExpired();
    }

    public enum TokenStatus {
        ACTIVE,
        USED,
        EXPIRED
    }
}