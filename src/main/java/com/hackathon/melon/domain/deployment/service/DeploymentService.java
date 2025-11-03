package com.hackathon.melon.domain.deployment.service;


import com.hackathon.melon.domain.deployment.dto.request.DeploymentRequestDto;
import org.springframework.stereotype.Service;


public interface DeploymentService {
    void deployProject(DeploymentRequestDto deploymentRequestDto);
}
