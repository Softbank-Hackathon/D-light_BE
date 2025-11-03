package com.hackathon.melon.global.aws;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.auth.credentials.AwsSessionCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.awssdk.services.ec2.model.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class Ec2DeployService {

    private final AmiUbuntuService amiUbuntuService;

    public void createSmallestEc2(AwsSessionCredentials sessionCredentials, String region, String projectName) {

        String ubuntuAmiId =amiUbuntuService.getLatestUbuntuAmiId(region);

        Ec2Client ec2 = Ec2Client.builder()
                .region(Region.of(region))
                .credentialsProvider(StaticCredentialsProvider.create(sessionCredentials))
                .build();

        RunInstancesRequest request = RunInstancesRequest.builder()
                .imageId(ubuntuAmiId) // 최신 우분투 AMI ID 사용
                .instanceType(InstanceType.T2_MICRO)
                .minCount(1)
                .maxCount(1)
                .tagSpecifications(
                        TagSpecification.builder()
                                .resourceType(ResourceType.INSTANCE)
                                .tags(Tag.builder().key("Name").value(projectName).build())
                                .build()
                )
                .build();

        RunInstancesResponse response = ec2.runInstances(request);
        log.info("EC2 인스턴스 생성됨: {}", response.instances().get(0).instanceId());
    }
}
