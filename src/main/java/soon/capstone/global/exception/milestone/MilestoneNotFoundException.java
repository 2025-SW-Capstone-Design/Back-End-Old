package soon.capstone.global.exception.milestone;

import soon.capstone.global.exception.RootException;
import soon.capstone.global.exception.dto.ErrorDetail;

public class MilestoneNotFoundException extends RootException {
    public MilestoneNotFoundException() {
        super(ErrorDetail.MILESTONE_NOT_FOUND);
    }

    public MilestoneNotFoundException(Throwable cause) {
        super(ErrorDetail.MILESTONE_NOT_FOUND, cause);
    }
}
