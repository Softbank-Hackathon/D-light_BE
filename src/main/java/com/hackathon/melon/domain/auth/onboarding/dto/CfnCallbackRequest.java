package com.hackathon.melon.domain.auth.onboarding.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CfnCallbackRequest {
    @NotBlank
    @Pattern(regexp = "^[0-9]{12}$", message = "Account ID must be exactly 12 digits")
    private String accountId;

    @NotBlank
    @Pattern(regexp = "^arn:aws:iam::[0-9]{12}:role/.+", message = "Invalid IAM Role ARN format")
    private String roleArn;

    @NotBlank
    private String bucketName;

    @NotBlank
    private String region;

    @NotBlank
    @Pattern(regexp = "^[0-9]{8}$", message = "External ID must be exactly 8 digits")
    private String externalId;

    @NotBlank
    @Pattern(regexp = "^arn:aws:cloudformation:.+", message = "Invalid Stack ARN format")
    private String stackId;
}