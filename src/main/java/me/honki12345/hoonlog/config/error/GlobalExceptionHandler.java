package me.honki12345.hoonlog.config.error;

import me.honki12345.hoonlog.config.error.exception.DuplicateUserAccountException;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.StringJoiner;

@RestControllerAdvice
public class GlobalExceptionHandler {
    public static final String DELIMITER = ", ";

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException ex) {
        String message = createValidationExceptionMessage(ex);
        ErrorResponse errorResponse = ErrorResponse.of(ErrorCode.INVALID_INPUT_VALUE, message);
        return new ResponseEntity<>(errorResponse, ErrorCode.INVALID_INPUT_VALUE.getStatus());
    }

    @ExceptionHandler(DuplicateUserAccountException.class)
    public ResponseEntity<ErrorResponse> handleDuplicationException(DuplicateUserAccountException ex) {
        return createResponseEntityByException(ex);
    }

    private static ResponseEntity<ErrorResponse> createResponseEntityByException(DuplicateUserAccountException ex) {
        ErrorCode errorCode = ex.getErrorCode();
        String message = ex.getMessage();
        ErrorResponse errorResponse = ErrorResponse.of(errorCode, message);
        ResponseEntity<ErrorResponse> responseEntity = new ResponseEntity<>(errorResponse, errorCode.getStatus());
        return responseEntity;
    }

    private String createValidationExceptionMessage(MethodArgumentNotValidException ex) {
        StringJoiner sj = new StringJoiner(DELIMITER);
        BindingResult bindingResult = ex.getBindingResult();
        for (FieldError fieldError : bindingResult.getFieldErrors()) {
            sj.add(fieldError.getDefaultMessage());
        }
        return sj.toString();
    }
}
