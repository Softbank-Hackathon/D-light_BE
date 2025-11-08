package com.hackathon.melon.domain.project.repository;

import com.hackathon.melon.domain.project.entity.Project;
import com.hackathon.melon.domain.project.entity.ProjectTarget;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProjectTargetRepository extends JpaRepository<ProjectTarget, Long> {
    Optional<ProjectTarget> findByProjectAndIsDefaultTrue(Project project);
}

