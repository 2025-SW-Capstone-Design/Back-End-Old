package soon.capstone.domain.issue.service.dto.request;

import lombok.Builder;

@Builder
public record IssueDetailServiceRequest(

    Long memberId,
    Long issueId,
    Long teamId,
    Long projectId

) {
}