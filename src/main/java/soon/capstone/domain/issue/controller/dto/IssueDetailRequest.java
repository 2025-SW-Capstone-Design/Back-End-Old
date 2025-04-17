package soon.capstone.domain.issue.controller.dto;

import soon.capstone.domain.issue.service.dto.request.IssueDetailServiceRequest;

public record IssueDetailRequest(
) {

    public static IssueDetailServiceRequest toServiceRequest(Long memberId, Long teamId, Long issueId, Long projectId) {
        return IssueDetailServiceRequest.builder()
            .memberId(memberId)
            .teamId(teamId)
            .issueId(issueId)
            .projectId(projectId)
            .build();
    }

}