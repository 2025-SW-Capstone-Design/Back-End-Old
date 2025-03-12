package soon.capstone.global.exception.teammember;

import lombok.Getter;
import soon.capstone.global.exception.RootException;
import soon.capstone.global.exception.dto.ErrorDetail;

@Getter
public class TeamMemberNotFoundException extends RootException {

    public TeamMemberNotFoundException() {
        super(ErrorDetail.TEAM_MEMBER_NOT_FOUND);
    }

    public TeamMemberNotFoundException(Throwable cause) {
        super(ErrorDetail.TEAM_MEMBER_NOT_FOUND, cause);
    }

}
