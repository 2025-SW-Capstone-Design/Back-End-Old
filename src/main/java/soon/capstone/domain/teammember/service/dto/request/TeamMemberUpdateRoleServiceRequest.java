package soon.capstone.domain.teammember.service.dto.request;

import lombok.Builder;

@Builder
public record TeamMemberUpdateRoleServiceRequest(

    Long teamId,
    Long teamMemberId,
    String role

) {
}