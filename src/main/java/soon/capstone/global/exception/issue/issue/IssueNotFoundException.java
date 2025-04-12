package soon.capstone.global.exception.issue.issue;

import lombok.Getter;
import soon.capstone.global.exception.RootException;
import soon.capstone.global.exception.dto.ErrorDetail;

@Getter
public class IssueNotFoundException extends RootException {

    public IssueNotFoundException() {
        super(ErrorDetail.ISSUE_NOT_FOUND);
    }

    public IssueNotFoundException(Throwable cause) {
        super(ErrorDetail.ISSUE_NOT_FOUND, cause);
    }

}