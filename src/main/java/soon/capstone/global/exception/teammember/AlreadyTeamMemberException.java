package soon.capstone.global.exception.teammember;

import soon.capstone.global.exception.RootException;
import soon.capstone.global.exception.dto.ErrorDetail;

public class AlreadyTeamMemberException extends RootException {

    public AlreadyTeamMemberException() {
        super(ErrorDetail.TEAM_MEMBER_ALREADY_EXISTS);
    }

    public AlreadyTeamMemberException(Throwable cause) {
        super(ErrorDetail.TEAM_MEMBER_ALREADY_EXISTS, cause);
    }


}