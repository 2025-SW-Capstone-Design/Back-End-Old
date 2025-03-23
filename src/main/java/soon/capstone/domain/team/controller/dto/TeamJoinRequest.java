package soon.capstone.domain.team.controller.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;
import soon.capstone.domain.team.service.dto.request.TeamJoinServiceRequest;

@Builder
public record TeamJoinRequest(

    @NotEmpty(message = "팀 초대 코드는 필수입니다.")
    String invitationCode

) {

    public TeamJoinServiceRequest toServiceRequest() {
        return TeamJoinServiceRequest.builder()
            .invitationCode(this.invitationCode)
            .build();
    }

}