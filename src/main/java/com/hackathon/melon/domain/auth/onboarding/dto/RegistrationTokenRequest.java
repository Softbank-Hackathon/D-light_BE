package com.hackathon.melon.domain.auth.onboarding.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class RegistrationTokenRequest {
    @Min(value = 60, message = "TTL must be at least 60 seconds")
    @Max(value = 3600, message = "TTL must not exceed 3600 seconds")
    private Integer ttlSeconds;
}