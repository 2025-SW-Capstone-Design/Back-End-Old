package soon.capstone.domain.milestone.service.dto.request;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record MilestoneCreateServiceRequest(
        Long teamId,
        Long projectId,
        String title,
        String description,
        LocalDateTime startDate,
        LocalDateTime dueDate
) {
}
