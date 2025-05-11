package soon.capstone.domain.milestone.service.dto.request;

import lombok.Builder;

@Builder
public record MilestoneStatusUpdateServiceRequest(

    String status,
    Long memberId,
    Long teamId,
    Long projectId,
    Long milestoneId

) {
}