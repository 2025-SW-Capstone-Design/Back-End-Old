package soon.capstone.domain.issue.controller.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import soon.capstone.domain.issue.service.dto.request.IssueLabelUpdateServiceRequest;

@Builder
public record IssueLabelUpdateRequest(

    @NotBlank(message = "기존 라벨명은 필수 입력 값입니다.")
    String oldTitle,

    @NotBlank(message = "새 라벨명은 필수 입력 값입니다.")
    String newTitle,

    @NotBlank(message = "라벨 설명은 필수 입력 값입니다.")
    String description,

    @NotBlank(message = "라벨 색상은 필수 입력 값입니다.")
    String color,

    @NotBlank(message = "오가니제이션명은 필수 입력 값입니다.")
    String organizationName,

    @NotBlank(message = "리포지토리명은 필수 입력 값입니다.")
    String repositoryName,

    @NotNull(message = "프로젝트 ID는 필수 입력 값입니다.")
    @Positive(message = "프로젝트 ID는 1 이상의 값이어야 합니다.")
    Long projectId

) {

    public IssueLabelUpdateServiceRequest toServiceRequest(Long teamId, Long labelId) {
        return IssueLabelUpdateServiceRequest.builder()
            .oldTitle(oldTitle)
            .newTitle(newTitle)
            .description(description)
            .color(color)
            .organizationName(organizationName)
            .repositoryName(repositoryName)
            .projectId(projectId)
            .teamId(teamId)
            .labelId(labelId)
            .build();
    }

}