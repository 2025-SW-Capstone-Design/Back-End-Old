package soon.capstone.global.exception.dto.response;

import lombok.Builder;

import java.util.HashMap;
import java.util.Map;

/**
 * (example)
 * {
 * "code": "400",
 * "message": "잘못된 요청입니다.",
 * "validation": {
 * "title": "값을 입력해주세요"
 * }
 * }
 */

public record ErrorResponse(

    int status,
    String message,
    Map<String, String> validation

) {

    @Builder
    public ErrorResponse(int status, String message, Map<String, String> validation) {
        this.status = status;
        this.message = message;
        this.validation = getValidation(validation);
    }

    public void addValidation(String fieldName, String errorMessage) {
        this.validation.put(fieldName, errorMessage);
    }

    private Map<String, String> getValidation(Map<String, String> validation) {
        if (validation != null) {
            return validation;
        }
        return new HashMap<>();
    }

}