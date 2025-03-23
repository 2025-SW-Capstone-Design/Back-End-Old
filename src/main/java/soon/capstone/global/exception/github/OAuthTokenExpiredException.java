package soon.capstone.global.exception.github;

import lombok.Getter;
import soon.capstone.global.exception.RootException;
import soon.capstone.global.exception.dto.ErrorDetail;

@Getter
public class OAuthTokenExpiredException extends RootException {

    public OAuthTokenExpiredException() {
        super(ErrorDetail.OAUTH_TOKEN_EXPIRED);
    }

    public OAuthTokenExpiredException(Throwable cause) {
        super(ErrorDetail.OAUTH_TOKEN_EXPIRED, cause);
    }

}