package com.hackathon.melon.domain.deployment.service;


import com.hackathon.melon.domain.deployment.dto.request.DeploymentRequestDto;
import com.hackathon.melon.domain.deployment.dto.request.FrontendDeploymentRequestDto;
import jakarta.validation.Valid;
import org.springframework.stereotype.Service;


public interface DeploymentService {
    void deployProject(DeploymentRequestDto deploymentRequestDto);

    String deployFrontend(@Valid FrontendDeploymentRequestDto frontendDeploymentRequestDto);
}
