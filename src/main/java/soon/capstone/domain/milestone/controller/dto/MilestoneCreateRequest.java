package soon.capstone.domain.milestone.controller.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import soon.capstone.domain.milestone.service.dto.request.MilestoneCreateServiceRequest;

import java.time.LocalDateTime;
@Builder
public record MilestoneCreateRequest(
        @NotNull(message = "팀 아이디를 입력해주세요.")
        Long teamId,

        @NotNull(message = "프로젝트 아이디를 입력해주세요.")
        Long projectId,

        @NotBlank(message = "마일스톤 이름을 입력해주세요.")
        String title,

        @NotBlank(message = "마일스톤 설명을 입력해주세요.")
        String description,

        @NotNull(message = "마일스톤 시작일을 입력해주세요.")
        LocalDateTime startDate,

        @NotNull(message = "마일스톤 마감일을 입력해주세요.")
        LocalDateTime dueDate
) {
    public MilestoneCreateServiceRequest toServiceRequest() {
        return MilestoneCreateServiceRequest.builder()
                .teamId(teamId)
                .projectId(projectId)
                .title(title)
                .description(description)
                .startDate(startDate)
                .dueDate(dueDate)
                .build();
    }
}
