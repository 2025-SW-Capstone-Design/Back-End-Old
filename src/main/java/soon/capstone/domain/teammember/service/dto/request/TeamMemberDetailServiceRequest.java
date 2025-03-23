package soon.capstone.domain.teammember.service.dto.request;

import lombok.Builder;

@Builder
public record TeamMemberDetailServiceRequest(

    Long teamId

) {
}