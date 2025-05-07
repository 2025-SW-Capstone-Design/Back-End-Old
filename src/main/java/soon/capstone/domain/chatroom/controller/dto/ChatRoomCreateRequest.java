package soon.capstone.domain.chatroom.controller.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import soon.capstone.domain.chatroom.service.dto.request.ChatRoomCreateServiceRequest;

import java.time.LocalDateTime;

@Builder
public record ChatRoomCreateRequest(

    @NotBlank(message = "채팅방 제목은 필수입니다.")
    String title,

    @NotNull(message = "예약 시간은 필수입니다.")
    @Future(message = "예약 시간은 현재 시간보다 미래여야 합니다.")
    LocalDateTime reservedAt,

    @NotBlank(message = "채팅방 SID는 필수입니다.")
    String sid

) {

    public ChatRoomCreateServiceRequest toServiceRequest(Long memberId, Long teamId) {
        return ChatRoomCreateServiceRequest.builder()
            .title(title)
            .reservedAt(reservedAt)
            .teamId(teamId)
            .memberId(memberId)
            .sid(sid)
            .build();
    }

}