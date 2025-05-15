package soon.capstone.domain.issue.controller.dto;

import lombok.Builder;
import soon.capstone.domain.issue.service.dto.request.IssueUpdateStatusServiceRequest;

@Builder
public record IssueUpdateStatusRequest(

    String organizationName,
    String repositoryName,
    String status

) {


    public IssueUpdateStatusServiceRequest toServiceRequest(Long memberId, Long teamId, Long issueId) {
        return IssueUpdateStatusServiceRequest.builder()
            .memberId(memberId)
            .teamId(teamId)
            .issueId(issueId)
            .organizationName(organizationName)
            .repositoryName(repositoryName)
            .status(status)
            .build();
    }

}