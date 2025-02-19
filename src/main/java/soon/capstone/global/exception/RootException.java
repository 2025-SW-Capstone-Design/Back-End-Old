package soon.capstone.global.exception;

import lombok.Getter;
import soon.capstone.global.exception.dto.ErrorDetail;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Getter
public abstract class RootException extends RuntimeException {

    private final Map<String, String> validation;
    private final ErrorDetail errorDetail;

    public RootException(ErrorDetail errorDetail) {
        super(errorDetail.getMessage());
        this.errorDetail = errorDetail;
        this.validation = new HashMap<>();
    }

    public RootException(ErrorDetail errorDetail, Throwable cause) {
        super(errorDetail.getMessage(), cause);
        this.errorDetail = errorDetail;
        this.validation = new HashMap<>();
    }

    public void addValidation(String fieldName, String message) {
        validation.put(fieldName, message);
    }

    public Map<String, String> getValidation() {
        return Collections.unmodifiableMap(validation);
    }

}