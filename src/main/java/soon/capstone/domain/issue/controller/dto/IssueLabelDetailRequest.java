package soon.capstone.domain.issue.controller.dto;

import soon.capstone.domain.issue.service.dto.request.IssueLabelDetailServiceRequest;

public record IssueLabelDetailRequest() {

    public static IssueLabelDetailServiceRequest toServiceRequest(Long teamId, Long projectId) {
        return IssueLabelDetailServiceRequest.builder()
            .projectId(projectId)
            .teamId(teamId)
            .build();
    }

}