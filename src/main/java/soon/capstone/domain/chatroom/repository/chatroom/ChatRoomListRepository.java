package soon.capstone.domain.chatroom.repository.chatroom;

import soon.capstone.domain.chatroom.entity.ChatRoom;
import soon.capstone.domain.teammember.service.dto.response.TeamMemberDetailResponse;

import java.util.List;

public interface ChatRoomListRepository {

    List<TeamMemberDetailResponse> getTeamMembersByChatRoom(ChatRoom chatRoom);

}
