package soon.capstone.domain.chatroom.controller.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import soon.capstone.domain.chatroom.service.dto.request.ChatRoomSummarizeServiceRequest;

@Builder
public record ChatRoomSummarizeRequest(

    @NotNull(message = "isFinal은 필수입니다.")
    boolean isFinal,

    @NotEmpty(message = "text는 필수입니다.")
    String text

) {

    public ChatRoomSummarizeServiceRequest toServiceRequest(Long teamId, Long memberId, Long chatRoomId) {
        return ChatRoomSummarizeServiceRequest.builder()
            .isFinal(isFinal)
            .teamId(teamId)
            .memberId(memberId)
            .chatRoomId(chatRoomId)
            .text(text)
            .build();
    }

}