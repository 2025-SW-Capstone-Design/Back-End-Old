package soon.capstone.domain.chatroom.service.dto.response;

import lombok.Builder;
import soon.capstone.domain.chatroom.entity.ChatRoom;

@Builder
public record ChatRoomDetailsResponse(

    Long id,
    String title,
    boolean active

) {

    public static ChatRoomDetailsResponse from(ChatRoom chatRoom) {
        return ChatRoomDetailsResponse.builder()
            .id(chatRoom.getId())
            .title(chatRoom.getTitle())
            .active(chatRoom.isActive())
            .build();
    }

}