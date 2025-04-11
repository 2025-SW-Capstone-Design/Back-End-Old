package soon.capstone.domain.milestone.service.dto.response;

import lombok.Builder;
import soon.capstone.domain.issue.service.dto.response.IssueDetailResponse;

import java.time.LocalDateTime;
import java.util.List;

@Builder
public record MilestoneDetailResponse(
        Long milestoneId,
        String title,
        String description,
        String creator,
        LocalDateTime dueDate,
        LocalDateTime startDate,
        boolean isCompleted,
        List<IssueDetailResponse> issues
) {
}
