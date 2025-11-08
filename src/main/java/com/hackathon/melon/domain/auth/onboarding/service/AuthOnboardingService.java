package com.hackathon.melon.domain.auth.onboarding.service;

import com.hackathon.melon.domain.auth.onboarding.dto.*;
import com.hackathon.melon.domain.auth.onboarding.entity.OnboardingRecord;
import com.hackathon.melon.domain.auth.onboarding.entity.RegistrationToken;
import com.hackathon.melon.domain.auth.onboarding.repository.OnboardingRecordRepository;
import com.hackathon.melon.domain.auth.onboarding.repository.RegistrationTokenRepository;
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

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.HexFormat;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthOnboardingService {

    private final RegistrationTokenRepository registrationTokenRepository;
    private final OnboardingRecordRepository onboardingRecordRepository;

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

    @Value("${auth.onboarding.hmac-secret}")
    private String hmacSecret;

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
    public CfnCallbackResponse handleCfnCallback(String registrationToken, String signature, CfnCallbackRequest request, String rawBody) {
        // 토큰 검증
        RegistrationToken token = registrationTokenRepository.findByToken(registrationToken)
                .orElseThrow(() -> new IllegalArgumentException("Invalid registration token"));

        if (!token.isValid()) {
            throw new IllegalArgumentException("Registration token is expired or already used");
        }

        // Signature 검증 (선택적이지만 권장)
        if (signature != null && !verifySignature(registrationToken, rawBody, signature)) {
            throw new SecurityException("Invalid signature");
        }

        // ExternalId 매칭 확인 (토큰과 연결된 사용자 검증)
        // 실제로는 토큰 생성 시 externalId를 저장하고 여기서 검증해야 함

        // 멱등성: stackId로 기존 레코드 확인
        OnboardingRecord record = onboardingRecordRepository.findByStackId(request.getStackId())
                .orElse(null);

        if (record != null) {
            // 업데이트 (멱등)
            record.updateRecord(request.getRoleArn(), request.getBucketName(), request.getRegion());
            log.info("Updated existing onboarding record: stackId={}", request.getStackId());
        } else {
            // 신규 생성
            record = OnboardingRecord.builder()
                    .user(token.getUser())
                    .accountId(request.getAccountId())
                    .roleArn(request.getRoleArn())
                    .bucketName(request.getBucketName())
                    .region(request.getRegion())
                    .externalId(request.getExternalId())
                    .stackId(request.getStackId())
                    .correlationId(registrationToken)
                    .build();
            onboardingRecordRepository.save(record);
            log.info("Created new onboarding record: stackId={}, accountId={}", request.getStackId(), request.getAccountId());
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

    private boolean verifySignature(String token, String rawBody, String providedSignature) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKeySpec = new SecretKeySpec(
                    hmacSecret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            mac.init(secretKeySpec);

            byte[] hash = mac.doFinal((token + rawBody).getBytes(StandardCharsets.UTF_8));
            String expectedSignature = HexFormat.of().formatHex(hash);

            return expectedSignature.equalsIgnoreCase(providedSignature);
        } catch (Exception e) {
            log.error("Failed to verify signature", e);
            return false;
        }
    }
}