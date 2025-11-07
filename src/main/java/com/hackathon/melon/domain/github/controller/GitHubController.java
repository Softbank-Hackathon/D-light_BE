package com.hackathon.melon.domain.github.controller;

import com.hackathon.melon.domain.github.dto.BranchDto;
import com.hackathon.melon.domain.github.dto.RepositoryDto;
import com.hackathon.melon.domain.github.service.GitHubService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.annotation.RegisteredOAuth2AuthorizedClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * GitHub API 연동 컨트롤러
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/github")
@RequiredArgsConstructor
public class GitHubController {

    private final GitHubService gitHubService;

    /**
     * 현재 로그인한 사용자의 GitHub 레포지토리 목록 조회
     *
     * @param authorizedClient OAuth2 인증 클라이언트
     * @return 레포지토리 목록
     */
    @GetMapping("/repos")
    public ResponseEntity<List<RepositoryDto>> getUserRepositories(
            @RegisteredOAuth2AuthorizedClient("github") OAuth2AuthorizedClient authorizedClient) {

        if (authorizedClient == null) {
            log.warn("인증되지 않은 사용자의 레포지토리 조회 시도");
            return ResponseEntity.status(401).build();
        }

        log.info("사용자 레포지토리 목록 조회: principalName = {}", authorizedClient.getPrincipalName());

        try {
            List<RepositoryDto> repositories = gitHubService.getUserRepositories(authorizedClient);
            return ResponseEntity.ok(repositories);
        } catch (Exception e) {
            log.error("레포지토리 목록 조회 실패", e);
            return ResponseEntity.status(500).build();
        }
    }

    /**
     * 특정 레포지토리의 브랜치 목록 조회
     *
     * @param authorizedClient OAuth2 인증 클라이언트
     * @param owner 레포지토리 소유자
     * @param repo 레포지토리 이름
     * @return 브랜치 목록
     */
    @GetMapping("/repos/{owner}/{repo}/branches")
    public ResponseEntity<List<BranchDto>> getRepositoryBranches(
            @RegisteredOAuth2AuthorizedClient("github") OAuth2AuthorizedClient authorizedClient,
            @PathVariable String owner,
            @PathVariable String repo) {

        if (authorizedClient == null) {
            log.warn("인증되지 않은 사용자의 브랜치 조회 시도");
            return ResponseEntity.status(401).build();
        }

        log.info("레포지토리 브랜치 목록 조회: principalName = {}, owner = {}, repo = {}",
                authorizedClient.getPrincipalName(), owner, repo);

        try {
            List<BranchDto> branches = gitHubService.getRepositoryBranches(authorizedClient, owner, repo);
            return ResponseEntity.ok(branches);
        } catch (Exception e) {
            log.error("브랜치 목록 조회 실패: {}/{}", owner, repo, e);
            return ResponseEntity.status(500).build();
        }
    }
}
