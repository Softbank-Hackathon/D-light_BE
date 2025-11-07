package com.hackathon.melon.domain.project.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 프로젝트 생성 요청 DTO
 */
@Getter
@NoArgsConstructor
public class ProjectRequest {

    @NotBlank(message = "프로젝트 이름은 필수 입력 항목입니다.")
    @Schema(description = "프로젝트 이름", example = "my-awesome-project")
    private String name;
}