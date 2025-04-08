package soon.capstone.global.exception.milestone;

import soon.capstone.global.exception.RootException;
import soon.capstone.global.exception.dto.ErrorDetail;

public class MilestoneInvalidDateException extends RootException {
    public MilestoneInvalidDateException() {
        super(ErrorDetail.MILESTONE_INVALID_DATE);
    }

    public MilestoneInvalidDateException(Throwable cause) {
        super(ErrorDetail.MILESTONE_INVALID_DATE, cause);
    }
}
