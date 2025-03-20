package soon.capstone.domain.team.controller.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import soon.capstone.domain.team.service.dto.request.TeamGenerateInvitationCodeServiceRequest;

@Builder
public record TeamGenerateInvitationCodeRequest(

    @Positive(message = "팀 ID는 0보다 커야 합니다.")
    @NotNull(message = "팀 ID는 필수 값입니다.")
    Long teamId

) {

    public TeamGenerateInvitationCodeServiceRequest toServiceRequest() {
        return TeamGenerateInvitationCodeServiceRequest.builder()
            .teamId(teamId)
            .build();
    }

}