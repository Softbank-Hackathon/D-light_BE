package com.hackathon.melon.domain.auth.onboarding.service;

import com.hackathon.melon.domain.auth.onboarding.dto.*;
import com.hackathon.melon.domain.auth.onboarding.entity.RegistrationToken;
import com.hackathon.melon.domain.auth.onboarding.repository.RegistrationTokenRepository;
import com.hackathon.melon.domain.project.entity.ProjectTarget;
import com.hackathon.melon.domain.project.repository.ProjectTargetRepository;
import com.hackathon.melon.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.UriComponentsBuilder;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Base64;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthOnboardingService {

    private final RegistrationTokenRepository registrationTokenRepository;
    private final ProjectTargetRepository projectTargetRepository;

    @Value("${aws.credentials.access-key}")
    private String awsAccessKey;

    @Value("${aws.credentials.secret-key}")
    private String awsSecretKey;

    @Value("${aws.onboarding.template.bucket:dlite-templates}")
    private String templateBucket;

    @Value("${aws.onboarding.template.key:template.yaml}")
    private String templateKey;

    @Value("${aws.onboarding.template.region:ap-northeast-2}")
    private String templateRegion;

    private static final String TOKEN_PREFIX = "rgx_";
    private static final int TOKEN_LENGTH = 32;

    /**
     * 1️⃣ Registration Token 발급
     */
    @Transactional
    public RegistrationTokenResponse createRegistrationToken(User user, RegistrationTokenRequest request) {
        String token = generateSecureToken();
        LocalDateTime expiresAt = LocalDateTime.now().plusSeconds(request.getTtlSeconds());

        RegistrationToken registrationToken = RegistrationToken.builder()
                .token(token)
                .user(user)
                .expiresAt(expiresAt)
                .status(RegistrationToken.TokenStatus.ACTIVE)
                .build();

        registrationTokenRepository.save(registrationToken);

        log.info("Registration token created for user {}: {}", user.getId(), token);

        return RegistrationTokenResponse.builder()
                .registrationToken(token)
                .expiresAt(expiresAt)
                .build();
    }

    /**
     * 2️⃣ Quick Create 링크 생성
     */
    @Transactional(readOnly = true)
    public QuickCreateLinkResponse createQuickCreateLink(User user, QuickCreateLinkRequest request) {
        // 토큰 검증
        RegistrationToken token = registrationTokenRepository.findByTokenAndUser(request.getRegistrationToken(), user)
                .orElseThrow(() -> new IllegalArgumentException("Invalid or unauthorized registration token"));

        if (!token.isValid()) {
            throw new IllegalArgumentException("Registration token is expired or already used");
        }

        // Presigned URL 생성
        String presignedUrl = generatePresignedUrl(templateBucket, templateKey, templateRegion, 900);

        // Quick Create URL 조합
        String quickCreateUrl = buildQuickCreateUrl(
                request.getRegion(),
                presignedUrl,
                request.getStackName(),
                request.getExternalId(),
                request.getRegistrationToken()
        );

        log.info("Quick create link generated for user {}: stackName={}", user.getId(), request.getStackName());

        return QuickCreateLinkResponse.builder()
                .quickCreateUrl(quickCreateUrl)
                .build();
    }

    /**
     * 3️⃣ CloudFormation Lambda 콜백
     */
    @Transactional
    public CfnCallbackResponse handleCfnCallback(String registrationToken, CfnCallbackRequest request) {
        // 토큰 검증
        RegistrationToken token = registrationTokenRepository.findByToken(registrationToken)
                .orElseThrow(() -> new IllegalArgumentException("Invalid registration token"));

        if (!token.isValid()) {
            throw new IllegalArgumentException("Registration token is expired or already used");
        }

        // 멱등성: stackId로 기존 ProjectTarget 확인
        ProjectTarget projectTarget = projectTargetRepository.findByStackId(request.getStackId())
                .orElse(null);

        if (projectTarget != null) {
            // 업데이트 (멱등)
            projectTarget.updateOnboardingInfo(
                    request.getRoleArn(),
                    request.getBucketName(),
                    request.getRegion(),
                    request.getAccountId(),
                    request.getExternalId(),
                    request.getStackId()
            );
            log.info("Updated existing ProjectTarget: stackId={}", request.getStackId());
        } else {
            // 신규 생성
            projectTarget = ProjectTarget.builder()
                    .user(token.getUser())
                    .accountId(request.getAccountId())
                    .roleArn(request.getRoleArn())
                    .bucketName(request.getBucketName())
                    .region(request.getRegion())
                    .externalId(request.getExternalId())
                    .stackId(request.getStackId())
                    .correlationId(registrationToken)
                    .isDefault(false)  // 온보딩 시에는 기본값 아님
                    .build();
            projectTargetRepository.save(projectTarget);
            log.info("Created new ProjectTarget: stackId={}, accountId={}", request.getStackId(), request.getAccountId());
        }

        // 토큰 사용 처리
        token.markAsUsed();

        return CfnCallbackResponse.success();
    }

    /**
     * 4️⃣ Presigned URL 생성
     */
    public PresignUrlResponse createPresignedUrl(PresignUrlRequest request) {
        String presignedUrl = generatePresignedUrl(
                request.getBucket(),
                request.getKey(),
                request.getRegion(),
                request.getExpiresIn()
        );

        return PresignUrlResponse.builder()
                .presignedUrl(presignedUrl)
                .build();
    }

    // ========== Helper Methods ==========

    private String generateSecureToken() {
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[TOKEN_LENGTH];
        random.nextBytes(bytes);
        return TOKEN_PREFIX + Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    private String generatePresignedUrl(String bucket, String key, String region, int expiresIn) {
        try (S3Presigner presigner = S3Presigner.builder()
                .region(Region.of(region))
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create(awsAccessKey, awsSecretKey)))
                .build()) {

            GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                    .bucket(bucket)
                    .key(key)
                    .build();

            GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                    .signatureDuration(Duration.ofSeconds(expiresIn))
                    .getObjectRequest(getObjectRequest)
                    .build();

            PresignedGetObjectRequest presignedRequest = presigner.presignGetObject(presignRequest);

            return presignedRequest.url().toString();
        } catch (Exception e) {
            log.error("Failed to generate presigned URL: bucket={}, key={}", bucket, key, e);
            throw new RuntimeException("Failed to generate presigned URL", e);
        }
    }

    private String buildQuickCreateUrl(String region, String presignedUrl, String stackName,
                                      String externalId, String registrationToken) {
        try {
            String encodedTemplateUrl = URLEncoder.encode(presignedUrl, StandardCharsets.UTF_8);

            return UriComponentsBuilder
                    .fromUriString("https://console.aws.amazon.com/cloudformation/home")
                    .queryParam("region", region)
                    .fragment("/stacks/quickcreate")
                    .build()
                    .toUriString()
                    + "?templateURL=" + encodedTemplateUrl
                    + "&stackName=" + stackName
                    + "&param_ExternalId=" + externalId
                    + "&param_RegistrationToken=" + registrationToken;
        } catch (Exception e) {
            log.error("Failed to build quick create URL", e);
            throw new RuntimeException("Failed to build quick create URL", e);
        }
    }
}