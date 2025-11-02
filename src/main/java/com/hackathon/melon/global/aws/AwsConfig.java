package com.hackathon.melon.global.aws;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sts.StsClient;


@Configuration
public class AwsConfig {
    @Bean
    public StsClient stsClient() {
        return StsClient.builder()
                .region(Region.AP_NORTHEAST_2)
                .build();
    }
}
