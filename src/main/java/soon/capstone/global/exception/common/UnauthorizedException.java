package soon.capstone.global.exception.common;

import soon.capstone.global.exception.RootException;
import soon.capstone.global.exception.dto.ErrorDetail;

public class UnauthorizedException extends RootException {

    public UnauthorizedException() {
        super(ErrorDetail.UNAUTHORIZED);
    }

    public UnauthorizedException(Throwable cause) {
        super(ErrorDetail.UNAUTHORIZED, cause);
    }

}