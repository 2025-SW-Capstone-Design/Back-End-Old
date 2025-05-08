package soon.capstone.domain.chatroom.service.dto.request;

import lombok.Builder;

@Builder
public record ChatRoomGetMembersServiceRequest(

    Long chatRoomId,
    Long teamId,
    Long memberId

) {
}