package soon.capstone.domain.teammember.controller.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import soon.capstone.domain.teammember.service.dto.request.TeamMemberUpdateRoleServiceRequest;

@Builder
public record TeamMemberUpdateRoleRequest(

    @Positive(message = "멤버 ID는 0보다 커야 합니다.")
    @NotNull(message = "멤버 ID는 필수 값입니다.")
    Long memberId,

    @NotEmpty(message = "권한은 필수 값입니다.")
    String role

) {

    public TeamMemberUpdateRoleServiceRequest toServiceRequest(Long teamId, Long requesterId) {
        return TeamMemberUpdateRoleServiceRequest.builder()
            .teamId(teamId)
            .requesterId(requesterId)
            .memberId(memberId)
            .role(role)
            .build();
    }

}