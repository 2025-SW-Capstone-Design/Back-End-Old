package soon.capstone.domain.chatroom.controller.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import soon.capstone.domain.chatroom.service.dto.request.ChatRoomCreateServiceRequest;

@Builder
public record ChatRoomCreateRequest(

    @NotBlank(message = "채팅방 제목은 필수입니다.")
    String title,

    @NotBlank(message = "채팅방 SID는 필수입니다.")
    String sid

) {

    public ChatRoomCreateServiceRequest toServiceRequest(Long memberId, Long teamId) {
        return ChatRoomCreateServiceRequest.builder()
            .title(title)
            .teamId(teamId)
            .memberId(memberId)
            .sid(sid)
            .build();
    }

}