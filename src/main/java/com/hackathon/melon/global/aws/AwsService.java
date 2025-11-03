package com.hackathon.melon.global.aws;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.auth.credentials.AwsSessionCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.awssdk.services.ec2.model.InstanceType;
import software.amazon.awssdk.services.ec2.model.RunInstancesRequest;
import software.amazon.awssdk.services.ec2.model.RunInstancesResponse;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.ListBucketsResponse;
import software.amazon.awssdk.services.sts.StsClient;
import software.amazon.awssdk.services.sts.model.AssumeRoleRequest;
import software.amazon.awssdk.services.sts.model.AssumeRoleResponse;

@Service
@RequiredArgsConstructor
@Slf4j
public class AwsService {

    private final StsClient stsClient;

    public void testAssumeRole(AssumeRoleRequestDto assumeRoleRequestDto) {
        String roleArn = assumeRoleRequestDto.getRoleArn();
        String externalId = assumeRoleRequestDto.getExternalId();
        String clientRegion = assumeRoleRequestDto.getRegion();

        AssumeRoleRequest assumeRoleRequest = AssumeRoleRequest.builder()
                .roleArn(roleArn)
                .roleSessionName("autodeploy-session")
                .externalId(externalId)
                .durationSeconds(3600)
                .build();

        AssumeRoleResponse assumeRoleResponse = stsClient.assumeRole(assumeRoleRequest);

        AwsSessionCredentials sessionCredentials = AwsSessionCredentials.create(
                assumeRoleResponse.credentials().accessKeyId(),
                assumeRoleResponse.credentials().secretAccessKey(),
                assumeRoleResponse.credentials().sessionToken()
        );

        log.debug("AccessKeyId: {}", sessionCredentials.accessKeyId());
        log.debug("SecretAccessKey: {}", sessionCredentials.secretAccessKey());
        log.debug("SessionToken: {}", sessionCredentials.sessionToken());

        S3Client s3Client = S3Client.builder()
                .region(Region.of(clientRegion))
                .credentialsProvider(StaticCredentialsProvider.create(sessionCredentials))
                .build();

        createSmallestEc2(sessionCredentials, clientRegion);

        ListBucketsResponse buckets = s3Client.listBuckets();
        buckets.buckets().forEach(b -> log.info("Bucket: {}", b.name()));
    }

    private void createSmallestEc2(AwsSessionCredentials sessionCredentials, String region) {
        Ec2Client ec2 = Ec2Client.builder()
                .region(Region.of(region))
                .credentialsProvider(StaticCredentialsProvider.create(sessionCredentials))
                .build();

        RunInstancesRequest request = RunInstancesRequest.builder()
                .imageId("ami-0a0de518b1fc4524c")
                .instanceType(InstanceType.T2_MICRO)
                .minCount(1)
                .maxCount(1)
                .build();

        RunInstancesResponse response = ec2.runInstances(request);
        log.info("EC2 인스턴스 생성됨: {}", response.instances().get(0).instanceId());
    }
}
