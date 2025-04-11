package soon.capstone.global.exception.milestone;

import soon.capstone.global.exception.RootException;
import soon.capstone.global.exception.dto.ErrorDetail;

public class MilestoneDuplicateTitleException extends RootException {
    public MilestoneDuplicateTitleException() {
        super(ErrorDetail.MILESTONE_DUPLICATE_TITLE);
    }

    public MilestoneDuplicateTitleException(Throwable cause) {
        super(ErrorDetail.MILESTONE_DUPLICATE_TITLE, cause);
    }
}
