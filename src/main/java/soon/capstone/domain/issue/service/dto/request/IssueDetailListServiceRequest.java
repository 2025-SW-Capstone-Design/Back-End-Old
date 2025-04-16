package soon.capstone.domain.issue.service.dto.request;

import lombok.Builder;

@Builder
public record IssueDetailListServiceRequest(

    Long memberId,
    Long teamId,
    Long projectId,
    String scope

) {
}