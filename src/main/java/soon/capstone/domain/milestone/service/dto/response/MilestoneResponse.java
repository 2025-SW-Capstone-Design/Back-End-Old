package soon.capstone.domain.milestone.service.dto.response;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record MilestoneResponse(
        Long milestoneId,
        String title,
        String description,
        String creator,
        LocalDateTime dueDate,
        LocalDateTime startDate,
        boolean isCompleted
) {
}
