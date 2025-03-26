package soon.capstone.global.exception.team;

import soon.capstone.global.exception.RootException;
import soon.capstone.global.exception.dto.ErrorDetail;

public class TeamNotAuthorizedException extends RootException {

    public TeamNotAuthorizedException() {
        super(ErrorDetail.TEAM_NOT_AUTHORIZED);
    }

    public TeamNotAuthorizedException(Throwable cause) {
        super(ErrorDetail.TEAM_NOT_AUTHORIZED, cause);
    }

}