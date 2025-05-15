package soon.capstone.domain.chatroom.service.dto.request;

import lombok.Builder;

@Builder
public record ChatRoomCreateServiceRequest(

    String title,
    Long teamId,
    Long memberId,
    String sid

) {
}