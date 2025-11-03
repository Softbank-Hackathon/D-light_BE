package com.hackathon.melon.global.aws;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.auth.credentials.AwsSessionCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class S3DeployService {

    public void createS3Bucket(AwsSessionCredentials sessionCredentials,String region,String projectName) {
        S3Client s3 = S3Client.builder()
                .region(Region.of(region))
                .credentialsProvider(StaticCredentialsProvider.create(sessionCredentials))
                .build();

        try {
            s3.createBucket(CreateBucketRequest.builder()
                    .bucket(projectName)
                    .createBucketConfiguration(
                            CreateBucketConfiguration.builder()
                                    .locationConstraint(Region.of(region).id())
                                    .build())
                    .build());

            // 정적 웹사이트 호스팅 활성화
            s3.putBucketWebsite(PutBucketWebsiteRequest.builder()
                    .bucket(projectName)
                    .websiteConfiguration(WebsiteConfiguration.builder()
                            .indexDocument(IndexDocument.builder().suffix("index.html").build())
                            .errorDocument(ErrorDocument.builder().key("index.html").build())
                            .build())
                    .build());
            log.info("정적 웹사이트 호스팅 활성화 완료: {}", projectName);

        } catch (S3Exception e) {
            log.error("S3 버킷 생성 실패: {}", e.awsErrorDetails().errorMessage());
            throw new RuntimeException("S3 버킷 생성에 실패했습니다.");
        }
    }
}
