package soon.capstone.domain.teammember.controller.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import soon.capstone.domain.teammember.service.dto.request.TeamMemberUpdatePositionServiceRequest;

@Builder
public record TeamMemberUpdatePositionRequest(

    @Positive(message = "팀 ID는 양수여야 합니다.")
    @NotNull(message = "팀원 ID는 필수입니다.")
    Long teamMemberId,

    @NotBlank(message = "팀원 포지션은 필수입니다.")
    String position

) {

    public TeamMemberUpdatePositionServiceRequest toServiceRequest(Long teamId, Long memberId) {
        return TeamMemberUpdatePositionServiceRequest.builder()
            .teamId(teamId)
            .teamMemberId(teamMemberId)
            .position(position)
            .memberId(memberId)
            .build();
    }

}