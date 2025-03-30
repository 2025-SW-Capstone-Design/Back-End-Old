package soon.capstone.global.exception.github;

import lombok.Getter;
import soon.capstone.global.exception.RootException;
import soon.capstone.global.exception.dto.ErrorDetail;

@Getter
public class GithubIssueLabelNotFoundException extends RootException {

    public GithubIssueLabelNotFoundException() {
        super(ErrorDetail.GITHUB_ISSUE_LABEL_NOT_FOUND);
    }

    public GithubIssueLabelNotFoundException(Throwable cause) {
        super(ErrorDetail.GITHUB_ISSUE_LABEL_NOT_FOUND, cause);
    }

}