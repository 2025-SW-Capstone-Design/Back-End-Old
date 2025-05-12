package soon.capstone.domain.member.service.dto.response;

import lombok.Builder;
import soon.capstone.domain.member.entity.Member;

@Builder
public record MemberDetailResponse(

    Long id,
    String email,
    String nickname,
    String profileImageURL

) {

    public static MemberDetailResponse from(Member member) {
        return MemberDetailResponse.builder()
            .id(member.getId())
            .email(member.getEmail())
            .nickname(member.getNickname())
            .profileImageURL(member.getProfileImageURL())
            .build();
    }

}