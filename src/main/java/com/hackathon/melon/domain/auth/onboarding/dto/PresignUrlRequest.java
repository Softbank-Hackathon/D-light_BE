package com.hackathon.melon.domain.auth.onboarding.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PresignUrlRequest {
    @NotBlank
    private String bucket;

    @NotBlank
    private String key;

    @NotBlank
    private String region;

    @Min(value = 60, message = "ExpiresIn must be at least 60 seconds")
    @Max(value = 3600, message = "ExpiresIn must not exceed 3600 seconds")
    private Integer expiresIn;
}