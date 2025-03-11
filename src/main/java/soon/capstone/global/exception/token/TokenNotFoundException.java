package soon.capstone.global.exception.token;

import lombok.Getter;
import soon.capstone.global.exception.RootException;
import soon.capstone.global.exception.dto.ErrorDetail;

@Getter
public class TokenNotFoundException extends RootException {

    public TokenNotFoundException() {
        super(ErrorDetail.TOKEN_NOT_FOUND);
    }

    public TokenNotFoundException(Throwable cause) {
        super(ErrorDetail.TOKEN_NOT_FOUND, cause);
    }

}