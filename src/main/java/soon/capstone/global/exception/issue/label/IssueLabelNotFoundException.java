package soon.capstone.global.exception.issue.label;

import lombok.Getter;
import soon.capstone.global.exception.RootException;
import soon.capstone.global.exception.dto.ErrorDetail;

@Getter
public class IssueLabelNotFoundException extends RootException {

    public IssueLabelNotFoundException() {
        super(ErrorDetail.ISSUE_LABEL_NOT_FOUND);
    }

    public IssueLabelNotFoundException(Throwable cause) {
        super(ErrorDetail.ISSUE_LABEL_NOT_FOUND, cause);
    }

}