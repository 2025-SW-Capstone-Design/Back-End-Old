package soon.capstone.domain.issue.controller.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import soon.capstone.domain.issue.service.dto.request.IssueLabelCreateServiceRequest;

@Builder
public record IssueLabelCreateRequest(

    @NotBlank(message = "제목은 필수 입력 값입니다.")
    String title,

    @NotBlank(message = "설명은 필수 입력 값입니다.")
    String description,

    @NotBlank(message = "색상은 필수 입력 값입니다.")
    String color,

    @NotNull(message = "프로젝트 ID는 필수 입력 값입니다.")
    @Positive(message = "프로젝트 ID는 1 이상의 값이어야 합니다.")
    Long projectId

) {

    public IssueLabelCreateServiceRequest toServiceRequest(Long teamId) {
        return IssueLabelCreateServiceRequest.builder()
            .title(title())
            .description(description())
            .projectId(projectId())
            .color(color())
            .teamId(teamId)
            .build();
    }

}