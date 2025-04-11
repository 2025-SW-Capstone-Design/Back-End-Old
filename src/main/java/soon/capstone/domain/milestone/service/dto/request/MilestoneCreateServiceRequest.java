package soon.capstone.domain.milestone.service.dto.request;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record MilestoneCreateServiceRequest(
        String title,
        String description,
        LocalDateTime startDate,
        LocalDateTime dueDate
) {
}
