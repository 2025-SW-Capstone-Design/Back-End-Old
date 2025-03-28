package soon.capstone.global.exception.issue.label;

import lombok.Getter;
import soon.capstone.global.exception.RootException;
import soon.capstone.global.exception.dto.ErrorDetail;

@Getter
public class AlreadyIssueLabelException extends RootException {

    public AlreadyIssueLabelException() {
        super(ErrorDetail.ISSUE_LABEL_ALREADY_EXISTS);
    }

    public AlreadyIssueLabelException(Throwable cause) {
        super(ErrorDetail.ISSUE_LABEL_ALREADY_EXISTS, cause);
    }

}