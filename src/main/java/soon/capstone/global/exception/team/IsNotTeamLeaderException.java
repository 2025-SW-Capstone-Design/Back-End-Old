package soon.capstone.global.exception.team;

import soon.capstone.global.exception.RootException;
import soon.capstone.global.exception.dto.ErrorDetail;

public class IsNotTeamLeaderException extends RootException {

    public IsNotTeamLeaderException() {
        super(ErrorDetail.IS_NOT_TEAM_LEADER);
    }

    public IsNotTeamLeaderException(Throwable cause) {
        super(ErrorDetail.IS_NOT_TEAM_LEADER, cause);
    }

}