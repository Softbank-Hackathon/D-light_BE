package com.hackathon.melon.global.aws;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.auth.credentials.AwsSessionCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.ListBucketsResponse;
import software.amazon.awssdk.services.sts.StsClient;
import software.amazon.awssdk.services.sts.model.AssumeRoleRequest;
import software.amazon.awssdk.services.sts.model.AssumeRoleResponse;

@Service
@RequiredArgsConstructor
public class AwsService {

    private final StsClient stsClient;

    public void testAssumeRole(String roleArn, String externalId, String clientRegion) {
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

        S3Client s3Client = S3Client.builder()
                .region(Region.of(clientRegion))
                .credentialsProvider(StaticCredentialsProvider.create(sessionCredentials))
                .build();

        ListBucketsResponse buckets = s3Client.listBuckets();
        buckets.buckets().forEach(b -> System.out.println(" Bucket: " + b.name()));
    }
}