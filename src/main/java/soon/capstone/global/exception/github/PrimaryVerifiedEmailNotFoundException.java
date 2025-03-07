package soon.capstone.global.exception.github;

import lombok.Getter;
import soon.capstone.global.exception.RootException;
import soon.capstone.global.exception.dto.ErrorDetail;

@Getter
public class PrimaryVerifiedEmailNotFoundException extends RootException {

    public PrimaryVerifiedEmailNotFoundException() {
        super(ErrorDetail.PRIMARY_VERIFIED_EMAIL_NOT_FOUND);
    }

    public PrimaryVerifiedEmailNotFoundException(Throwable cause) {
        super(ErrorDetail.PRIMARY_VERIFIED_EMAIL_NOT_FOUND, cause);
    }

}