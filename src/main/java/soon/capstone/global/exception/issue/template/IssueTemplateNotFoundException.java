package soon.capstone.global.exception.issue.template;

import lombok.Getter;
import soon.capstone.global.exception.RootException;
import soon.capstone.global.exception.dto.ErrorDetail;

@Getter
public class IssueTemplateNotFoundException extends RootException {

    public IssueTemplateNotFoundException() {
        super(ErrorDetail.ISSUE_TEMPLATE_NOT_FOUND);
    }

    public IssueTemplateNotFoundException(Throwable cause) {
        super(ErrorDetail.ISSUE_TEMPLATE_NOT_FOUND, cause);
    }

}