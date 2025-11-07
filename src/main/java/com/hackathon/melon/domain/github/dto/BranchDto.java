package com.hackathon.melon.domain.github.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * GitHub Branch 정보 DTO
 */
@Data
public class BranchDto {

    private String name;

    private Commit commit;

    @JsonProperty("protected")
    private Boolean isProtected;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    /**
     * Branch의 Commit 정보
     */
    @Data
    public static class Commit {
        private String sha;
        private String url;
    }
}
