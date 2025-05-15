package soon.capstone.global.exception.chatroom;

import lombok.Getter;
import soon.capstone.global.exception.RootException;
import soon.capstone.global.exception.dto.ErrorDetail;

@Getter
public class TemporaryRoomIdentityNotFoundException extends RootException {

    public TemporaryRoomIdentityNotFoundException() {
        super(ErrorDetail.TEMPORARY_ROOM_IDENTITY_NOT_FOUND);
    }

    public TemporaryRoomIdentityNotFoundException(Throwable cause) {
        super(ErrorDetail.TEMPORARY_ROOM_IDENTITY_NOT_FOUND, cause);
    }

}