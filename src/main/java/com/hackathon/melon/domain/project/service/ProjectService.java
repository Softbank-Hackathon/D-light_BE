package com.hackathon.melon.domain.project.service;

import com.hackathon.melon.domain.project.dto.ProjectRequest;
import com.hackathon.melon.domain.project.dto.ProjectResponse;
import com.hackathon.melon.domain.project.entity.Project;
import com.hackathon.melon.domain.project.repository.ProjectRepository;
import com.hackathon.melon.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepository projectRepository;

    @Transactional
    public Project createProject(User user, ProjectRequest request) {
        Project project = Project.builder()
                .user(user)
                .projectName(request.getProjectName())
                .serviceType(request.getServiceType())
                .githubRepoUrl(request.getGithubRepoUrl())
                .frameworkType(request.getFrameworkType())
                .defaultBranch(request.getDefaultBranch())
                .isActive(true)
                .build();
        return projectRepository.save(project);
    }

    @Transactional(readOnly = true)
    public List<ProjectResponse> getProjects(long userId) {
        return projectRepository.findAllByUserId(userId).stream()
                .map(ProjectResponse::from)
                .collect(Collectors.toList());
    }
}
