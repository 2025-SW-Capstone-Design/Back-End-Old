package soon.capstone.domain.chatroom.service.dto.request;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record ChatRoomCreateServiceRequest(

    String title,
    LocalDateTime reservedAt,
    Long teamId,
    String sid

) {
}