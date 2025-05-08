package soon.capstone.domain.chatroom.controller.dto;

import lombok.Builder;
import soon.capstone.domain.chatroom.service.dto.request.ChatRoomTeamMembersDetailServiceRequest;

@Builder
public record ChatRoomTeamMembersDetailRequest() {

    public static ChatRoomTeamMembersDetailServiceRequest toServiceRequest(Long teamId, Long chatRoomId) {
        return ChatRoomTeamMembersDetailServiceRequest.builder()
            .chatRoomId(chatRoomId)
            .teamId(teamId)
            .build();
    }

}