package soon.capstone.global.exception.team;

import soon.capstone.global.exception.RootException;

import static soon.capstone.global.exception.dto.ErrorDetail.TEAM_ALREADY_EXISTS;

public class TeamAlreadyExistsException extends RootException {

    public TeamAlreadyExistsException() {
        super(TEAM_ALREADY_EXISTS);
    }

    public TeamAlreadyExistsException(Throwable cause) {
        super(TEAM_ALREADY_EXISTS, cause);
    }

}