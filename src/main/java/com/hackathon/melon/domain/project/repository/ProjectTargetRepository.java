package com.hackathon.melon.domain.project.repository;

import com.hackathon.melon.domain.project.entity.EnvType;
import com.hackathon.melon.domain.project.entity.ProjectTarget;
import com.hackathon.melon.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProjectTargetRepository extends JpaRepository<ProjectTarget, Long> {
    Optional<ProjectTarget> findByUserAndIsDefaultTrue(User user);
    Optional<ProjectTarget> findByUserAndEnv(User user, EnvType env);
    List<ProjectTarget> findAllByUser(User user);
    Optional<ProjectTarget> findByStackId(String stackId);  // CFN 콜백 멱등성용
}

