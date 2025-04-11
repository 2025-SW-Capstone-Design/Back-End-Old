package soon.capstone.domain.milestone.controller.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import soon.capstone.domain.milestone.service.dto.request.MilestoneCreateServiceRequest;

import java.time.LocalDateTime;
@Builder
public record MilestoneCreateRequest(
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
                .title(title)
                .description(description)
                .startDate(startDate)
                .dueDate(dueDate)
                .build();
    }
}
