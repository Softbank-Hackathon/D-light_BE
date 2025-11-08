package com.hackathon.melon.domain.deployment.service;

import com.hackathon.melon.domain.deployment.dto.request.DeploymentRequestDto;
import com.hackathon.melon.domain.deployment.dto.request.FrontendDeploymentRequestDto;
import com.hackathon.melon.global.aws.AssumeRoleRequestDto;
import com.hackathon.melon.global.aws.AwsService;
import com.hackathon.melon.global.aws.Ec2DeployService;
import com.hackathon.melon.global.aws.S3DeployService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.auth.credentials.AwsSessionCredentials;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.services.ec2.model.RunInstancesResponse;
import software.amazon.awssdk.services.s3.S3Client;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.Map;


@Service
@Slf4j
@RequiredArgsConstructor
// 자체 서버에서 빌드해서 산출물을 S3에 올리거나 EC2에 배포하는 방식으로 구현 즉, 자체 서버에는 npm, nodejs, git, java, mvn 등이 설치되어 있어야 함
public class DeploymentServiceImpl implements DeploymentService {

    private final AwsService awsService;
    private final Ec2DeployService ec2DeployService;
    private final S3DeployService s3DeployService;

    @Override
    public void deployProject(DeploymentRequestDto deploymentRequestDto) {

        AwsSessionCredentials creds;
        try {
            creds = awsService.getAssumeRole(
                    new AssumeRoleRequestDto(
                            deploymentRequestDto.getRoleArn(),
                            deploymentRequestDto.getExternalId()
                    )
            );
        } catch (Exception e) {
            log.error("STS AssumeRole 실패: {}", e.getMessage(), e);
            throw new RuntimeException("AWS 권한 위임(AssumeRole)에 실패했습니다. Role ARN 또는 ExternalId를 확인하세요.");
        }
        String env = deploymentRequestDto.getEnvironmentVariables();

        // 프로젝트 타입에 따라 배포 로직 분기 추후 메서드 분리 고려

        if (deploymentRequestDto.getProjectType().equals("frontend")){ // 프론트엔드 프로젝트인 경우
            String projectName = deploymentRequestDto.getProjectName()+"-frontend";
            s3DeployService.createS3Bucket(
                    creds,
                    deploymentRequestDto.getRegion(),
                    projectName
            );



            //TOdo: 프론트 깃허브 연동 및 배포 스크립트 실행 로직 구현





        } else if (deploymentRequestDto.getProjectType().equals("backend")) { // 백엔드 프로젝트인 경우
            String projectName = deploymentRequestDto.getProjectName()+"-backend";
            RunInstancesResponse response=  ec2DeployService.createSmallestEc2(
                    creds,
                    deploymentRequestDto.getRegion(),
                    projectName
            );
            //TOdo: 백엔드 깃허브 연동 및 배포 스크립트 실행 로직 구현

        }
        else{
            throw new IllegalArgumentException("지원하지 않는 프로젝트 타입입니다.");
        }

    }
    @Override
    public String deployFrontend(FrontendDeploymentRequestDto dto) {
        AwsSessionCredentials creds = awsService.getAssumeRole(new AssumeRoleRequestDto(dto.getRoleArn(), dto.getExternalId()));

        String projectName = dto.getProjectName() + "-frontend";

        Path workDir = Paths.get("src/main/resources/deploy", projectName).toAbsolutePath(); //프로젝트 이름으로 작업 디렉토리 설정

        try {
            if (Files.exists(workDir)) deleteRecursively(workDir);
            Files.createDirectories(workDir);
            log.info("프로젝트 파일 저장 위치 : {}", workDir.toString());

            String branch = dto.getBranch() != null ? dto.getBranch() : "main";
            run("git clone -b " + branch + " " + dto.getGithubRepositoryUrl() + " " + workDir, null);
            String pm = detectPackageManager(workDir);

            run("cd " + workDir + " && " + pm + " install", dto.getEnv()); // 패키지 매니저에 맞게 의존성 설치
            String buildCmd = switch (pm) {
                case "yarn" -> "yarn build";
                case "pnpm" -> "pnpm build";
                case "bun"  -> "bun run build";
                default     -> "npm run build";
            };
            run("cd " + workDir + " && " + buildCmd, dto.getEnv());

            Path outDir = detectOutDir(workDir);
            s3DeployService.createS3Bucket(creds, dto.getRegion(), projectName);
            String indexUrl = s3DeployService.uploadDirectory(creds, dto.getRegion(), projectName, outDir);
            log.info("프론트엔드 배포 완료: {}", projectName);
            return indexUrl;
        } catch (Exception e) {
            log.error("프론트엔드 배포 실패: {}", e.getMessage(), e);
            throw new RuntimeException("프론트엔드 배포 실패: " + e.getMessage(), e);
        } finally {
            try {
                deleteRecursively(workDir);
                log.info("작업 디렉토리 정리 완료: {}", workDir);
            } catch (IOException ignore) {
                log.warn("작업 디렉토리 정리 중 경고: {}", workDir);
            }
        }
    }

    private void run(String cmd, Map<String, String> env) throws Exception {
        ProcessBuilder pb = new ProcessBuilder("bash", "-lc", cmd);
        if (env != null && !env.isEmpty()) pb.environment().putAll(env); //env가 없으면 기본 환경변수 사용
        pb.inheritIO();
        Process p = pb.start();
        if (p.waitFor() != 0) throw new RuntimeException("명령 실패: " + cmd);
    }

    private String detectPackageManager(Path dir) { // 클라이언트의 프로젝트 (깃허브 링크) 폴더에서 패키지 매니저 감지
        if (Files.exists(dir.resolve("yarn.lock"))) return "yarn";
        if (Files.exists(dir.resolve("pnpm-lock.yaml"))) return "pnpm";
        log.info("패키지 매니저 감지 실패, 기본값으로 npm 사용");
        return "npm";
    }

    private Path detectOutDir(Path dir) {
        for (String c : new String[]{"dist", "build", "out"}) {
            Path p = dir.resolve(c);
            if (Files.isDirectory(p)) return p;
        }
        throw new RuntimeException("빌드 결과 폴더를 찾지 못했습니다.");
    }

    private void deleteRecursively(Path dir) throws IOException {
        if (!Files.exists(dir)) return;
        try (var walk = Files.walk(dir)) {
            walk.sorted(Comparator.reverseOrder())
                    .forEach(p -> { try { Files.deleteIfExists(p); } catch (IOException ignored) {} });
        }
    }

}
