package soon.capstone.domain.readme.controller.dto.request;

import soon.capstone.domain.readme.service.dto.request.ReadmeDetailServiceRequest;

public record ReadmeDetailRequest() {

    public static ReadmeDetailServiceRequest toServiceRequest(
        Long memberId,
        Long readmeId,
        Long teamId
    ) {
        return ReadmeDetailServiceRequest.builder()
            .memberId(memberId)
            .readmeId(readmeId)
            .teamId(teamId)
            .build();
    }

}