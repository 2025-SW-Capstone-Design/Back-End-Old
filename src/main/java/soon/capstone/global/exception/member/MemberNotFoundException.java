package soon.capstone.global.exception.member;

import lombok.Getter;
import soon.capstone.global.exception.RootException;
import soon.capstone.global.exception.dto.ErrorDetail;

@Getter
public class MemberNotFoundException extends RootException {

    public MemberNotFoundException() {
        super(ErrorDetail.MEMBER_NOT_FOUND);
    }

    public MemberNotFoundException(Throwable cause) {
        super(ErrorDetail.MEMBER_NOT_FOUND, cause);
    }

}