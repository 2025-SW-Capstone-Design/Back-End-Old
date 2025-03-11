package soon.capstone.global.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import soon.capstone.global.exception.dto.response.ErrorResponse;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

@Slf4j
@RestControllerAdvice
public class GlobalRestExceptionHandler {

    @ExceptionHandler(RootException.class)
    public ResponseEntity<ErrorResponse> rootException(RootException e) {
        int statusCode = e.getErrorDetail().getStatus();

        ErrorResponse response = ErrorResponse.builder()
            .status(statusCode)
            .message(e.getMessage())
            .validation(e.getValidation())
            .build();

        log.error("RootException 발생: 상태 코드 - {}, 메시지 - {}, 상세 정보 - {}", statusCode, e.getMessage(), e.getErrorDetail(), e);

        return ResponseEntity.status(statusCode).body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> exception(Exception e) {
        log.error("예외 발생: {}", e.getMessage(), e);

        int statusCode = INTERNAL_SERVER_ERROR.value();
        ErrorResponse response = ErrorResponse.builder()
            .status(statusCode)
            .message(e.getMessage())
            .build();

        return ResponseEntity.status(statusCode).body(response);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> invalidRequestHandler(MethodArgumentNotValidException e) {
        int statusCode = e.getStatusCode().value();
        ErrorResponse response = ErrorResponse.builder()
            .status(statusCode)
            .message("잘못된 요청입니다.")
            .build();

        e.getFieldErrors().forEach(fieldError -> {
            String field = fieldError.getField();
            String errorMessage = fieldError.getDefaultMessage();
            log.warn("유효성 검사 실패 - 필드: {}, 메시지: {}", field, errorMessage);
            response.addValidation(field, errorMessage);
        });

        return ResponseEntity.status(statusCode).body(response);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ErrorResponse> missingRequestParameterHandler(MissingServletRequestParameterException e) {
        log.warn("필수 요청 파라미터 누락: {}", e.getParameterName());

        int statusCode = e.getStatusCode().value();
        String errorMessage = e.getParameterName() + "는 필수입니다.";
        ErrorResponse response = ErrorResponse.builder()
            .status(statusCode)
            .message(errorMessage)
            .build();

        return ResponseEntity.status(statusCode).body(response);
    }

}