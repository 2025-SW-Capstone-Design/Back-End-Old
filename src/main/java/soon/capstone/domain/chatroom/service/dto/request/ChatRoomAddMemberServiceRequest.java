package soon.capstone.domain.chatroom.service.dto.request;

import lombok.Builder;

@Builder
public record ChatRoomAddMemberServiceRequest(

    Long chatRoomId,
    Long teamId,
    Long memberId

) {
}