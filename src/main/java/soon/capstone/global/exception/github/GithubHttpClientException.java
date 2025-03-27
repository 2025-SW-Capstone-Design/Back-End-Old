package soon.capstone.global.exception.github;

import lombok.Getter;
import soon.capstone.global.exception.RootException;
import soon.capstone.global.exception.dto.ErrorDetail;

@Getter
public class GithubHttpClientException extends RootException {
    public GithubHttpClientException() {
        super(ErrorDetail.GITHUB_HTTP_CLIENT_ERROR);
    }

    public GithubHttpClientException(Throwable cause) {
        super(ErrorDetail.GITHUB_HTTP_CLIENT_ERROR, cause);
    }
}
