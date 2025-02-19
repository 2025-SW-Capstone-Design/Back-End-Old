package soon.capstone.global.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
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

        return ResponseEntity.status(statusCode).body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> exception(Exception e) {
        log.error("예외발생: ", e);

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
            .message(e.getMessage())
            .build();

        for (FieldError fieldError : e.getFieldErrors()) {
            response.addValidation(fieldError.getField(), fieldError.getDefaultMessage());
        }

        return ResponseEntity.status(statusCode).body(response);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ErrorResponse> missingRequestParameterHandler(MissingServletRequestParameterException e) {
        log.warn("필수 파라미터가 누락되었습니다: {}", e.getParameterName());

        int statusCode = e.getStatusCode().value();
        String errorMessage = e.getParameterName() + "는 필수입니다.";
        ErrorResponse response = ErrorResponse.builder()
            .status(statusCode)
            .message(errorMessage)
            .build();

        return ResponseEntity.status(statusCode).body(response);
    }

}