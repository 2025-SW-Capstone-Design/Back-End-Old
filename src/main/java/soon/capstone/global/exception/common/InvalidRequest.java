package soon.capstone.global.exception.common;

import lombok.Getter;
import soon.capstone.global.exception.RootException;
import soon.capstone.global.exception.dto.ErrorDetail;

@Getter
public class InvalidRequest extends RootException {

    public InvalidRequest() {
        super(ErrorDetail.INVALID_REQUEST);
    }

    public InvalidRequest(String fieldName, String message) {
        super(ErrorDetail.INVALID_REQUEST); // 에러의 전체적 유형
        addValidation(fieldName, message); // 구체적 에러 메시지
    }

    public InvalidRequest(Throwable cause) {
        super(ErrorDetail.INVALID_REQUEST, cause);
    }

}