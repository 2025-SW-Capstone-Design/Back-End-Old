package soon.capstone.global.exception.chatroom;

import lombok.Getter;
import soon.capstone.global.exception.RootException;
import soon.capstone.global.exception.dto.ErrorDetail;

@Getter
public class ChatRoomNotFoundException extends RootException {

    public ChatRoomNotFoundException() {
        super(ErrorDetail.CHAT_ROOM_NOT_FOUND);
    }

    public ChatRoomNotFoundException(Throwable cause) {
        super(ErrorDetail.CHAT_ROOM_NOT_FOUND, cause);
    }

}