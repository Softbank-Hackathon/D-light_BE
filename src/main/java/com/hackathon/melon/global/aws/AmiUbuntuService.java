package com.hackathon.melon.global.aws;

import org.springframework.stereotype.Component;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.ssm.SsmClient;
import software.amazon.awssdk.services.ssm.model.GetParameterRequest;
import software.amazon.awssdk.services.ssm.model.GetParameterResponse;

@Component
public class AmiUbuntuService {

    public String getLatestUbuntuAmiId(String region) {
        SsmClient ssm = SsmClient.builder()
                .region(Region.of(region))
                .build();

        GetParameterRequest request = GetParameterRequest.builder() //우분투로 고정 AMI ID 가져오기
                .name("/aws/service/canonical/ubuntu/server/24.04/stable/current/amd64/hvm/ebs-gp3/ami-id")
                .build();

        GetParameterResponse response = ssm.getParameter(request);
        return response.parameter().value();
    }
}