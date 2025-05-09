package soon.capstone.domain.milestone.service.dto.response;

import lombok.Builder;
import soon.capstone.domain.issue.service.dto.response.IssueDetailResponse;

import java.util.List;

@Builder
public record MilestoneIssueResponse(

    MilestoneResponse milestone,
    List<IssueDetailResponse> issues

) {
}