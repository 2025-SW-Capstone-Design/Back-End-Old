package soon.capstone.domain.readme.controller.dto.request;

import soon.capstone.domain.readme.service.dto.request.ReadmeRollbackServiceRequest;

public record ReadmeRollbackRequest() {

    public static ReadmeRollbackServiceRequest toServiceRequest(Long memberId, Long readmeId, Long projectId, Long teamId) {
        return ReadmeRollbackServiceRequest.builder()
            .memberId(memberId)
            .readmeId(readmeId)
            .projectId(projectId)
            .teamId(teamId)
            .build();
    }

}