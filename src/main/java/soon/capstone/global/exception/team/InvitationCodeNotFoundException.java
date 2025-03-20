package soon.capstone.global.exception.team;

import lombok.Getter;
import soon.capstone.global.exception.RootException;
import soon.capstone.global.exception.dto.ErrorDetail;

@Getter
public class InvitationCodeNotFoundException extends RootException {

    public InvitationCodeNotFoundException() {
        super(ErrorDetail.INVITATION_CODE_NOT_FOUND);
    }

    public InvitationCodeNotFoundException(Throwable cause) {
        super(ErrorDetail.INVITATION_CODE_NOT_FOUND, cause);
    }

}