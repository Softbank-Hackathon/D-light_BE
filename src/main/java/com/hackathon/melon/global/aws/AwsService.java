package com.hackathon.melon.global.aws;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.auth.credentials.AwsSessionCredentials;
import software.amazon.awssdk.services.sts.StsClient;
import software.amazon.awssdk.services.sts.model.AssumeRoleRequest;
import software.amazon.awssdk.services.sts.model.AssumeRoleResponse;

@Service
@RequiredArgsConstructor
@Slf4j
public class AwsService {

    private final StsClient stsClient;

    public AwsSessionCredentials getAssumeRole(AssumeRoleRequestDto assumeRoleRequestDto) { // AWS STS 권한 위임
        String roleArn = assumeRoleRequestDto.getRoleArn();
        String externalId = assumeRoleRequestDto.getExternalId();

        AssumeRoleRequest assumeRoleRequest = AssumeRoleRequest.builder()
                .roleArn(roleArn)
                .roleSessionName("autodeploy-session")
                .externalId(externalId)
                .durationSeconds(3600)
                .build();

        AssumeRoleResponse assumeRoleResponse = stsClient.assumeRole(assumeRoleRequest);

        AwsSessionCredentials sessionCredentials = AwsSessionCredentials.create(
                assumeRoleResponse.credentials().accessKeyId(),
                assumeRoleResponse.credentials().secretAccessKey(),
                assumeRoleResponse.credentials().sessionToken()
        );

        log.debug("임시 AccessKeyId: {}", sessionCredentials.accessKeyId());
        log.debug("임시 SecretAccessKey: {}", sessionCredentials.secretAccessKey());
        log.debug("임시 SessionToken: {}", sessionCredentials.sessionToken());

        return sessionCredentials;
    }
}
