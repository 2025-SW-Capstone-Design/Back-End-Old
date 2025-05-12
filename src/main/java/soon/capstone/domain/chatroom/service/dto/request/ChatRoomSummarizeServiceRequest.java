package soon.capstone.domain.chatroom.service.dto.request;

import lombok.Builder;

@Builder
public record ChatRoomSummarizeServiceRequest(

    Long teamId,
    Long memberId,
    Long chatRoomId,
    boolean isFinal,
    String text

) {
}