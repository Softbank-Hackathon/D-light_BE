package com.hackathon.melon.global.aws;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AssumeRoleRequestDto {
    private String roleArn;
    private String externalId;
}