package soon.capstone.domain.issue.controller.dto;

import lombok.Builder;
import soon.capstone.domain.issue.service.dto.request.IssueUpdateStatusServiceRequest;

import java.util.List;

@Builder
public record IssueUpdateStatusRequest(

    String organizationName,
    String repositoryName,
    String status,
    List<String> labels

) {


    public IssueUpdateStatusServiceRequest toServiceRequest(Long memberId, Long teamId, Long issueId) {
        return IssueUpdateStatusServiceRequest.builder()
            .memberId(memberId)
            .teamId(teamId)
            .labels(labels)
            .issueId(issueId)
            .organizationName(organizationName)
            .repositoryName(repositoryName)
            .status(status)
            .build();
    }

}