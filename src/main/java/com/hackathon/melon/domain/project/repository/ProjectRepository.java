package com.hackathon.melon.domain.project.repository;

import com.hackathon.melon.domain.project.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * 프로젝트 레포지토리
 */
@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {
}