package soon.capstone.global.exception.teammember;

import lombok.Getter;
import soon.capstone.global.exception.RootException;
import soon.capstone.global.exception.dto.ErrorDetail;

@Getter
public class TeamMemberPositionNotFoundException extends RootException {

    public TeamMemberPositionNotFoundException() {
        super(ErrorDetail.TEAM_MEMBER_NOT_FOUND);
    }

    public TeamMemberPositionNotFoundException(Throwable cause) {
        super(ErrorDetail.TEAM_MEMBER_NOT_FOUND, cause);
    }

}