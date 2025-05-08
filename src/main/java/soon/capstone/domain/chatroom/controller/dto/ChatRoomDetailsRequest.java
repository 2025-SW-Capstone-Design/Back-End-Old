package soon.capstone.domain.chatroom.controller.dto;

import lombok.Builder;
import soon.capstone.domain.chatroom.service.dto.request.ChatRoomDetailsServiceRequest;

@Builder
public record ChatRoomDetailsRequest(

    Long memberId,
    Long teamId

) {

    public static ChatRoomDetailsServiceRequest toServiceRequest(Long memberId, Long teamId) {
        return ChatRoomDetailsServiceRequest.builder()
            .memberId(memberId)
            .teamId(teamId)
            .build();
    }

}