package soon.capstone.global.exception.chatroom;

import lombok.Getter;
import soon.capstone.global.exception.RootException;
import soon.capstone.global.exception.dto.ErrorDetail;

@Getter
public class ChatRoomAlreadyExistsForTeamException extends RootException {

    public ChatRoomAlreadyExistsForTeamException() {
        super(ErrorDetail.CHAT_ROOM_ALREADY_EXISTS_FOR_TEAM);
    }

    public ChatRoomAlreadyExistsForTeamException(Throwable cause) {
        super(ErrorDetail.CHAT_ROOM_ALREADY_EXISTS_FOR_TEAM, cause);
    }

}