package soon.capstone.global.exception.github;

import lombok.Getter;
import soon.capstone.global.exception.RootException;
import soon.capstone.global.exception.dto.ErrorDetail;

@Getter
public class GithubMemberEmailNotFoundException extends RootException {

    public GithubMemberEmailNotFoundException() {
        super(ErrorDetail.GITHUB_MEMBER_EMAIL_NOT_FOUND);
    }

    public GithubMemberEmailNotFoundException(Throwable cause) {
        super(ErrorDetail.GITHUB_MEMBER_EMAIL_NOT_FOUND, cause);
    }

}