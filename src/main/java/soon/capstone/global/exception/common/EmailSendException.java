package soon.capstone.global.exception.common;

import lombok.Getter;
import soon.capstone.global.exception.RootException;
import soon.capstone.global.exception.dto.ErrorDetail;

@Getter
public class EmailSendException extends RootException {

    public EmailSendException() {
        super(ErrorDetail.EMAIL_SEND);
    }

    public EmailSendException(Throwable cause) {
        super(ErrorDetail.EMAIL_SEND, cause);
    }

}