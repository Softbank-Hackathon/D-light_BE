package com.hackathon.melon.domain.github.service;

import com.hackathon.melon.domain.github.dto.BranchDto;
import com.hackathon.melon.domain.github.dto.RepositoryDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.web.reactive.function.client.ServletOAuth2AuthorizedClientExchangeFilterFunction;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

/**
 * GitHub API 호출 서비스 (WebClient + OAuth2 자동 토큰 관리)
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class GitHubService {

    private static final String GITHUB_API_BASE_URL = "https://api.github.com";

    private final WebClient webClient;

    /**
     * 현재 로그인한 사용자의 GitHub 레포지토리 목록 조회
     *
     * @param authorizedClient OAuth2 인증 클라이언트
     * @return 레포지토리 목록
     */
    public List<RepositoryDto> getUserRepositories(OAuth2AuthorizedClient authorizedClient) {
        log.info("GitHub API 호출: 사용자 레포지토리 목록 조회");

        return webClient.get()
                .uri(GITHUB_API_BASE_URL + "/user/repos?per_page=100&sort=updated")
                .headers(headers -> headers.set("Accept", "application/vnd.github.v3+json"))
                .attributes(ServletOAuth2AuthorizedClientExchangeFilterFunction
                        .oauth2AuthorizedClient(authorizedClient))
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<RepositoryDto>>() {})
                .block();
    }

    /**
     * 특정 레포지토리의 브랜치 목록 조회
     *
     * @param authorizedClient OAuth2 인증 클라이언트
     * @param owner 레포지토리 소유자
     * @param repo 레포지토리 이름
     * @return 브랜치 목록
     */
    public List<BranchDto> getRepositoryBranches(OAuth2AuthorizedClient authorizedClient, String owner, String repo) {
        log.info("GitHub API 호출: 레포지토리 브랜치 목록 조회 - {}/{}", owner, repo);

        String url = String.format("%s/repos/%s/%s/branches", GITHUB_API_BASE_URL, owner, repo);

        return webClient.get()
                .uri(url)
                .headers(headers -> headers.set("Accept", "application/vnd.github.v3+json"))
                .attributes(ServletOAuth2AuthorizedClientExchangeFilterFunction
                        .oauth2AuthorizedClient(authorizedClient))
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<BranchDto>>() {})
                .block();
    }
}