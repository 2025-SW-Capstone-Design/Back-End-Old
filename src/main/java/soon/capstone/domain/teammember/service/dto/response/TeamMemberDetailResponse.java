package soon.capstone.domain.teammember.service.dto.response;

import lombok.Builder;

@Builder
public record TeamMemberDetailResponse(

    Long memberId,
    String position,
    String role,
    String nickname,
    String profileImageURL

) {

}