package soon.capstone.global.exception.team;

import lombok.Getter;
import soon.capstone.global.exception.RootException;
import soon.capstone.global.exception.dto.ErrorDetail;

@Getter
public class TeamNotFoundException extends RootException {

    public TeamNotFoundException() {
        super(ErrorDetail.TEAM_NOT_FOUND);
    }

    public TeamNotFoundException(Throwable cause) {
        super(ErrorDetail.TEAM_NOT_FOUND, cause);
    }

}