package soon.capstone.domain.milestone.controller.dto.request;

import lombok.Builder;
import soon.capstone.domain.milestone.service.dto.request.MilestoneUpdateServiceRequest;

import java.time.LocalDateTime;

@Builder
public record MilestoneUpdateRequest(
        String title,
        String description,
        LocalDateTime startDate,
        LocalDateTime dueDate
) {
    public MilestoneUpdateServiceRequest toServiceRequest() {
        return MilestoneUpdateServiceRequest.builder()
                .title(title)
                .description(description)
                .startDate(startDate)
                .dueDate(dueDate)
                .build();
    }
}
