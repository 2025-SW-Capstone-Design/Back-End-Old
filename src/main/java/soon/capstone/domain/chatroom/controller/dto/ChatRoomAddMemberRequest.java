package soon.capstone.domain.chatroom.controller.dto;

import lombok.Builder;
import soon.capstone.domain.chatroom.service.dto.request.ChatRoomAddMemberServiceRequest;

@Builder
public record ChatRoomAddMemberRequest() {

    public static ChatRoomAddMemberServiceRequest toServiceRequest(Long memberId, Long teamId, Long chatRoomId) {
        return ChatRoomAddMemberServiceRequest.builder()
            .memberId(memberId)
            .chatRoomId(chatRoomId)
            .teamId(teamId)
            .build();
    }

}