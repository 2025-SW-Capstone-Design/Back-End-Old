package soon.capstone.domain.issue.controller.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import soon.capstone.domain.issue.service.dto.request.IssueTemplateCreateServiceRequest;

@Builder
public record IssueTemplateCreateRequest(

    @NotBlank(message = "제목은 필수 입력 값입니다.")
    String title,

    @NotBlank(message = "설명은 필수 입력 값입니다.")
    String description,

    @NotBlank(message = "내용은 필수 입력 값입니다.")
    String content,

    @NotBlank(message = "타입은 필수 입력 값입니다.")
    String type,

    @NotNull(message = "프로젝트 ID는 필수 입력 값입니다.")
    @Positive(message = "프로젝트 ID는 0보다 커야합니다.")
    Long projectId

) {

    public IssueTemplateCreateServiceRequest toServiceRequest(Long teamId) {
        return IssueTemplateCreateServiceRequest.builder()
            .title(title)
            .description(description)
            .content(content)
            .type(type)
            .projectId(projectId)
            .teamId(teamId)
            .build();
    }

}