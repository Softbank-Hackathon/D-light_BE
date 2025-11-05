package com.hackathon.melon.domain.deployment.service;

import com.hackathon.melon.domain.deployment.dto.request.DeploymentRequestDto;
import com.hackathon.melon.global.aws.AssumeRoleRequestDto;
import com.hackathon.melon.global.aws.AwsService;
import com.hackathon.melon.global.aws.Ec2DeployService;
import com.hackathon.melon.global.aws.S3DeployService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.auth.credentials.AwsSessionCredentials;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.services.ec2.model.RunInstancesResponse;
import software.amazon.awssdk.services.s3.S3Client;


@Service
@Slf4j
@RequiredArgsConstructor
public class DeploymentServiceImpl implements DeploymentService {

    private final AwsService awsService;
    private final Ec2DeployService ec2DeployService;
    private final S3DeployService s3DeployService;

    @Override
    public void deployProject(DeploymentRequestDto deploymentRequestDto) {

        AwsSessionCredentials creds;
        try {
            creds = awsService.getAssumeRole(
                    new AssumeRoleRequestDto(
                            deploymentRequestDto.getRoleArn(),
                            deploymentRequestDto.getExternalId()
                    )
            );
        } catch (Exception e) {
            log.error("STS AssumeRole 실패: {}", e.getMessage(), e);
            throw new RuntimeException("AWS 권한 위임(AssumeRole)에 실패했습니다. Role ARN 또는 ExternalId를 확인하세요.");
        }
        String env = deploymentRequestDto.getEnvironmentVariables();

        // 프로젝트 타입에 따라 배포 로직 분기 추후 메서드 분리 고려

        if (deploymentRequestDto.getProjectType().equals("frontend")){ // 프론트엔드 프로젝트인 경우
            String projectName = deploymentRequestDto.getProjectName()+"-frontend";
            s3DeployService.createS3Bucket(
                    creds,
                    deploymentRequestDto.getRegion(),
                    projectName
            );

            //TOdo: 프론트 깃허브 연동 및 배포 스크립트 실행 로직 구현





        } else if (deploymentRequestDto.getProjectType().equals("backend")) { // 백엔드 프로젝트인 경우
            String projectName = deploymentRequestDto.getProjectName()+"-backend";
            RunInstancesResponse response=  ec2DeployService.createSmallestEc2(
                    creds,
                    deploymentRequestDto.getRegion(),
                    projectName
            );
            //TOdo: 백엔드 깃허브 연동 및 배포 스크립트 실행 로직 구현

        }
        else{
            throw new IllegalArgumentException("지원하지 않는 프로젝트 타입입니다.");
        }



    }
}
