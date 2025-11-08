package com.hackathon.melon.domain.auth.onboarding.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class QuickCreateLinkRequest {
    @NotBlank
    @Pattern(regexp = "^[0-9]{8}$", message = "External ID must be exactly 8 digits")
    private String externalId;

    @NotBlank
    private String registrationToken;

    @NotBlank
    private String region;

    @NotBlank
    @Pattern(regexp = "^[a-zA-Z0-9-]+$", message = "Stack name must contain only alphanumeric characters and hyphens")
    private String stackName;
}