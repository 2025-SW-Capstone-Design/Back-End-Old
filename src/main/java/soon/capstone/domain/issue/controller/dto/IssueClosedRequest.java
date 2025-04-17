package soon.capstone.domain.issue.controller.dto;

import lombok.Builder;
import soon.capstone.domain.issue.service.dto.request.IssueClosedServiceRequest;

@Builder
public record IssueClosedRequest(

    String organizationName,
    String repositoryName

) {


    public IssueClosedServiceRequest toServiceRequest(Long memberId, Long teamId, Long issueId) {
        return IssueClosedServiceRequest.builder()
            .memberId(memberId)
            .teamId(teamId)
            .issueId(issueId)
            .organizationName(organizationName)
            .repositoryName(repositoryName)
            .build();
    }

}