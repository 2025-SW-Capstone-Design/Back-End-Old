package soon.capstone.domain.readme.controller.dto.request;

import soon.capstone.domain.readme.service.dto.request.ReadmeDeleteServiceRequest;

public record ReadmeDeleteRequest(
) {

    public static ReadmeDeleteServiceRequest toServiceRequest(Long memberId, Long readmeId, Long teamId) {
        return ReadmeDeleteServiceRequest.builder()
            .memberId(memberId)
            .readmeId(readmeId)
            .teamId(teamId)
            .build();
    }

}