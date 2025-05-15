package soon.capstone.domain.issue.service.dto.request;

import lombok.Builder;

@Builder
public record IssueUpdateStatusServiceRequest(

    Long memberId,
    Long teamId,
    Long issueId,
    String organizationName,
    String repositoryName,
    String status

) {
}