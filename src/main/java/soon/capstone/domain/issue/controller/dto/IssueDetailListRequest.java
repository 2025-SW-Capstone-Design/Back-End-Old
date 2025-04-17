package soon.capstone.domain.issue.controller.dto;

import lombok.Builder;
import soon.capstone.domain.issue.service.dto.request.IssueDetailListServiceRequest;

@Builder
public record IssueDetailListRequest(
) {

    public static IssueDetailListServiceRequest toServiceRequest(Long memberId, Long teamId, Long projectId, String scope) {
        return IssueDetailListServiceRequest.builder()
            .memberId(memberId)
            .teamId(teamId)
            .projectId(projectId)
            .scope(scope)
            .build();
    }

}