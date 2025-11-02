package com.hackathon.melon.global.aws;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/aws")
@RequiredArgsConstructor
public class AwsController {

    private final AwsService awsService;

    @PostMapping("/assume")
    public String assumeAndListBuckets(@RequestBody AssumeRoleRequestDto dto) {
        awsService.testAssumeRole(dto.getRoleArn(), dto.getExternalId(), dto.getRegion());
        return " STS AssumeRole success. Check console for bucket list.";
    }

}
