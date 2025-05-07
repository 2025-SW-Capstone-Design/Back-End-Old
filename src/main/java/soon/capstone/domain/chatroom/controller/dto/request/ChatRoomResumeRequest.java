package soon.capstone.domain.chatroom.controller.dto.request;

import lombok.Builder;
import soon.capstone.domain.chatroom.service.dto.request.ChatRoomResumeServiceRequest;

@Builder
public record ChatRoomResumeRequest() {

    public static ChatRoomResumeServiceRequest toServiceRequest(Long memberId, Long teamId, Long chatRoomId) {
        return ChatRoomResumeServiceRequest.builder()
            .memberId(memberId)
            .teamId(teamId)
            .chatRoomId(chatRoomId)
            .build();
    }

}