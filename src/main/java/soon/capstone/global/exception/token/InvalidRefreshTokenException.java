package soon.capstone.global.exception.token;

import lombok.Getter;
import soon.capstone.global.exception.RootException;
import soon.capstone.global.exception.dto.ErrorDetail;

@Getter
public class InvalidRefreshTokenException extends RootException {

    public InvalidRefreshTokenException() {
        super(ErrorDetail.INVALID_TOKEN);
    }

    public InvalidRefreshTokenException(Throwable cause) {
        super(ErrorDetail.INVALID_TOKEN, cause);
    }

}