package soon.capstone.domain.milestone.controller.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import soon.capstone.domain.milestone.service.dto.request.MilestoneStatusUpdateServiceRequest;

@Builder
public record MilestoneStatusUpdateRequest(

    @NotBlank(message = "상태를 입력해주세요.")
    String status

) {

    public MilestoneStatusUpdateServiceRequest toServiceRequest(Long memberId, Long teamId, Long projectId, Long milestoneId) {
        return MilestoneStatusUpdateServiceRequest.builder()
            .milestoneId(milestoneId)
            .status(status)
            .projectId(projectId)
            .memberId(memberId)
            .teamId(teamId)
            .build();
    }

}