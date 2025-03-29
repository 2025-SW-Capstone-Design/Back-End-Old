package soon.capstone.domain.issue.controller.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import soon.capstone.domain.issue.service.dto.request.IssueLabelCreateServiceRequest;

@Builder
public record IssueLabelCreateRequest(

    @NotBlank(message = "제목은 필수 입력 값입니다.")
    String title,

    @NotBlank(message = "설명은 필수 입력 값입니다.")
    String description,

    @NotBlank(message = "색상은 필수 입력 값입니다.")
    String color

) {

    public IssueLabelCreateServiceRequest toServiceRequest(Long teamId) {
        return IssueLabelCreateServiceRequest.builder()
            .title(title())
            .description(description())
            .color(color())
            .teamId(teamId)
            .build();
    }

}