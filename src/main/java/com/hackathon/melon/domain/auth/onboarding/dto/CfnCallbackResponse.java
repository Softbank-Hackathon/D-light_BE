package com.hackathon.melon.domain.auth.onboarding.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CfnCallbackResponse {
    private Boolean ok;

    public static CfnCallbackResponse success() {
        return new CfnCallbackResponse(true);
    }
}