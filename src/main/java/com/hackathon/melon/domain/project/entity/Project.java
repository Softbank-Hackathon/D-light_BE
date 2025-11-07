package com.hackathon.melon.domain.project.entity;

import com.hackathon.melon.domain.user.entity.User;
import com.hackathon.melon.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 프로젝트 엔티티
 */
@Entity
@Table(name = "projects", indexes = {
        @Index(name = "idx_user_id", columnList = "user_id")
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

    @Column(nullable = false)
    private String name;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive;

    /**
     * 프로젝트 생성 정적 팩토리 메서드
     */
    public static Project createProject(User user, String name) {
        Project project = new Project();
        project.user = user;
        project.name = name;
        project.isActive = true;
        return project;
    }

    /**
     * 프로젝트 비활성화
     */
    public void deactivate() {
        this.isActive = false;
    }

    /**
     * 프로젝트 활성화
     */
    public void activate() {
        this.isActive = true;
    }
}