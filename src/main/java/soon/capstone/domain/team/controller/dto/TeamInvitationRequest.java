package soon.capstone.domain.team.controller.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import soon.capstone.domain.team.service.dto.request.TeamInvitationServiceRequest;

import java.util.List;

@Builder
public record TeamInvitationRequest(

    @NotNull(message = "이메일 목록은 필수입니다.")
    @Size(min = 1, message = "최소 한 개 이상의 이메일을 입력해야 합니다.")
    List<@Email(message = "유효한 이메일 형식이어야 합니다.") String> emails

) {

    public TeamInvitationServiceRequest toServiceRequest(Long teamId) {
        return TeamInvitationServiceRequest.builder()
            .teamId(teamId)
            .emails(emails)
            .build();
    }

}