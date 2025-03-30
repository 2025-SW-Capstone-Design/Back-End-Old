package soon.capstone.global.exception.issue.template;

import lombok.Getter;
import soon.capstone.global.exception.RootException;
import soon.capstone.global.exception.dto.ErrorDetail;

@Getter
public class AlreadyIssueTemplateException extends RootException {

    public AlreadyIssueTemplateException() {
        super(ErrorDetail.ISSUE_TEMPLATE_ALREADY_EXISTS);
    }

    public AlreadyIssueTemplateException(Throwable cause) {
        super(ErrorDetail.ISSUE_TEMPLATE_ALREADY_EXISTS, cause);
    }

}