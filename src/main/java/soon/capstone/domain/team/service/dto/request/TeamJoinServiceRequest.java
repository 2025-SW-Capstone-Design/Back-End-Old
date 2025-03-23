package soon.capstone.domain.team.service.dto.request;

import lombok.Builder;

@Builder
public record TeamJoinServiceRequest(

    String invitationCode

) {

}