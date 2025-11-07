package com.hackathon.melon.global.aws;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.auth.credentials.AwsSessionCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

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

            s3.putPublicAccessBlock(PutPublicAccessBlockRequest.builder()
                    .bucket(projectName)
                    .publicAccessBlockConfiguration(PublicAccessBlockConfiguration.builder()
                            .blockPublicAcls(false)
                            .ignorePublicAcls(false)
                            .blockPublicPolicy(false)
                            .restrictPublicBuckets(false)
                            .build())
                    .build());
            log.info("퍼블릭 액세스 차단 비활성화 완료: {}", projectName);

            String policy = """
            {
              "Version": "2012-10-17",
              "Statement": [
                {
                  "Sid": "PublicReadGetObject",
                  "Effect": "Allow",
                  "Principal": "*",
                  "Action": ["s3:GetObject"],
                  "Resource": ["arn:aws:s3:::%s/*"]
                }
              ]
            }
            """.formatted(projectName);

            s3.putBucketPolicy(PutBucketPolicyRequest.builder()
                    .bucket(projectName)
                    .policy(policy)
                    .build());
            log.info("퍼블릭 읽기 정책 추가 완료: {}", projectName);
        } catch (S3Exception e) {
            log.error("S3 버킷 생성 실패: {}", e.awsErrorDetails().errorMessage());
            throw new RuntimeException("S3 버킷 생성에 실패했습니다.");
        }
    }
    public String uploadDirectory(AwsSessionCredentials sessionCredentials, String region, String bucketName, Path directoryPath) {
        S3Client s3 = S3Client.builder()
                .region(Region.of(region))
                .credentialsProvider(StaticCredentialsProvider.create(sessionCredentials))
                .build();

        try {
            Files.walk(directoryPath)
                    .filter(Files::isRegularFile)
                    .forEach(path -> {
                        String key = directoryPath.relativize(path).toString().replace("\\", "/");
                        try {
                            s3.putObject(
                                    PutObjectRequest.builder()
                                            .bucket(bucketName)
                                            .key(key)
                                            .contentType(Files.probeContentType(path))
                                            .build(),
                                    path
                            );
                            log.info("S3 업로드 완료: {}/{}", bucketName, key);
                        } catch (Exception e) {
                            log.error("파일 업로드 실패: {}", path, e);
                        }
                    });

            log.info(" 전체 디렉토리 업로드 완료: s3://{}/{}", bucketName, directoryPath);
            String indexUrl = String.format("https://%s.s3.%s.amazonaws.com/index.html", bucketName, region);
            log.info("접근 URL: {}", indexUrl);
            return indexUrl;

        } catch (IOException e) {
            throw new RuntimeException("S3 업로드 중 디렉토리 접근 실패: " + e.getMessage(), e);
        }
    }

}
