package soon.capstone.domain.teammember.service.dto.request;

import lombok.Builder;

@Builder
public record TeamMemberUpdatePositionServiceRequest(

    Long teamId,
    Long memberId,
    Long requesterId,
    String position

) {
}