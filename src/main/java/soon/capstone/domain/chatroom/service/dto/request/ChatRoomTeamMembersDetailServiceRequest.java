package soon.capstone.domain.chatroom.service.dto.request;

import lombok.Builder;

@Builder
public record ChatRoomTeamMembersDetailServiceRequest(

    Long chatRoomId,
    Long teamId

) {
}