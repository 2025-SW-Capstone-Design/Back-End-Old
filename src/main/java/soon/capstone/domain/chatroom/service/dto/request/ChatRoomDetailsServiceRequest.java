package soon.capstone.domain.chatroom.service.dto.request;

import lombok.Builder;

@Builder
public record ChatRoomDetailsServiceRequest(

    Long teamId,
    Long memberId

) {
}