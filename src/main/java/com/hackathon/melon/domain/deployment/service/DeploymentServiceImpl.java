package com.hackathon.melon.domain.deployment.service;

import com.hackathon.melon.domain.deployment.dto.request.DeploymentRequestDto;
// Project_Service Repository, Entity 에서 repo url, service name (기존에는 project name이엇으나 서비스로 변경 - 하위 작업이라서.. )
// 및 default_branch 가져오도록 변경함. default_branch 의 경우 기본값 main 에서 사용자 입력시 override 되도록 구현 필요..(제가 해야되는..아마도?)
import com.hackathon.melon.domain.deployment.dto.request.FrontendDeploymentRequestDto;
import com.hackathon.melon.domain.project.entity.Project;
import com.hackathon.melon.domain.project.entity.ProjectTarget;
import com.hackathon.melon.domain.project.repository.ProjectRepository;
import com.hackathon.melon.domain.project.repository.ProjectTargetRepository;
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
    private final ProjectRepository projectRepository;
    private final ProjectTargetRepository projectTargetRepository;

    @Override
    public void deployProject(DeploymentRequestDto deploymentRequestDto) {
        // This method might also need refactoring, but focusing on deployFrontend first.
        // ... (existing code)
    }

    @Override
    public String deployFrontend(FrontendDeploymentRequestDto dto) {
        log.info("==================== Frontend Deployment Start ====================");
        log.info("Attempting to deploy frontend for projectId: {}", dto.getProjectId());

        Project project = projectRepository.findById(dto.getProjectId())
                .orElseThrow(() -> new IllegalArgumentException("프로젝트를 찾을 수 없습니다. projectId: " + dto.getProjectId()));
        log.info("Found Project: '{}' (ID: {})", project.getProjectName(), project.getId());

        // Project에서 User를 가져와서 해당 User의 기본 배포 설정 조회
        ProjectTarget projectTarget = projectTargetRepository.findByUserAndIsDefaultTrue(project.getUser())
                .orElseThrow(() -> new IllegalArgumentException("해당 사용자의 기본 배포 설정을 찾을 수 없습니다. userId: " + project.getUser().getId()));

        log.info("Default Project Target Loaded: ID={}", projectTarget.getId());
        log.info("  - Region: {}", projectTarget.getRegion());
        log.info("  - Role ARN: {}", projectTarget.getRoleArn());
        log.info("  - External ID: {}", projectTarget.getExternalId());
        log.info("  - Bucket Name: {}", projectTarget.getBucketName());

        // 온보딩 시 생성된 버킷이 없으면 에러
        if (projectTarget.getBucketName() == null || projectTarget.getBucketName().isEmpty()) {
            throw new IllegalArgumentException("온보딩된 S3 버킷이 없습니다. AWS 계정 온보딩을 먼저 완료해주세요.");
        }

        AwsSessionCredentials creds = awsService.getAssumeRole(new AssumeRoleRequestDto(projectTarget.getRoleArn(), projectTarget.getExternalId()));

        String projectName = project.getProjectName() + "-frontend";
        String bucketName = projectTarget.getBucketName();  // 온보딩 시 생성된 버킷 사용

        Path workDir = Paths.get("src/main/resources/deploy", projectName).toAbsolutePath();

        try {
            if (Files.exists(workDir)) deleteRecursively(workDir);
            Files.createDirectories(workDir);
            log.info("프로젝트 파일 저장 위치 : {}", workDir.toString());

            String branch = project.getDefaultBranch() != null ? project.getDefaultBranch() : "main";
            run("git clone -b " + branch + " " + project.getGithubRepoUrl() + " " + workDir, null);
            String pm = detectPackageManager(workDir);

            run("cd " + workDir + " && " + pm + " install", dto.getEnv());
            String buildCmd = switch (pm) {
                case "yarn" -> "yarn build";
                case "pnpm" -> "pnpm build";
                case "bun"  -> "bun run build";
                default     -> "npm run build";
            };
            run("cd " + workDir + " && " + buildCmd, dto.getEnv());

            Path outDir = detectOutDir(workDir);
            // 온보딩 시 생성된 버킷 사용 (버킷이 이미 존재하므로 createS3Bucket 호출 불필요)
            String indexUrl = s3DeployService.uploadDirectory(creds, projectTarget.getRegion(), bucketName, outDir);
            log.info("프론트엔드 배포 완료: bucketName={}", bucketName);
            log.info("==================== Frontend Deployment End ====================");
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
