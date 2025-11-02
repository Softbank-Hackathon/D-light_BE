package com.hackathon.melon.global.aws;


import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class AssumeRoleRequestDto {
    private String roleArn;
    private String externalId;
    private String region;
}