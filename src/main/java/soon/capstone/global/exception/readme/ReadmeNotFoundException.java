package soon.capstone.global.exception.readme;

import lombok.Getter;
import soon.capstone.global.exception.RootException;
import soon.capstone.global.exception.dto.ErrorDetail;

@Getter
public class ReadmeNotFoundException extends RootException {

    public ReadmeNotFoundException() {
        super(ErrorDetail.READEME_NOT_FOUND);
    }

    public ReadmeNotFoundException(Throwable cause) {
        super(ErrorDetail.READEME_NOT_FOUND, cause);
    }

}