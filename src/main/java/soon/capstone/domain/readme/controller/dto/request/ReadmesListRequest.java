package soon.capstone.domain.readme.controller.dto.request;

import soon.capstone.domain.readme.service.dto.request.ReadmesListServiceRequest;

public record ReadmesListRequest() {

    public static ReadmesListServiceRequest toServiceRequest(
        Long memberId,
        Long projectId,
        Long teamId
    ) {
        return ReadmesListServiceRequest.builder()
            .memberId(memberId)
            .projectId(projectId)
            .teamId(teamId)
            .build();
    }

}