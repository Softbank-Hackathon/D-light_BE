package com.hackathon.melon.domain.github.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * GitHub Repository 정보 DTO
 */
@Data
public class RepositoryDto {

    private Long id;

    private String name;

    @JsonProperty("full_name")
    private String fullName;

    private String description;

    @JsonProperty("private")
    private Boolean isPrivate;

    @JsonProperty("html_url")
    private String htmlUrl;

    @JsonProperty("clone_url")
    private String cloneUrl;

    @JsonProperty("ssh_url")
    private String sshUrl;

    @JsonProperty("default_branch")
    private String defaultBranch;

    private Owner owner;

    @JsonProperty("pushed_at")
    private String pushedAt;

    private String language;

    @JsonProperty("stargazers_count")
    private Integer stargazersCount;

    @JsonProperty("watchers_count")
    private Integer watchersCount;

    @JsonProperty("forks_count")
    private Integer forksCount;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    /**
     * Repository Owner 정보
     */
    @Data
    public static class Owner {
        private String login;

        @JsonProperty("avatar_url")
        private String avatarUrl;

        @JsonProperty("html_url")
        private String htmlUrl;
    }
}
