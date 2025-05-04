package soon.capstone.global.exception.chatroom;

import lombok.Getter;
import soon.capstone.global.exception.RootException;
import soon.capstone.global.exception.dto.ErrorDetail;

@Getter
public class ChatRoomTeamMemberNotFoundException extends RootException {

    public ChatRoomTeamMemberNotFoundException() {
        super(ErrorDetail.CHAT_ROOM_TEAM_MEMBER_NOT_FOUND);
    }

    public ChatRoomTeamMemberNotFoundException(Throwable cause) {
        super(ErrorDetail.CHAT_ROOM_TEAM_MEMBER_NOT_FOUND, cause);
    }

}