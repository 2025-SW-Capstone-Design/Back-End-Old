package soon.capstone.domain.chatroom.service.dto.request;

import lombok.Builder;

@Builder
public record ChatRoomFinishServiceRequest(

    String sid,
    Long teamId,
    Long memberId

) {
}