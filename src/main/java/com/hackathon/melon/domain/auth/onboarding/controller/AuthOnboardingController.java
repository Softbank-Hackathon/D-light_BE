package com.hackathon.melon.domain.auth.onboarding.controller;

import com.hackathon.melon.domain.auth.onboarding.dto.*;
import com.hackathon.melon.domain.auth.onboarding.service.AuthOnboardingService;
import com.hackathon.melon.domain.user.entity.User;
import com.hackathon.melon.domain.user.repository.UserRepository;
import com.hackathon.melon.global.common.CustomApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Auth Onboarding", description = "AWS 계정 온보딩 API")
public class AuthOnboardingController {

    private final AuthOnboardingService authOnboardingService;
    private final UserRepository userRepository;

    /**
     * 1️⃣ Registration Token 발급
     */
    @Operation(summary = "Registration Token 발급", description = "Quick Create용 단기 등록 토큰 생성")
    @PostMapping("/registration-token")
    public ResponseEntity<CustomApiResponse<RegistrationTokenResponse>> createRegistrationToken(
            @AuthenticationPrincipal OAuth2User oauth2User,
            @Valid @RequestBody RegistrationTokenRequest request) {

        User user = getUserFromOAuth2(oauth2User);

        RegistrationTokenResponse response = authOnboardingService.createRegistrationToken(user, request);
        return ResponseEntity.ok(CustomApiResponse.onSuccess(response));
    }

    /**
     * 2️⃣ Quick Create 링크 생성
     */
    @Operation(summary = "Quick Create 링크 생성", description = "CFN 템플릿 presigned URL을 만들고 콘솔 링크 반환")
    @PostMapping("/quick-create-link")
    public ResponseEntity<CustomApiResponse<QuickCreateLinkResponse>> createQuickCreateLink(
            @AuthenticationPrincipal OAuth2User oauth2User,
            @Valid @RequestBody QuickCreateLinkRequest request) {

        User user = getUserFromOAuth2(oauth2User);

        QuickCreateLinkResponse response = authOnboardingService.createQuickCreateLink(user, request);
        return ResponseEntity.ok(CustomApiResponse.onSuccess(response));
    }

    /**
     * 3️⃣ CloudFormation Lambda 콜백
     */
    @Operation(summary = "CFN Lambda 콜백", description = "스택 생성 시 Role/Bucket 정보 등록")
    @PostMapping("/cfn-callback")
    public ResponseEntity<CustomApiResponse<CfnCallbackResponse>> handleCfnCallback(
            @RequestHeader("X-Dlite-Registration-Token") String registrationToken,
            @RequestHeader(value = "X-Dlite-Signature", required = false) String signature,
            @Valid @RequestBody CfnCallbackRequest request,
            @RequestBody String rawBody) {

        CfnCallbackResponse response = authOnboardingService.handleCfnCallback(
                registrationToken, signature, request, rawBody);

        return ResponseEntity.ok(CustomApiResponse.onSuccess(response));
    }

    /**
     * 4️⃣ Presigned URL 생성
     */
    @Operation(summary = "Presigned URL 생성", description = "S3 템플릿 파일 접근용 URL 생성")
    @PostMapping("/presign-url")
    public ResponseEntity<CustomApiResponse<PresignUrlResponse>> createPresignedUrl(
            @AuthenticationPrincipal OAuth2User oauth2User,
            @Valid @RequestBody PresignUrlRequest request) {

        if (oauth2User == null) {
            return ResponseEntity.status(401)
                    .body(CustomApiResponse.onFailure("인증되지 않은 사용자입니다.", null));
        }

        PresignUrlResponse response = authOnboardingService.createPresignedUrl(request);
        return ResponseEntity.ok(CustomApiResponse.onSuccess(response));
    }

    // Helper method
    private User getUserFromOAuth2(OAuth2User oauth2User) {
        if (oauth2User == null) {
            throw new IllegalArgumentException("인증되지 않은 사용자입니다.");
        }

        Long githubId = ((Number) oauth2User.getAttributes().get("id")).longValue();
        return userRepository.findByGithubId(githubId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
    }
}
