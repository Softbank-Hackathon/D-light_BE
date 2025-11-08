package com.hackathon.melon.domain.project.service;

import com.hackathon.melon.domain.project.dto.ProjectTargetRequestDto;
import com.hackathon.melon.domain.project.entity.ProjectTarget;
import com.hackathon.melon.domain.project.repository.ProjectTargetRepository;
import com.hackathon.melon.domain.user.entity.User;
import com.hackathon.melon.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProjectTargetService {

    private final UserRepository userRepository;
    private final ProjectTargetRepository projectTargetRepository;

    @Transactional
    public ProjectTarget createProjectTarget(ProjectTargetRequestDto requestDto) {
        User user = userRepository.findById(requestDto.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + requestDto.getUserId()));

        ProjectTarget projectTarget = ProjectTarget.builder()
                .user(user)
                .env(requestDto.getEnv())
                .roleArn(requestDto.getRoleArn())
                .externalId(requestDto.getExternalId())
                .region(requestDto.getRegion())
                .sessionDurationSecs(requestDto.getSessionDurationSecs())
                .isDefault(requestDto.isDefault())
                .build();

        return projectTargetRepository.save(projectTarget);
    }

    @Transactional(readOnly = true)
    public List<ProjectTarget> findAll() {
        return projectTargetRepository.findAll();
    }
}
