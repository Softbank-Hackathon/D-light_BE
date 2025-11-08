package com.hackathon.melon.domain.project.service;

import com.hackathon.melon.domain.project.dto.ProjectTargetRequestDto;
import com.hackathon.melon.domain.project.entity.Project;
import com.hackathon.melon.domain.project.entity.ProjectTarget;
import com.hackathon.melon.domain.project.repository.ProjectRepository;
import com.hackathon.melon.domain.project.repository.ProjectTargetRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProjectTargetService {

    private final ProjectRepository projectRepository;
    private final ProjectTargetRepository projectTargetRepository;

    @Transactional
    public ProjectTarget createProjectTarget(ProjectTargetRequestDto requestDto) {
        Project project = projectRepository.findById(requestDto.getProjectId())
                .orElseThrow(() -> new IllegalArgumentException("Project not found with id: " + requestDto.getProjectId()));

        ProjectTarget projectTarget = ProjectTarget.builder()
                .project(project)
                .env(requestDto.getEnv())
                .roleArn(requestDto.getRoleArn())
                .externalId(requestDto.getExternalId())
                .region(requestDto.getRegion())
                .sessionDurationSecs(requestDto.getSessionDurationSecs())
                .isDefault(true)
                .build();

        return projectTargetRepository.save(projectTarget);
    }

    @Transactional(readOnly = true)
    public List<ProjectTarget> findAll() {
        return projectTargetRepository.findAll();
    }
}
