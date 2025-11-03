package com.hackathon.melon.global.aws;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/aws")
@RequiredArgsConstructor
public class AwsController {

    private final AwsService awsService;

    @PostMapping("/assume")
    public String assumeAndListBuckets(@RequestBody AssumeRoleRequestDto assumeRoleRequestDto) {
        awsService.testAssumeRole(assumeRoleRequestDto);
        return "STS AssumeRole success. Check server logs for results.";
    }
}
