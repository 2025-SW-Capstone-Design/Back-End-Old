package soon.capstone.domain.team.service.dto.request;

import lombok.Builder;

import java.util.List;

@Builder
public record TeamInvitationServiceRequest(

    Long teamId,
    List<String> emails

) {
}