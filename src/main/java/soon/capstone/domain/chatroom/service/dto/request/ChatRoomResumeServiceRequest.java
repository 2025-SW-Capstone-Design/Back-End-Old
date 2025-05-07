package soon.capstone.domain.chatroom.service.dto.request;

import lombok.Builder;

@Builder
public record ChatRoomResumeServiceRequest(

    Long teamId,
    Long memberId,
    Long chatRoomId

) {
}